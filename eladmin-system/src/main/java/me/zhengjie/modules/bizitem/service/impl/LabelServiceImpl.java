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
import me.zhengjie.modules.bizitem.domain.Label;
import me.zhengjie.modules.bizitem.repository.LabelRepository;
import me.zhengjie.modules.bizitem.service.LabelService;
import me.zhengjie.modules.bizitem.service.dto.LabelDto;
import me.zhengjie.modules.bizitem.service.dto.LabelQueryCriteria;
import me.zhengjie.modules.bizitem.service.mapstruct.LabelMapper;
import me.zhengjie.utils.QueryHelp;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "label")
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelDto> queryAllWithHierarchy(LabelQueryCriteria criteria) {
        // 查询所有标签
        List<Label> labelList = labelRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
            return QueryHelp.getPredicate(root, criteria, criteriaBuilder);
        });

        if (labelList.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 DTO
        List<LabelDto> allLabels = labelMapper.toDto(labelList);

        // 构建一级标签列表和二级标签映射
        List<LabelDto> firstLevelLabels = new ArrayList<>();
        Map<Long, List<LabelDto>> secondLabelMap = new HashMap<>();

        for (LabelDto label : allLabels) {
            // 一级标签
            if (label.getLabelLevel() != null && label.getLabelLevel() == 1) {
                firstLevelLabels.add(label);
            }
            // 二级标签
            else if (label.getLabelLevel() != null && label.getLabelLevel() == 2 && label.getFirstLabelId() != null) {
                List<LabelDto> secondLabelList = secondLabelMap.computeIfAbsent(
                    label.getFirstLabelId(), 
                    k -> new ArrayList<>()
                );
                secondLabelList.add(label);
            }
        }

        // 为一级标签添加二级标签列表
        for (LabelDto firstLabel : firstLevelLabels) {
            firstLabel.setSecondLabelList(secondLabelMap.get(firstLabel.getId()));
        }

        return firstLevelLabels;
    }
}
