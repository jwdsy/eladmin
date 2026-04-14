package me.zhengjie.modules.biz.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ImageSearchResultDto implements Serializable {

    /**
     * 相似度（0-1，越高越相似）
     */
    private Double similarity;

    /**
     * 检索到的图片路径
     */
    private String sourceImagePath;

    /**
     * 检索到的图片文件名
     */
    private String sourceImageName;

    /**
     * 匹配到的商品信息（可能为空）
     */
    private BizItemBaseRecordDto item;
}

