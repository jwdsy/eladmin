package me.zhengjie.modules.biz.repository;

import me.zhengjie.modules.biz.repository.domain.BizCustomerSubmitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

/**
 * dal Interface:BizItemBaseRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizCustomerSubmitRecordRepository extends JpaRepository<BizCustomerSubmitRecord, Long>, JpaSpecificationExecutor<BizCustomerSubmitRecord> {
    void deleteAllByIdIn(Set<Long> ids);
}