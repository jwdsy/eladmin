package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class CustomerSubmitItemRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 商品ID列表
     */
    private List<Long> itemIdList;
}
