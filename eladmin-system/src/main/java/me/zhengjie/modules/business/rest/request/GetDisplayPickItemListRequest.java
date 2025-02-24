package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/17
 */
@Data
public class GetDisplayPickItemListRequest extends GetItemDetailListRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long customerUserId;

}
