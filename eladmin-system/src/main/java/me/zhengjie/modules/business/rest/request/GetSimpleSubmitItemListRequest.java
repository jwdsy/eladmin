package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/17
 */
@Data
public class GetSimpleSubmitItemListRequest implements Serializable {

    /**
     * 提交ID
     */
    private Long submitId;

    /**
     * 第几页，默认1
     */
    private Integer pageNo = 1;
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
}
