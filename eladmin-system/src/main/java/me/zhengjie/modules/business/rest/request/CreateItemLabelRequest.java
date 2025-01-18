package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Data
public class CreateItemLabelRequest implements Serializable {
    /**
     * 标签ID
     */
    private Long	labelId;
    /**
     * 标签名称
     */
    private String	labelName;
    /**
     * 标签等级 1 一级，2 二级
     */
    private Integer	labelLevel;
    /**
     * 一级标签ID
     */
    private Long	firstLabelId;
    /**
     * 标签描述
     */
    private String	description;

    /**
     * 用户ID
     */
    private Long	userId;
}
