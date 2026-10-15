package me.zhengjie.modules.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spire.xls.*;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.domain.FillData;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.GetItemDetailListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.utils.OkHttpUtils;
import me.zhengjie.modules.business.utils.TemplateExcelUtils;
import me.zhengjie.utils.RedisUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/25
 */
@Slf4j
@Service
public class ItemDetailExportService {
    // 第一行内容行号
    public static final Integer FIRST_ROW_INDEX = 11;
    // 图片宽度
    private static final Integer ROW_HEIGHT = 170;
    private static final Integer COLUMN_WIDTH = 25;
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;
    @Autowired
    private ItemDetailService itemDetailService;
    @Autowired
    private RedisUtils redisUtils;

    public void exportItemDetailList(GetItemDetailListRequest request, OutputStream out) throws Exception {
        List<GetItemDetailListResponse.ItemModel> itemModelList = getItemModelList(request);
        exportItemDetailList(itemModelList, out);
    }

    public void exportItemDetailList(List<GetItemDetailListResponse.ItemModel> itemModelList, OutputStream out) throws Exception {
        if(CollectionUtils.isEmpty(itemModelList)){
            return;
        }
        // 加载示例文档
        String templateFileUrl = "http://8.130.87.100:8009/template/template.xlsx";
        Workbook workbook = downloadTemplateFile(templateFileUrl);
        if(null == workbook){
            return;
        }

        try {
            exportExcel(itemModelList, workbook);
            workbook.saveToStream(out);
        }finally {
            if(null != out){
                out.close();
            }
            workbook.dispose();
        }
    }

