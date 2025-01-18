package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class CreateItemDetailResponse implements Serializable {
    /**
     * 商品ID
     */
    private Long	itemId;
}
