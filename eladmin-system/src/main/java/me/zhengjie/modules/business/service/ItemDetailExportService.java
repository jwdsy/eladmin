package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonParser;
import com.spire.xls.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.GetItemDetailListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
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

    public void exportItemDetailList(GetItemDetailListRequest request, OutputStream out) throws Exception {
        List<GetItemDetailListResponse.ItemModel> itemModelList = getItemModelList(request);
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

    private List<GetItemDetailListResponse.ItemModel> getItemModelList(GetItemDetailListRequest request) {
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = itemDetailService.getItemListQueryWrapper(request);
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);
        return itemDetailService.getItemModelList(recordList, IsTypeInteger.YES.getCode());
    }

    private void exportExcel(List<GetItemDetailListResponse.ItemModel> itemModelList, Workbook workbook) throws IOException {
        //获取第一个工作表
        Worksheet sheet = workbook.getWorksheets().get(0);
        CountDownLatch countDownLatch = new CountDownLatch(itemModelList.size());
        ExecutorService executor = Executors.newFixedThreadPool(6);
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
                    setPicCell(sheet, rowIndex, finalColumnIndex, itemModel.getItemPic());
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

    private void setPicCell(Worksheet sheet, int rowIndex, int columnIndex, String picUrl) {
        CellRange cell = sheet.getCellRange(rowIndex, columnIndex);
        cell.borderAround(LineStyleType.Thin);
        if(null == picUrl){
            // todo:增加日志
            return;
        }
        // 设置列宽
        sheet.setColumnWidth(columnIndex, COLUMN_WIDTH);
        BufferedImage targetImage = downloadImage(picUrl);
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
        // 压缩图片，值为40时，25张图片导出后文件大小为3.6M
        excelPicture.compress(40);
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

    private BufferedImage downloadImage(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            try (InputStream inputStream = connection.getInputStream()){
                BufferedImage image = ImageIO.read(inputStream);
                return image;
            }

        } catch (Exception e) {
            return null;
        }finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
    }
}
