package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spire.xls.CellRange;
import com.spire.xls.ExcelPicture;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
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
    private static final Integer DESIRED_WIDTH = 170;
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
        for (int i = 0; i < itemModelList.size(); i++) {
            GetItemDetailListResponse.ItemModel itemModel = itemModelList.get(i);
            int rowIndex = FIRST_ROW_INDEX + i;
            // 设置行高
            sheet.setRowHeight(rowIndex, DESIRED_WIDTH);

            int columnIndex = 1;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getItemNo());

            columnIndex++;
            setPicCell(sheet, rowIndex, columnIndex, itemModel.getItemPic());

            columnIndex++;
            setContentCell(sheet, rowIndex, columnIndex, itemModel.getDescription());
        }
    }

    private void setContentCell(Worksheet sheet, int rowIndex, int columnIndex, String content) {
        if(null == content){
            return;
        }
        CellRange cell = sheet.getCellRange(rowIndex, columnIndex);
        cell.setValue(content);
    }

    private void setPicCell(Worksheet sheet, int rowIndex, int columnIndex, String picUrl) {
        if(null == picUrl){
            return;
        }
        // 设置列宽
        sheet.setColumnWidth(columnIndex, 25);
        BufferedImage targetImage = downloadImage(picUrl);
        ExcelPicture excelPicture = sheet.getPictures().add(rowIndex, columnIndex, targetImage);
        int originalWidth = excelPicture.getWidth();
        int originalHeight = excelPicture.getHeight();
        double aspectRatio = (double) originalWidth / originalHeight;
        int desiredHeight = (int) (DESIRED_WIDTH / aspectRatio); // 保持纵横比
        excelPicture.setWidth(DESIRED_WIDTH);
        excelPicture.setHeight(desiredHeight);
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
