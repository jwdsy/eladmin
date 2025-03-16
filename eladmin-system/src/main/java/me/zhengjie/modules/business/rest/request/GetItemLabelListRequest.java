package me.zhengjie.modules.business.rest.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Data
public class GetItemLabelListRequest implements Serializable {
    private List<Long> labelIdList;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签等级 1 一级，2 二级
     */
    private Integer	labelLevel;

    /**
     * 一级标签ID
     */
    private Long	firstLabelId;

    /**
     * 标签状态 1 上线；2 下线
     */
    private Integer	labelStatus;

    /**
     * 标签描述-- 支持模糊搜索
     */
    private String	description;


    /**
     * 第几页，默认1
     */
    private Integer pageNo = 1;
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
}
