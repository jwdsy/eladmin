package me.zhengjie.modules.business.rest.response;

import com.alibaba.excel.metadata.data.WriteCellData;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Data
public class GetItemDetailListResponse implements Serializable {
    /**
     * 商品列表
     */
    private List<ItemModel> itemList;
    /**
     * 是否有下一页 1 是，0 否
     */
    private Integer hasMore;

    /**
     * 总数据量
     */
    private Long totalNum;

    @Data
    public static class ItemModel implements Serializable{
        /**
         * 主键ID
         */
        private Long	itemId;
        /**
         * 商品编号
         */
        private String	itemNo;
        /**
         * 商品图片
         */
        private String	itemPic;
        /**
         * 标签描述
         */
        private String	description;
        /**
         * 出货港
         */
        private String	deliveryPort;
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
        /**
         * 内盒(个)
         */
        private Integer	innerBox;
        /**
         * 外箱(个)
         */
        private Integer	outerCtn;
        /**
         * 净重(g)
         */
        private String	weightPieces;
        /**
         * 起订量(PCS)
         */
        private Integer	mininumOrderQuantity;
        /**
         * 包装箱长(cm)
         */
        private String	cartonLength;
        /**
         * 包装箱宽(cm)
         */
        private String	cartonWidth;
        /**
         * 包装箱高(cm)
         */
        private String	cartonHeight;
        /**
         * 单价(人民币)
         */
        private String	unitPrice;
        /**
         * 工厂名称
         */
        private String	factoryName;
        /**
         * 商品工艺
         */
        private String	itemCraft;


// ==================== 下面是计算出来的 ====================
        /**
         * 商品图片
         */
        private WriteCellData<Void> itemPicture;
        /**
         * 美金--因为汇率，算得不太准
         */
        private String unitPrice4Dollar;

        /**
         * CBM / CTN  1箱是多少立方米
         */
        private String cbmPerCTN;

        /**
         * QTY IN 20GP(pcs)
         */
        private String qtyIn20GP;

        /**
         * QTY IN 40GP(pcs)
         */
        private String qtyIn40GP;

        /**
         * QTY IN 40HC(pcs)
         */
        private String qtyIn40HC;

// ==================== 用户填写的备注 ====================
        /**
         * 用户填写的备注
         */
        private String pickRemark;

// ==================== 下面是只显示不导出的 ====================

        /**
         * 一级标签ID
         */
        private Long	firstLabelId;
        /**
         * 一级标签名称
         */
        private String	firstLabelName;
        /**
         * 二级标签ID
         */
        private Long	secondLabelId;
        /**
         * 二级标签名称
         */
        private String	secondLabelName;
        /**
         * 年
         */
        private Integer	year;
        /**
         * 1 春；2 夏；3 秋；4 冬
         */
        private Integer	season;
        /**
         * 产品状态 1 上线；2 下线
         */
        private Integer	itemStatus;
        /**
         * 商品备注
         */
        private String	itemRemark;
        /**
         * 创建时间
         */
        private Date createTime;
        /**
         * 最近修改时间
         */
        private Date	lastModifyTime;
    }


}