    public void exportItemDetailList2(List<GetItemDetailListResponse.ItemModel> itemModelList, ServletOutputStream out) {
        if(CollectionUtils.isEmpty(itemModelList)){
            return;
        }
        
        long startTime = System.currentTimeMillis();
        log.info("开始导出商品列表，共{}条数据", itemModelList.size());
        
        try {
            // 1. 从本地加载模板文件
            String templateFileName = "template/exportTemplate.xlsx";
            InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templateFileName);
            
            if (templateStream == null) {
                throw new RuntimeException("模板文件不存在: " + templateFileName);
            }
            
            // 2. 并发下载所有图片（提高效率）
            downloadImagesInParallel(itemModelList);
            
            // 3. 写入 Excel
            try (ExcelWriter excelWriter = EasyExcel.write(out)
                    .withTemplate(templateStream)
                    .registerWriteHandler(new CustomCellWriteHandler()) // 自定义处理器设置行高列宽
                    .build()) {
                
                WriteSheet writeSheet = EasyExcel.writerSheet().build();
                FillData fillData = FillData.getInstance();
                
                // 直接写入数据
                excelWriter.fill(fillData, writeSheet);
                excelWriter.fill(itemModelList, writeSheet);
                excelWriter.finish();
            } finally {
                // 关闭模板流
                if (templateStream != null) {
                    templateStream.close();
                }
            }
            
            long endTime = System.currentTimeMillis();
            log.info("导出完成，耗时: {}ms", (endTime - startTime));
            
        } catch (Exception e) {
            log.error("导出失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 并发下载图片（提高效率）
     */
    private void downloadImagesInParallel(List<GetItemDetailListResponse.ItemModel> itemModelList) {
        int threadPoolSize = Math.min(10, itemModelList.size()); // 最多10个线程
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(itemModelList.size());
        
        long downloadStart = System.currentTimeMillis();
        
        for (GetItemDetailListResponse.ItemModel itemModel : itemModelList) {
            executor.execute(() -> {
                try {
                    byte[] bytes = downloadImageWithCache(itemModel.getItemPic(), itemModel.getItemNo());
                    if (bytes != null) {
                        WriteCellData<Void> cellData = TemplateExcelUtils.imageCells(bytes);
                        itemModel.setItemPicture(cellData);
                    }
                } catch (Exception e) {
                    log.error("下载图片失败 itemNo:{}", itemModel.getItemNo(), e);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(); // 等待所有图片下载完成
            long downloadEnd = System.currentTimeMillis();
            log.info("图片下载完成，耗时: {}ms", (downloadEnd - downloadStart));
        } catch (InterruptedException e) {
            log.error("等待图片下载被中断", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * 带缓存的图片下载（提高效率）
     */
    private byte[] downloadImageWithCache(String imageUrl, String itemNo) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        String redisKey = "image:bytes:" + itemNo;
        
        try {
            // 1. 先查缓存
            Object cacheObj = redisUtils.get(redisKey);
            if (cacheObj instanceof String) {
                String base64Str = (String) cacheObj;
                return Base64.decodeBase64(base64Str);
            }
        } catch (Exception e) {
            log.warn("读取图片缓存失败 redisKey:{}", redisKey, e);
        }
        
        // 2. 下载图片
        try {
            byte[] bytes = OkHttpUtils.downloadImage(imageUrl);
            if (bytes != null && bytes.length > 0) {
                // 3. 写入缓存（2小时过期）
                String base64Str = Base64.encodeBase64String(bytes);
                redisUtils.set(redisKey, base64Str, 7200);
                return bytes;
            }
        } catch (Exception e) {
            log.error("下载图片失败 imageUrl:{}, itemNo:{}", imageUrl, itemNo, e);
        }
        
        return null;
    }
    
    /**
     * 自定义单元格写入处理器 - 设置行高和列宽
     */
    private static class CustomCellWriteHandler implements com.alibaba.excel.write.handler.CellWriteHandler {
        // 数据行开始的行号（从0开始计数，根据模板实际情况调整）
        private static final int DATA_START_ROW_INDEX = 10; // 第12行开始是数据行
        // 图片列的索引（根据实际模板调整）
        private static final int IMAGE_COLUMN_INDEX = 2;
        // 行高和列宽设置为相同值（单位：像素转换为 Excel 单位）
        private static final short ROW_HEIGHT = 170 * 20; // Excel 行高单位是 1/20 点
        private static final int COLUMN_WIDTH = 25 * 256; // Excel 列宽单位是 1/256 字符宽度

        @Override
        public void afterCellDispose(com.alibaba.excel.write.metadata.holder.WriteSheetHolder writeSheetHolder,
                                     com.alibaba.excel.write.metadata.holder.WriteTableHolder writeTableHolder,
                                     List<com.alibaba.excel.metadata.data.WriteCellData<?>> cellDataList,
                                     org.apache.poi.ss.usermodel.Cell cell,
                                     com.alibaba.excel.metadata.Head head,
                                     Integer relativeRowIndex,
                                     Boolean isHead) {
            // 获取当前行号
            int currentRowIndex = cell.getRowIndex();
            
            // 只对数据行设置行高（从 DATA_START_ROW_INDEX 开始）
            if (currentRowIndex >= DATA_START_ROW_INDEX) {
                org.apache.poi.ss.usermodel.Row row = cell.getRow();
                row.setHeight(ROW_HEIGHT);
            }
            
            // 设置图片列的列宽
            if (cell.getColumnIndex() == IMAGE_COLUMN_INDEX) {
                org.apache.poi.ss.usermodel.Sheet sheet = writeSheetHolder.getSheet();
                sheet.setColumnWidth(IMAGE_COLUMN_INDEX, COLUMN_WIDTH);
            }
        }
    }

    private List<GetItemDetailListResponse.ItemModel> getItemModelList(GetItemDetailListRequest request) {
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = itemDetailService.getItemListQueryWrapper(request);
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);
        return itemDetailService.getItemModelList(recordList);
    }

    private void exportExcel(List<GetItemDetailListResponse.ItemModel> itemModelList, Workbook workbook) throws IOException {
        //获取第一个工作表
        Worksheet sheet = workbook.getWorksheets().get(0);
        CountDownLatch countDownLatch = new CountDownLatch(itemModelList.size());
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CellRange cell = sheet.getCellRange(7, 17);
        cell.setText(DateUtil.format(new Date(), "yyyy/MM/dd"));

        for (int i = 0; i < itemModelList.size(); i++) {
            GetItemDetailListResponse.ItemModel itemModel = itemModelList.get(i);
            final int rowIndex = FIRST_ROW_INDEX + i;
            // 设置行高
            sheet.setRowHeight(rowIndex, ROW_HEIGHT);

            int columnIndex = 0;
            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemNo());

            columnIndex++;
            final int finalColumnIndex = columnIndex;
            executor.execute(() -> {
                try {
                    setPicCell(sheet, rowIndex, finalColumnIndex, itemModel.getItemPic(), itemModel.getItemNo());
                }catch (Exception e){
                    log.error("导出图片失败", e);
                }finally {
                    countDownLatch.countDown();
                }
            });
//            setPicCell(sheet, rowIndex, columnIndex, itemModel.getItemPic());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getDescription());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getDeliveryPort());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemLength());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemWidth());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemHeight());

            columnIndex++;
            if(null != itemModel.getInnerBox()){
                setContentCell(sheet, rowIndex, columnIndex, String.valueOf(itemModel.getInnerBox()));
            }

            columnIndex++;
            if(null != itemModel.getOuterCtn()){
                setContentCell(sheet, rowIndex, columnIndex, String.valueOf(itemModel.getOuterCtn()));
            }

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getWeightPieces());

            columnIndex++;
            if(null != itemModel.getMininumOrderQuantity()){
                setContentCell(sheet, rowIndex, columnIndex, String.valueOf(itemModel.getMininumOrderQuantity()));
            }

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getCartonLength());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getCartonWidth());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getCartonHeight());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getCbmPerCTN());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getUnitPrice4Dollar());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getQtyIn20GP());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getQtyIn40GP());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getQtyIn40HC());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getUnitPrice());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getFactoryName());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemRemark());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getPickRemark());
        }
        try {
            countDownLatch.await();
        }catch (Exception e){
            log.error("导出图片失败", e);
        }
