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
import me.zhengjie.modules.bizitem.domain.CustomerPickRecord;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.repository.CustomerPickRecordRepository;
import me.zhengjie.modules.bizitem.repository.ItemDisplayRepository;
import me.zhengjie.modules.bizitem.service.ItemDisplayService;
import me.zhengjie.modules.bizitem.service.dto.ItemDisplayDto;
import me.zhengjie.modules.bizitem.service.dto.ItemDisplayQueryCriteria;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "itemDisplay")
public class ItemDisplayServiceImpl implements ItemDisplayService {

    private final ItemDisplayRepository itemDisplayRepository;
    private final CustomerPickRecordRepository customerPickRecordRepository;

    @Override
    public PageResult<ItemDisplayDto> queryDisplayItems(ItemDisplayQueryCriteria criteria, Pageable pageable) {
        // 获取用户选择的商品映射
        Map<Long, String> pickItemMap = getPickItemMap(criteria.getUserId());
        Set<Long> pickItemIds = pickItemMap.keySet();

        // 如果只查询喜欢的商品，但用户没有喜欢的商品，直接返回空
        if (criteria.getPickFlag() != null && criteria.getPickFlag() == 1 && pickItemIds.isEmpty()) {
            return PageUtil.toPage(Page.empty());
        }

        // 构建查询条件
        Page<ItemBaseRecord> page = itemDisplayRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 使用 QueryHelp 处理基础查询条件
            Predicate basePredicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            if (basePredicate != null) {
                predicates.add(basePredicate);
            }

            // 如果只查询喜欢的商品
            if (criteria.getPickFlag() != null && criteria.getPickFlag() == 1) {
                predicates.add(root.get("id").in(pickItemIds));
            }

            // 排序
            List<javax.persistence.criteria.Order> orders = new ArrayList<>();
            // 默认按 ID 倒序
            orders.add(criteriaBuilder.desc(root.get("id")));
            criteriaQuery.orderBy(orders);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // 转换为 DTO
        return PageUtil.toPage(page.map(item -> convertToDto(item, pickItemMap)));
    }

    /**
     * 获取用户选择的商品映射
     */
    private Map<Long, String> getPickItemMap(Long userId) {
        if (userId == null) {
            return new HashMap<>();
        }

        List<CustomerPickRecord> pickRecords = customerPickRecordRepository.findByUserId(userId);

        Map<Long, String> pickItemMap = new HashMap<>();
        if (pickRecords != null && !pickRecords.isEmpty()) {
            for (CustomerPickRecord record : pickRecords) {
                pickItemMap.put(record.getItemId(), record.getItemRemark());
            }
        }
        return pickItemMap;
    }

    /**
     * 转换为 DTO
     */
    private ItemDisplayDto convertToDto(ItemBaseRecord item, Map<Long, String> pickItemMap) {
        ItemDisplayDto dto = new ItemDisplayDto();
        dto.setItemId(item.getId());
        dto.setItemNo(item.getItemNo());
        dto.setItemPic(item.getItemPic());
        dto.setItemCompressPic(item.getItemCompressPic());
        dto.setDescription(item.getDescription());

        // 设置尺寸信息
        dto.setItemLength(convertBigDecimalToString(item.getItemLength()));
        dto.setItemWidth(convertBigDecimalToString(item.getItemWidth()));
        dto.setItemHeight(convertBigDecimalToString(item.getItemHeight()));

        // 设置是否喜欢
        if (pickItemMap.containsKey(item.getId())) {
            dto.setPickFlag(1);
            dto.setPickRemark(pickItemMap.get(item.getId()));
        } else {
            dto.setPickFlag(0);
        }

        return dto;
    }

    /**
     * BigDecimal 转 String
     */
    private String convertBigDecimalToString(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
