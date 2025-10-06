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
     * 季度 1 春季，2 夏季，3 秋季，4 冬季
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

    // -------范围筛选-------------------------------------------------------------------------------------------------
    /**
     * 长度范围，null 是查全部
     */
    private Integer lengthMin;
    private Integer lengthMax;

    /**
     * 宽度范围，null 是查全部
     */
    private Integer widthMin;
    private Integer widthMax;

    /**
     * 高度范围，null 是查全部
     */
    private Integer heightMin;
    private Integer heightMax;


    // -------排序-----------------------------------------------------------------------------------------------------
    /**
     * 长度排序 1 降序；2 升序
     */
    private Integer lengthOrder;
    /**
     * 宽度排序 1 降序；2 升序
     */
    private Integer widthOrder;
    /**
     * 高度排序 1 降序；2 升序
     */
    private Integer heightOrder;
}
