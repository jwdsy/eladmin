package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/2/22
 */
@Data
public class GetFirstLabelListResponse implements Serializable {
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
    }
}
