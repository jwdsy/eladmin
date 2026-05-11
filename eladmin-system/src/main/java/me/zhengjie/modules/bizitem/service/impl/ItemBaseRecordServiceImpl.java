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
package me.zhengjie.modules.bizitem.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.repository.ItemBaseRecordRepository;
import me.zhengjie.modules.bizitem.service.ItemBaseRecordService;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordQueryCriteria;
import me.zhengjie.modules.bizitem.service.mapstruct.ItemBaseRecordMapper;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Zheng Jie
 * @date 2026-05-07
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "itemBaseRecord")
public class ItemBaseRecordServiceImpl implements ItemBaseRecordService {

    private final ItemBaseRecordRepository itemBaseRecordRepository;
    private final ItemBaseRecordMapper itemBaseRecordMapper;

    @Override
    public PageResult<ItemBaseRecordDto> queryAll(ItemBaseRecordQueryCriteria criteria, Pageable pageable) {
        Page<ItemBaseRecord> page = itemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            // 添加查询条件
            return QueryHelp.getPredicate(root, criteria, criteriaBuilder);
        }, pageable);
        return PageUtil.toPage(page.map(itemBaseRecordMapper::toDto));
    }
}
