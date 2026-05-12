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
import java.util.Date;

/**
 * entity:ItemShowRecord
 *
 * @author Zheng Jie
 * @date 2026-05-12
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "biz_item_show_record")
public class ItemShowRecord implements Serializable {

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

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "最近修改时间")
    private Date lastModifyTime;

    @ApiModelProperty(value = "最近修改人ID")
    private Long modifyUserId;
}
