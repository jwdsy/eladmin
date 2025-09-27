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
public class GetItemDetailListRequest implements Serializable {

    private List<Long> itemIdList;

    private List<String> itemNoList;

    /**
     * 一级标签，null 是查全部
     */
    private Long firstLabelId;
    /**
     * 二级标签，null 是查全部
     */
    private Long secondLabelId;
    /**
     * 标签描述，支持模糊查询
     */
    private String	description;
    /**
     * 工厂名称，支持模糊查询
     */
    private String factoryName;

    /**
     * 产品状态 1 上线；2 下线  不选是全部
     */
    private Integer	itemStatus;

    /**
     * 年
     */
    private Integer year;

    /**
     * 季度
     */
    private Integer season;



    /**
     * 第几页，默认1
     */
    private Integer page = 1;
    /**
     * 每页数量，默认10
     */
    private Integer size = 10;

}
