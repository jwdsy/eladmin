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
import me.zhengjie.modules.bizitem.domain.CustomerSubmitDetail;
import me.zhengjie.modules.bizitem.domain.CustomerSubmitRecord;
import me.zhengjie.modules.bizitem.repository.CustomerPickRecordRepository;
import me.zhengjie.modules.bizitem.repository.CustomerSubmitDetailRepository;
import me.zhengjie.modules.bizitem.repository.CustomerSubmitRecordRepository;
import me.zhengjie.modules.bizitem.service.CustomerSubmitService;
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
public class CustomerSubmitServiceImpl implements CustomerSubmitService {

    private final CustomerPickRecordRepository customerPickRecordRepository;
    private final CustomerSubmitRecordRepository customerSubmitRecordRepository;
    private final CustomerSubmitDetailRepository customerSubmitDetailRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPickedItems(Long userId) {
        if (userId == null) {
            return;
        }

        // 查询用户选择的商品
        List<CustomerPickRecord> pickRecords = customerPickRecordRepository.findByUserId(userId);
        if (pickRecords == null || pickRecords.isEmpty()) {
            return;
        }

        // 创建提交记录
        CustomerSubmitRecord submitRecord = new CustomerSubmitRecord();
        submitRecord.setUserId(userId);
        submitRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
        submitRecord = customerSubmitRecordRepository.save(submitRecord);

        // 创建提交明细
        for (CustomerPickRecord pickRecord : pickRecords) {
            CustomerSubmitDetail detail = new CustomerSubmitDetail();
            detail.setUserId(pickRecord.getUserId());
            detail.setItemId(pickRecord.getItemId());
            detail.setSubmitRecordId(submitRecord.getId());
            detail.setItemRemark(pickRecord.getItemRemark());
            detail.setCreateTime(submitRecord.getCreateTime());
            customerSubmitDetailRepository.save(detail);
        }
    }
}
