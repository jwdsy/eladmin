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
import java.math.BigDecimal;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Data
public class ItemDisplayQueryCriteria implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否喜欢 1 是，0 否
     */
    private Integer pickFlag;

    /**
     * 一级标签ID
     */
    @Query
    private Long firstLabelId;

    /**
     * 二级标签ID
     */
    @Query
    private Long secondLabelId;

    /**
     * 年份
     */
    @Query
    private Integer year;

    /**
     * 季节 1 春季，3 秋季
     */
    @Query
    private Integer season;

    /**
     * 商品状态 1 上线；2 下线
     */
    @Query
    private Integer itemStatus;

    /**
     * 删除标识 0 否，1 是
     */
    @Query
    private Integer delFlag;

    /**
     * 长度最小值
     */
    private BigDecimal lengthMin;

    /**
     * 长度最大值
     */
    private BigDecimal lengthMax;

    /**
     * 宽度最小值
     */
    private BigDecimal widthMin;

    /**
     * 宽度最大值
     */
    private BigDecimal widthMax;

    /**
     * 高度最小值
     */
    private BigDecimal heightMin;

    /**
     * 高度最大值
     */
    private BigDecimal heightMax;

    /**
     * 长度排序 1 降序；2 升序
     */
    private Integer lengthOrder;

    /**
     * 宽度排序 1 降序；2 升序
     */
    private Integer widthOrder;

    /**
     * 高度排序 1 降序；2 升序
     */
    private Integer heightOrder;
}
