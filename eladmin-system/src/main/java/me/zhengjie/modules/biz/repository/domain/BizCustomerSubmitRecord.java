package me.zhengjie.modules.biz.repository.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "biz_customer_submit_record")
public class BizCustomerSubmitRecord implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "创建人ID")
    private Long userId;

    @ApiModelProperty(value = "状态")
    private Integer	submitStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
