package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class CustomerPickItemRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 产品ID
     */
    private Long	itemId;
    /**
     * 是否喜欢 1 是，0 否
     */
    private Integer pickFlag;

}
