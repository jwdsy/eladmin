package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Data
public class GetItemLabelListResponse implements Serializable {

    private List<LabelModel> labelList;

    /**
     * 是否有下一页 1 是，0 否
     */
    private Integer hasMore;

    /**
     * 总数据量
     */
    private Long totalNum;


    @Data
    public static class LabelModel implements Serializable{
        /**
         * 标签ID
         */
        private Long	labelId;
        /**
         * 标签名称
         */
        private String	labelName;
        /**
         * 标签等级 1 一级，2 二级
         */
        private Integer	labelLevel;

        /**
         * 标签等级 1 一级，2 二级
         */
        private String	labelLevelName;

        /**
         * 一级标签ID
         */
        private Long	firstLabelId;

        /**
         * 一级标签名称
         */
        private String  firstLabelName;

        /**
         * 标签描述
         */
        private String	description;
        /**
         * 标签状态 1 上线；2 下线
         */
        private Integer	labelStatus;
        /**
         * 创建时间
         */
        private Date createTime;
        /**
         * 创建人ID
         */
        private Long	createUserId;
        /**
         * 最近修改时间
         */
        private Date	lastModifyTime;
        /**
         * 最近修改人ID
         */
        private Long	modifyUserId;
    }
}
