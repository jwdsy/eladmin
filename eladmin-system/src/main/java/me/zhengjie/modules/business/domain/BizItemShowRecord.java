package me.zhengjie.modules.business.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizItemShowRecord
 * 
 * @author wangpengfei
 * @date 2025-10-6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_item_show_record")
public class BizItemShowRecord implements Serializable {
	
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
       * 商品缩略图片
       */
      private String	itemCompressPic;
      /**
       * 标签描述
       */
      private String	description;
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
