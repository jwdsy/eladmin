package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class GetDisplayItemListRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否喜欢 1 是，0 否
     */
    private Integer pickFlag;

    /**
     * 一级标签，null 是查全部
     */
    private Long firstLabelId;
    /**
     * 二级标签，，null 是查全部
     */
    private Long secondLabelId;

    /**
     * 年
     */
    private Integer year;

    /**
     * 季度
     */
    private Integer season;

    /**
     * 第几页，默认1
     */
    private Integer pageNo = 1;
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
}
