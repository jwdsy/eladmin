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
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.repository.ItemBaseRecordRepository;
import me.zhengjie.modules.bizitem.service.ItemBaseRecordService;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordQueryCriteria;
import me.zhengjie.modules.bizitem.service.mapstruct.ItemBaseRecordMapper;
import me.zhengjie.utils.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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

    @Override
    public List<ItemBaseRecordDto> queryAll(ItemBaseRecordQueryCriteria criteria) {
        List<ItemBaseRecord> list = itemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            // 添加排序：按id倒序
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
            return QueryHelp.getPredicate(root, criteria, criteriaBuilder);
        });
        return itemBaseRecordMapper.toDto(list);
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public ItemBaseRecordDto findById(Long id) {
        ItemBaseRecord itemBaseRecord = itemBaseRecordRepository.findById(id).orElseGet(ItemBaseRecord::new);
        ValidationUtil.isNull(itemBaseRecord.getId(), "ItemBaseRecord", "id", id);
        return itemBaseRecordMapper.toDto(itemBaseRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ItemBaseRecord resources) {
        ItemBaseRecord item = itemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (item != null) {
            throw new EntityExistException(ItemBaseRecord.class, "itemNo", resources.getItemNo());
        }
        itemBaseRecordRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ItemBaseRecord resources) {
        ItemBaseRecord item = itemBaseRecordRepository.findById(resources.getId()).orElseGet(ItemBaseRecord::new);
        ValidationUtil.isNull(item.getId(), "ItemBaseRecord", "id", resources.getId());
        ItemBaseRecord old = itemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(ItemBaseRecord.class, "itemNo", resources.getItemNo());
        }
        itemBaseRecordRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            itemBaseRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ItemBaseRecordDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ItemBaseRecordDto dto : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("商品编号", dto.getItemNo());
            map.put("标签描述", dto.getDescription());
            map.put("单价", dto.getUnitPrice());
            map.put("年份", dto.getYear());
            map.put("季节", dto.getSeason());
            map.put("产品状态", dto.getItemStatus() == 1 ? "上线" : "下线");
            map.put("创建日期", dto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
