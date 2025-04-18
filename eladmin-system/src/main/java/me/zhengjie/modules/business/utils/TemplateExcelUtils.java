package me.zhengjie.modules.business.utils;

import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.WriteCellData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateExcelUtils {


    public static WriteCellData<Void> imageCells(byte[] bytes) throws IOException {

        WriteCellData<Void> writeCellData = new WriteCellData<>();

        // 可以放入多个图片
        List<ImageData> imageDataList = new ArrayList<>();
        writeCellData.setImageDataList(imageDataList);

        ImageData imageData = new ImageData();
        imageDataList.add(imageData);
        // 设置图片
        imageData.setImage(bytes);
        // 图片类型
        //imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);

        // 上 右 下 左 需要留空，这个类似于 css 的 margin；
        // 这里实测 不能设置太大 超过单元格原始大小后 打开会提示修复。暂时未找到很好的解法。
        imageData.setTop(0);
        imageData.setRight(0);
        imageData.setBottom(0);
        imageData.setLeft(0);

        // * 设置图片的位置。Relative表示相对于当前的单元格index。first是左上点，last是对角线的右下点，这样确定一个图片的位置和大小。
        // 目前填充模板的图片变量是images，index：row=CELL_COUNT,column=0。所有图片都基于此位置来设置相对位置
        // 第1张图片相对位置
        imageData.setRelativeFirstRowIndex(0);
        imageData.setRelativeFirstColumnIndex(0);
        imageData.setRelativeLastRowIndex(0);
        imageData.setRelativeLastColumnIndex(0);

        return writeCellData;
    }

}