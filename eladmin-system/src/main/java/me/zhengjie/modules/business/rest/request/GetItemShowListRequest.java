package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/10/6
 */
@Data
public class GetItemShowListRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

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
}
