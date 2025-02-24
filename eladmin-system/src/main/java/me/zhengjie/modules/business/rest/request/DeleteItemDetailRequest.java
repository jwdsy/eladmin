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
public class DeleteItemDetailRequest implements Serializable {
    /**
     * 商品ID
     */
    private List<Long> itemIdList;

    /**
     * 用户ID
     */
    private Long	userId;
}
