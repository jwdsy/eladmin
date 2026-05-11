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
package me.zhengjie.modules.bizitem.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Data
public class LabelDto implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签等级 1 一级，2 二级
     */
    private Integer labelLevel;

    /**
     * 一级标签ID
     */
    private Long firstLabelId;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签状态 1 上线；2 下线
     */
    private Integer labelStatus;

    /**
     * 删除标识 0 否，1 是
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 最近修改时间
     */
    private Timestamp lastModifyTime;

    /**
     * 最近修改人ID
     */
    private Long modifyUserId;

    /**
     * 二级标签列表
     */
    private List<LabelDto> secondLabelList;
}
