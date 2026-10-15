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
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.bizitem.domain.CustomerPickRecord;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.repository.CustomerPickRecordRepository;
import me.zhengjie.modules.bizitem.repository.ItemBaseRecordRepository;
import me.zhengjie.modules.bizitem.service.CustomerPickService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Service
@RequiredArgsConstructor
public class CustomerPickServiceImpl implements CustomerPickService {

    private final CustomerPickRecordRepository customerPickRecordRepository;
    private final ItemBaseRecordRepository itemBaseRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickItem(Long userId, Long itemId, String itemRemark, Integer pickFlag) {
        if (userId == null || itemId == null || pickFlag == null) {
            return;
        }

        // 验证商品是否存在且有效
        ItemBaseRecord item = itemBaseRecordRepository.findById(itemId).orElse(null);
        if (item == null || item.getDelFlag() == 1 || item.getItemStatus() != 1) {
            throw new BadRequestException("商品不存在");
        }

        // 删除已存在的选择记录
        CustomerPickRecord existingRecord = customerPickRecordRepository.findByUserIdAndItemId(userId, itemId);
        if (existingRecord != null) {
            customerPickRecordRepository.delete(existingRecord);
        }

        // 如果是选择商品（pickFlag=1），则创建新记录
        if (pickFlag == 1) {
            CustomerPickRecord pickRecord = new CustomerPickRecord();
            pickRecord.setUserId(userId);
            pickRecord.setItemId(itemId);
            pickRecord.setItemRemark(itemRemark);
            pickRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
            customerPickRecordRepository.save(pickRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanPickedItems(Long userId) {
        if (userId == null) {
            return;
        }

        // 查询用户的所有选择记录
        List<CustomerPickRecord> pickRecords = customerPickRecordRepository.findByUserId(userId);
        
        // 批量删除
        if (pickRecords != null && !pickRecords.isEmpty()) {
            customerPickRecordRepository.deleteAll(pickRecords);
        }
    }
}