//        countDownLatch.await();
    }

    private void setContentCell(Worksheet sheet, int rowIndex, int columnIndex, String content) {
        CellRange cell = sheet.getCellRange(rowIndex, columnIndex);
        // 设置单元格样式的对齐方式为居中
        cell.getStyle().setHorizontalAlignment(HorizontalAlignType.Center);
        cell.getStyle().setVerticalAlignment(VerticalAlignType.Center);
        // 设置为文本格式
        cell.setNumberFormat("@");
        cell.borderAround(LineStyleType.Thin);
        if(null == content){
            return;
        }
        cell.setValue(content);
    }

    private void setPicCell(Worksheet sheet, int rowIndex, int columnIndex, String picUrl, String itemNo) {
        CellRange cell = sheet.getCellRange(rowIndex, columnIndex);
        cell.borderAround(LineStyleType.Thin);
        if(null == picUrl){
            // todo:增加日志
            return;
        }
        // 设置列宽
        sheet.setColumnWidth(columnIndex, COLUMN_WIDTH);
        long start = System.currentTimeMillis();
        BufferedImage targetImage = downloadImage(picUrl, itemNo);
        long end = System.currentTimeMillis();
        log.info(itemNo + " download time:" + (end - start) + "ms");
        if(null == targetImage){
            // todo:增加日志
            return;
        }
        ExcelPicture excelPicture = sheet.getPictures().add(rowIndex, columnIndex, targetImage);
        int originalWidth = excelPicture.getWidth();
        int originalHeight = excelPicture.getHeight();
        double aspectRatio = (double) originalHeight / originalWidth;
        int desiredWidth = (int) (ROW_HEIGHT / aspectRatio); // 保持纵横比
        excelPicture.setWidth(desiredWidth);
        excelPicture.setHeight(ROW_HEIGHT);
        // 恢复图片压缩
        long compressStart = System.currentTimeMillis();
        excelPicture.compress(40);
        long compressEnd = System.currentTimeMillis();
        log.info(itemNo + " compress time:" + (compressEnd - compressStart) + "ms");
        // // 使用 Thumbnailator 进行压缩（如需切换，取消注释下方代码）
        // try {
        //     BufferedImage compressedImage = Thumbnails.of(targetImage)
        //             .size(300, 300)
        //             .outputQuality(0.4f)
        //             .asBufferedImage();
        //     ExcelPicture excelPicture = sheet.getPictures().add(rowIndex, columnIndex, compressedImage);
        //     int originalWidth = excelPicture.getWidth();
        //     int originalHeight = excelPicture.getHeight();
        //     double aspectRatio = (double) originalHeight / originalWidth;
        //     int desiredWidth = (int) (ROW_HEIGHT / aspectRatio); // 保持纵横比
        //     excelPicture.setWidth(desiredWidth);
        //     excelPicture.setHeight(ROW_HEIGHT);
        // } catch (IOException e) {
        //     log.error("图片压缩失败 itemNo:{}", itemNo, e);
        // }
    }

    private Workbook downloadTemplateFile(String fileUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            /**
             * isTemplate：类型为 boolean。
             * 作用：指定是否将输入流中的内容作为模板加载。
             * 如果设置为 true，则表示将输入流中的 Excel 文件视为模板进行加载。这可能意味着会保留一些格式或结构信息，以便后续操作（如填充数据）。
             * 如果设置为 false，则表示正常加载 Excel 文件，不作为模板处理
             */
            try (InputStream inputStream = connection.getInputStream()){
                Workbook workbook = new Workbook();
                workbook.loadFromStream(inputStream, true);
                return workbook;
            }

        } catch (Exception e) {
            return null;
        }finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    private BufferedImage downloadImage(String imageUrl, String itemNo) {
        String redisKey = "image:" + itemNo;
        
        try {
            // 1. 先查缓存
            Object cacheObj = redisUtils.get(redisKey);
            if (cacheObj instanceof String) {
                String base64Str = (String) cacheObj;
                byte[] bytes = Base64.decodeBase64(base64Str);
                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                    BufferedImage image = ImageIO.read(inputStream);
                    if (image != null) {
                        return image;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("读取图片缓存失败 redisKey:{}", redisKey, e);
        }
        
        // 2. 下载图片（使用优化后的方法）
        return downloadImageWithRetry(imageUrl, itemNo, redisKey, 3);
    }
    
    /**
     * 带重试机制的图片下载（优化性能）
     * 
     * @param imageUrl 图片URL
     * @param itemNo 商品编号
     * @param redisKey Redis缓存键
     * @param maxRetries 最大重试次数
     * @return BufferedImage
     */
    private BufferedImage downloadImageWithRetry(String imageUrl, String itemNo, String redisKey, int maxRetries) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return downloadImageOptimized(imageUrl, itemNo, redisKey);
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    log.warn("下载图片失败，第{}次重试 itemNo:{}", attempt, itemNo);
                    try {
                        Thread.sleep(100 * attempt); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        log.error("下载图片失败，已重试{}次 imageUrl:{}, itemNo:{}", maxRetries, imageUrl, itemNo, lastException);
        return null;
    }
    
    /**
     * 优化后的图片下载方法
     * 
     * 优化点：
     * 1. 使用更大的缓冲区（8KB -> 16KB）
     * 2. 优化超时设置
     * 3. 添加 Keep-Alive 支持
     * 4. 预估容量避免扩容
     */
    private BufferedImage downloadImageOptimized(String imageUrl, String itemNo, String redisKey) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // 优化1：调整超时时间（根据实际网络情况）
            connection.setConnectTimeout(3000); // 连接超时 3秒
            connection.setReadTimeout(10000);   // 读取超时 10秒
            
            // 优化2：启用 Keep-Alive
            connection.setRequestProperty("Connection", "keep-alive");
            
            // 优化3：设置 User-Agent（某些服务器需要）
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            // 优化4：禁用缓存（确保获取最新图片）
            connection.setUseCaches(false);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP响应码: " + responseCode);
            }
            
            // 优化5：获取内容长度，预估容量避免扩容
            int contentLength = connection.getContentLength();
            ByteArrayOutputStream baos = contentLength > 0 
                ? new ByteArrayOutputStream(contentLength) 
                : new ByteArrayOutputStream(8192);
            
            try (InputStream inputStream = connection.getInputStream()) {
                // 优化6：使用更大的缓冲区（16KB）
                byte[] buffer = new byte[16384];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            }
            
            byte[] bytes = baos.toByteArray();
            
            // 写入缓存（异步，不阻塞主流程）
            asyncCacheImage(redisKey, bytes);
            
            // 转换为 BufferedImage
            try (InputStream imgInput = new ByteArrayInputStream(bytes)) {
                BufferedImage image = ImageIO.read(imgInput);
                if (image == null) {
                    throw new IOException("无法解析图片数据");
                }
                return image;
            }
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 异步缓存图片（不阻塞主流程）
     */
    private void asyncCacheImage(String redisKey, byte[] bytes) {
        // 使用线程池异步执行缓存操作
        CompletableFuture.runAsync(() -> {
            try {
                String base64Str = Base64.encodeBase64String(bytes);
                redisUtils.set(redisKey, base64Str, 7200); // 2小时
            } catch (Exception e) {
                log.warn("异步缓存图片失败 redisKey:{}", redisKey, e);
            }
        });
    }
}
