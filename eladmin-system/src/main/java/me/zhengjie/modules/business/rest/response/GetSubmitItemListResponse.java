package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/17
 */
@Data
public class GetSubmitItemListResponse implements Serializable {
    /**
     * 提交记录列表
     */
    private List<recordModel> recordList;
    /**
     * 是否有下一页 1 是，0 否
     */
    private Integer hasMore;

    /**
     * 总数据量
     */
    private Integer totalNum;

    @Data
    public static class recordModel implements Serializable{
        private Long recordId;

        /**
         * 商品ID列表，以英文逗号分隔
         */
        private String	itemNos;

        /**
         * 提交时间
         */
        private Date createTime;
    }
}
