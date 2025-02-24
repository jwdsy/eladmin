package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/17
 */
@Data
public class DeleteItemLabelRequest implements Serializable {
    /**
     * 标签ID
     */
    private List<Long> labelIdList;

    /**
     * 用户ID
     */
    private Long	userId;
}
