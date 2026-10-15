package me.zhengjie.modules.biz.repository;

import me.zhengjie.modules.bizitem.domain.CustomerSubmitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

/**
 * dal Interface:BizItemBaseRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizCustomerSubmitRecordRepository extends JpaRepository<CustomerSubmitRecord, Long>, JpaSpecificationExecutor<CustomerSubmitRecord> {
    void deleteAllByIdIn(Set<Long> ids);
}