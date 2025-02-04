package me.zhengjie.modules.business.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizCustomerSubmitDetail
 * 
 * @author wangpengfei
 * @date 2025-1-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_customer_submit_detail")
public class BizCustomerSubmitDetail implements Serializable {
	
      /**
       * 主键ID
       */
      private Long	id;
      /**
       * 用户ID
       */
      private Long	userId;
      /**
       * 商品ID
       */
      private Long	itemId;
      /**
       * 提交记录ID
       */
      private Long	submitRecordId;
      /**
       * 商品备注
       */
      private String	itemRemark;
      /**
       * 创建时间
       */
      private Date	createTime;

}
