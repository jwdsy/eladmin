package me.zhengjie.modules.biz.repository.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * entity:BizItemBaseRecord
 *
 * @author wangpengfei
 * @date 2025-1-6
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "biz_item_base_record")
public class BizItemBaseRecord extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id")
    @NotNull(groups = Update.class)
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
