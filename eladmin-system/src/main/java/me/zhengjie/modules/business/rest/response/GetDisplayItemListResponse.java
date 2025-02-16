package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class GetDisplayItemListResponse implements Serializable {
    /**
     * 商品列表
     */
    private List<ItemModel> itemList;
//    /**
//     * 是否有下一页 1 是，0 否
//     */
//    private Integer hasMore;
//
//    /**
//     * 总数据量
//     */
//    private Integer totalNum;

    @Data
    public static class ItemModel implements Serializable {
        /**
         * 商品ID
         */
        private Long	itemId;
        /**
         * 产品编号
         */
        private String	itemNo;
        /**
         * 产品图片
         */
        private String	itemPic;
        /**
         * 标签描述
         */
        private String	description;
        /**
         * 是否喜欢 1 是，0 否
         */
        private Integer pickFlag;

        /**
         * 用户喜欢备注
         */
        private String pickRemark;

        /**
         * 商品长(cm)
         */
        private String itemLength;
        /**
         * 商品宽(cm)
         */
        private String	itemWidth;
        /**
         * 商品高(cm)
         */
        private String	itemHeight;
    }
}
