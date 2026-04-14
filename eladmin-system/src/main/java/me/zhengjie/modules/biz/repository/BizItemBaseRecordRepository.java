package me.zhengjie.modules.biz.repository;

import me.zhengjie.modules.biz.repository.domain.BizItemBaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * dal Interface:BizItemBaseRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizItemBaseRecordRepository extends JpaRepository<BizItemBaseRecord, Long>, JpaSpecificationExecutor<BizItemBaseRecord> {
    BizItemBaseRecord findByItemNo(String itemNo);

    BizItemBaseRecord findTop1ByItemPicContainingAndDelFlagOrderByIdDesc(String keyword, Integer delFlag);

    BizItemBaseRecord findTop1ByItemPicContainingOrderByIdDesc(String keyword);

    Page<BizItemBaseRecord> findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(Long startId, Long endId, Pageable pageable);

    BizItemBaseRecord findTop1ByOrderByIdDesc();

    void deleteAllByIdIn(Set<Long> ids);
}