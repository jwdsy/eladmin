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
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.bizitem.domain.ItemShowRecord;
import me.zhengjie.modules.bizitem.repository.ItemShowRecordRepository;
import me.zhengjie.modules.bizitem.service.ItemShowRecordService;
import me.zhengjie.modules.bizitem.service.dto.ItemShowRecordDto;
import me.zhengjie.modules.bizitem.service.dto.ItemShowRecordQueryCriteria;
import me.zhengjie.modules.bizitem.service.mapstruct.ItemShowRecordMapper;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 商品展览服务实现
 * 
 * @author Zheng Jie
 * @date 2026-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "itemShowRecord")
public class ItemShowRecordServiceImpl implements ItemShowRecordService {

    private final ItemShowRecordRepository itemShowRecordRepository;
    private final ItemShowRecordMapper itemShowRecordMapper;

    @Override
    public PageResult<ItemShowRecordDto> queryAll(ItemShowRecordQueryCriteria criteria, Pageable pageable) {
        log.info("查询商品展览列表，查询条件: {}, 分页参数: {}", criteria, pageable);
        
        Page<ItemShowRecord> page = itemShowRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            // 使用 QueryHelp 构建查询条件
            return QueryHelp.getPredicate(root, criteria, criteriaBuilder);
        }, pageable);
        
        PageResult<ItemShowRecordDto> result = PageUtil.toPage(page.map(itemShowRecordMapper::toDto));
        log.info("查询商品展览列表成功，返回数量: {}", result.getContent().size());
        
        return result;
    }
}
