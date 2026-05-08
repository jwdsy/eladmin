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
 * @date 2026-05-07
 */
@Data
public class ItemBaseRecordQueryCriteria {

    /**
     * 模糊搜索关键词，同时搜索itemNo和description字段（OR关系）
     */
    @Query(blurry = "itemNo,description")
    private String blurry;

    @Query(type = Query.Type.INNER_LIKE)
    private String itemNo;

    @Query(type = Query.Type.INNER_LIKE)
    private String description;

    @Query
    private Long firstLabelId;

    @Query
    private Long secondLabelId;

    @Query
    private Integer year;

    @Query
    private Integer season;

    @Query
    private Integer itemStatus;

    @Query
    private Integer delFlag;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
