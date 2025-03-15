package me.zhengjie.modules.business.rest.request.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/3/10
 */
@Data
public class CompressPictureRequest implements Serializable {
    private String sourcePath;
    private Integer sideLength;  // 设置缩略图的宽度和高度 正方形
    private Float outputQuality; // 设置压缩质量（0.0f 到 1.0f）
}
