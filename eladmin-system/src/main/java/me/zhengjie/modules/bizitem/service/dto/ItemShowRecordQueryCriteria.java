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

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2026-05-12
 */
@Data
public class ItemShowRecordQueryCriteria {

    /**
     * 用户ID（用于扩展，当前版本不使用）
     */
    private Long userId;

    /**
     * 年份筛选
     */
    @Query
    private Integer year;

    /**
     * 季节筛选：1 春；2 夏；3 秋；4 冬
     */
    @Query
    private Integer season;

    /**
     * 产品状态 1 上线；2 下线
     */
    @Query
    private Integer itemStatus;

    /**
     * 删除标识 0 否，1 是
     */
    @Query
    private Integer delFlag;

    /**
     * 创建时间范围
     */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
