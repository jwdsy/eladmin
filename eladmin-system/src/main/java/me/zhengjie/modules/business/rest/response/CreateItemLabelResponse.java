package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Data
public class CreateItemLabelResponse implements Serializable {
    /**
     * 标签ID
     */
    private Long	labelId;

    /**
     * 标签等级 1 一级，2 二级
     */
    private Integer	labelLevel;
}
