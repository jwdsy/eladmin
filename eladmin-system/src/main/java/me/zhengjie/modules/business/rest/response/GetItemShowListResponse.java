package me.zhengjie.modules.business.rest.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/10/6
 */
@Data
public class GetItemShowListResponse implements Serializable {
    /**
     * 商品列表
     */
    private List<ItemModel> itemList;

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
         * 产品缩略图片
         */
        private String	itemCompressPic;
        /**
         * 标签描述
         */
        private String	description;
    }
}
