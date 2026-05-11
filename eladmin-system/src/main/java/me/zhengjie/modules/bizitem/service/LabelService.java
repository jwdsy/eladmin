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

import me.zhengjie.modules.bizitem.service.dto.LabelDto;
import me.zhengjie.modules.bizitem.service.dto.LabelQueryCriteria;

import java.util.List;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
public interface LabelService {

    /**
     * 查询所有标签（用于前台展示，包含层级结构）
     * @param criteria 条件
     * @return /
     */
    List<LabelDto> queryAllWithHierarchy(LabelQueryCriteria criteria);
}
