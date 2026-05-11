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

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Entity
@Data
@Table(name = "biz_item_label")
public class Label implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @Column(name = "label_name", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @Column(name = "label_level", nullable = false)
    @NotNull
    @ApiModelProperty(value = "标签等级 1 一级，2 二级")
    private Integer labelLevel;

    @Column(name = "first_label_id")
    @ApiModelProperty(value = "一级标签ID")
    private Long firstLabelId;

    @Column(name = "description")
    @ApiModelProperty(value = "标签描述")
    private String description;

    @Column(name = "label_status", nullable = false)
    @NotNull
    @ApiModelProperty(value = "标签状态 1 上线；2 下线")
    private Integer labelStatus;

    @Column(name = "del_flag", nullable = false)
    @NotNull
    @ApiModelProperty(value = "删除标识 0 否，1 是")
    private Integer delFlag;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "create_user_id")
    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @Column(name = "last_modify_time")
    @ApiModelProperty(value = "最近修改时间")
    private Timestamp lastModifyTime;

    @Column(name = "modify_user_id")
    @ApiModelProperty(value = "最近修改人ID")
    private Long modifyUserId;

    public void copy(Label source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
