package me.zhengjie.modules.business.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizItemBaseRecord
 * 
 * @author wangpengfei
 * @date 2025-1-6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_item_base_record")
public class BizItemBaseRecord implements Serializable {

      /**
       * 主键ID
       */
      private Long	id;
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
      private BigDecimal	itemLength;
      /**
       * 商品宽(cm)
       */
      private BigDecimal	itemWidth;
      /**
       * 商品高(cm)
       */
      private BigDecimal	itemHeight;
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
      private BigDecimal	weightPieces;
      /**
       * 起订量(PCS)
       */
      private Integer	mininumOrderQuantity;
      /**
       * 包装箱长(cm)
       */
      private BigDecimal	cartonLength;
      /**
       * 包装箱宽(cm)
       */
      private BigDecimal	cartonWidth;
      /**
       * 包装箱高(cm)
       */
      private BigDecimal	cartonHeight;
      /**
       * 单价(人民币) 
       */
      private BigDecimal	unitPrice;
      /**
       * 工厂名称
       */
      private String	factoryName;
      /**
       * 商品工艺
       */
      private String	itemCraft;
      /**
       * 一级标签ID
       */
      private Long	firstLabelId;
      /**
       * 二级标签ID
       */
      private Long	secondLabelId;
      /**
       * 商品备注
       */
      private String	itemRemark;
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
       * 删除标识 0 否，1 是
       */
      private Integer	delFlag;
      /**
       * 创建时间
       */
      private Date	createTime;
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
