package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Data
public class UpdateItemLabelStatusRequest implements Serializable {
    /**
     * 标签ID
     */
    private Long	labelId;
    /**
     * 标签状态 1 上线；2 下线
     */
    private Integer	labelStatus;

    /**
     * 用户ID
     */
    private Long	userId;
}
