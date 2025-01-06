package me.zhengjie.modules.system.domain.business;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * entity:BizCustomerPickRecord
 * 
 * @author wangpengfei
 * @date 2025-1-6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("biz_customer_pick_record")
public class BizCustomerPickRecord implements Serializable {
	
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
       * 创建时间
       */
      private Date	createTime;

}
