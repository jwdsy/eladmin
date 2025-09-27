package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/7/12
 */
@Data
public class GetFirstLabelRequest implements Serializable {
    /**
     * 标签等级 1 一级，2 二级
     */
    private Integer	labelLevel;

    /**
     * 一级标签ID
     */
    private Long	firstLabelId;
}
