package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/2/16
 */
@Data
public class GetSubmitRecordListRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 提交ID
     */
    private Date startDate;

    /**
     * 提交ID
     */
    private Date endDate;

    /**
     * 第几页，默认1
     */
    private Integer pageNo = 1;
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
}
