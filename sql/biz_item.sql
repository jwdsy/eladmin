CREATE TABLE `biz_customer_pick_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `item_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB COMMENT='顾客挑选记录表';

CREATE TABLE `biz_customer_submit_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `item_ids` varchar(2000) DEFAULT NULL COMMENT '商品ID列表，以英文逗号分隔',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='顾客商品提交记录表';


CREATE TABLE `biz_item_base_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_no` varchar(64) DEFAULT NULL COMMENT '商品编号',
  `item_pic` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `description` varchar(200) DEFAULT NULL COMMENT '标签描述',
  `delivery_port` varchar(200) DEFAULT NULL COMMENT '出货港',
  `item_length` decimal(10,2) DEFAULT NULL COMMENT '商品长(cm)',
  `item_width` decimal(10,2) DEFAULT NULL COMMENT '商品宽(cm)',
  `item_height` decimal(10,2) DEFAULT NULL COMMENT '商品高(cm)',
  `inner_box` int(11) DEFAULT NULL COMMENT '内盒(个)',
  `outer_ctn` int(11) DEFAULT NULL COMMENT '外箱(个)',
  `weight_pieces` decimal(10,2) DEFAULT NULL COMMENT '净重(g)',
  `mininum_order_quantity` int(11) DEFAULT NULL COMMENT '起订量(PCS)',
  `carton_length` decimal(10,2) DEFAULT NULL COMMENT '包装箱长(cm)',
  `carton_width` decimal(10,2) DEFAULT NULL COMMENT '包装箱宽(cm)',
  `carton_height` decimal(10,2) DEFAULT NULL COMMENT '包装箱高(cm)',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价(人民币) ',
  `factory_name` varchar(64) DEFAULT NULL COMMENT '工厂名称',
  `item_craft` varchar(64) DEFAULT NULL COMMENT '商品工艺',
  `first_label_id` bigint(20) DEFAULT NULL COMMENT '一级标签ID',
  `second_label_id` bigint(20) DEFAULT NULL COMMENT '二级标签ID',
  `item_remark` varchar(200) DEFAULT NULL COMMENT '商品备注',
  `year` int(11) DEFAULT NULL COMMENT '年',
  `season` int(11) DEFAULT NULL COMMENT '1 春；2 夏；3 秋；4 冬',
  `del_flag` tinyint(2) DEFAULT 0 COMMENT '删除标识 0 否，1 是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  `modify_user_id` bigint(20) DEFAULT NULL COMMENT '最近修改人ID',
  PRIMARY KEY (`id`),
  KEY `idx_first_label_id` (`first_label_id`),
  KEY `idx_second_label_id` (`second_label_id`)
) ENGINE=InnoDB COMMENT='商品基本信息表';

CREATE TABLE `biz_item_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `label_name` varchar(20) DEFAULT NULL COMMENT '标签名称',
  `label_level` tinyint(4) DEFAULT NULL COMMENT '标签等级 1 一级，2 二级',
  `first_label_id` bigint(20) DEFAULT NULL COMMENT '一级标签ID',
  `description` varchar(20) DEFAULT NULL COMMENT '标签描述',
  `del_flag` tinyint(2) DEFAULT 0 COMMENT '删除标识 0 否，1 是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  `modify_user_id` bigint(20) DEFAULT NULL COMMENT '最近修改人ID',
  PRIMARY KEY (`id`),
  KEY `idx_first_label_id` (`first_label_id`)
) ENGINE=InnoDB COMMENT='商品标签表';