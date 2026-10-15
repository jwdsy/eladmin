/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.bizitem.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * entity:ItemBaseRecord
 *
 * @author Zheng Jie
 * @date 2026-05-07
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "biz_item_base_record")
public class ItemBaseRecord implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "商品编号")
    @Column(name = "item_no")
    private String itemNo;

    @ApiModelProperty(value = "商品图片")
    @Column(name = "item_pic")
    private String itemPic;

    @ApiModelProperty(value = "商品缩略图片")
    @Column(name = "item_compress_pic")
    private String itemCompressPic;

    @ApiModelProperty(value = "标签描述")
    private String description;

    @ApiModelProperty(value = "出货港")
    private String deliveryPort;

    @ApiModelProperty(value = "商品长(cm)")
    private BigDecimal itemLength;

    @ApiModelProperty(value = "商品宽(cm)")
    private BigDecimal itemWidth;

    @ApiModelProperty(value = "商品高(cm)")
    private BigDecimal itemHeight;

    @ApiModelProperty(value = "内盒(个)")
    private Integer innerBox;

    @ApiModelProperty(value = "外箱(个)")
    private Integer outerCtn;

    @ApiModelProperty(value = "净重(g)")
    private BigDecimal weightPieces;

    @ApiModelProperty(value = "起订量(PCS)")
    private Integer mininumOrderQuantity;

    @ApiModelProperty(value = "包装箱长(cm)")
    private BigDecimal cartonLength;

    @ApiModelProperty(value = "包装箱宽(cm)")
    private BigDecimal cartonWidth;

    @ApiModelProperty(value = "包装箱高(cm)")
    private BigDecimal cartonHeight;

    @ApiModelProperty(value = "单价(人民币) ")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "商品工艺")
    private String itemCraft;

    @ApiModelProperty(value = "一级标签ID")
    private Long firstLabelId;

    @ApiModelProperty(value = "二级标签ID")
    private Long secondLabelId;

    @ApiModelProperty(value = "商品备注")
    private String itemRemark;

    @ApiModelProperty(value = "年")
    private Integer year;

    @ApiModelProperty(value = "1 春；2 夏；3 秋；4 冬")
    private Integer season;

    @ApiModelProperty(value = "产品状态 1 上线；2 下线")
    private Integer itemStatus;

    @ApiModelProperty(value = "删除标识 0 否，1 是")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "最近修改时间")
    private Date lastModifyTime;

    @ApiModelProperty(value = "最近修改人ID")
    private Long modifyUserId;

}
