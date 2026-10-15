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
package me.zhengjie.modules.bizitem.service;

import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;

/**
 * @author Zheng Jie
 * @date 2026-05-07
 */
public interface ItemBaseRecordService {

    /**
     * 查询所有数据
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    PageResult<ItemBaseRecordDto> queryAll(ItemBaseRecordQueryCriteria criteria, Pageable pageable);
}
