package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Data
public class UpdateItemStatusRequest implements Serializable {
    /**
     * 商品ID
     */
    private Long	itemId;

    /**
     * 产品状态 1 上线；2 下线
     */
    private Integer	itemStatus;

    /**
     * 用户ID
     */
    private Long	userId;
}
