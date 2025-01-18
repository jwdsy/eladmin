package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Data
public class GetDisplayLabelListResponse implements Serializable {
    /**
     * 标签列表
     */
    private List<LabelModel> labelList;

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
         * 二级标签列表
         */
        private List<LabelModel> secondLabelList;
    }
}
