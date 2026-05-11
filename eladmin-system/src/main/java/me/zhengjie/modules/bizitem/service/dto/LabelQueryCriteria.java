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
import me.zhengjie.annotation.Query;

import java.io.Serializable;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Data
public class LabelQueryCriteria implements Serializable {

    /**
     * 标签名称
     */
    @Query(type = Query.Type.INNER_LIKE)
    private String labelName;

    /**
     * 标签等级 1 一级，2 二级
     */
    @Query
    private Integer labelLevel;

    /**
     * 一级标签ID
     */
    @Query
    private Long firstLabelId;

    /**
     * 标签状态 1 上线；2 下线
     */
    @Query
    private Integer labelStatus;

    /**
     * 删除标识 0 否，1 是
     */
    @Query
    private Integer delFlag;
}
