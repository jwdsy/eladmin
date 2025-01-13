package me.zhengjie.modules.business.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizCustomerSubmitRecord
 * 
 * @author wangpengfei
 * @date 2025-1-6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_customer_submit_record")
public class BizCustomerSubmitRecord implements Serializable {
	
      /**
       * 主键ID
       */
      private Long	id;
      /**
       * 用户ID
       */
      private Long	userId;
      /**
       * 商品ID列表，以英文逗号分隔
       */
      private String	itemIds;
      /**
       * 创建时间
       */
      private Date	createTime;

}
