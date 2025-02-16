package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/2/16
 */
@Data
public class GetSubmitRecordListResponse implements Serializable {
    /**
     * 是否有下一页 1 是，0 否
     */
    private Integer hasMore;

    /**
     * 总数据量
     */
    private Long totalNum;

    /**
     * 提交记录列表
     */
    List<SubmitModel> recordList;

    @Data
    public static class SubmitModel implements Serializable{
        /**
         * 提交记录ID
         */
        private Long recordId;

        /**
         * 提交记录时间
         */
        private Date submitTime;
    }

}
