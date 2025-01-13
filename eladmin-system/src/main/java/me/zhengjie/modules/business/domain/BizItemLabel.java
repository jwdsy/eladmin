package me.zhengjie.modules.business.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizItemLabel
 * 
 * @author wangpengfei
 * @date 2025-1-6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_item_label")
public class BizItemLabel implements Serializable {
	
      /**
       * 主键ID
       */
      private Long	id;
      /**
       * 标签名称
       */
      private String	labelName;
      /**
       * 标签等级 1 一级，2 二级
       */
      private Integer	labelLevel;
      /**
       * 一级标签ID
       */
      private Long	firstLabelId;
      /**
       * 标签描述
       */
      private String	description;
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
