package me.zhengjie.modules.biz.service;

import me.zhengjie.modules.biz.repository.domain.BizItemBaseRecord;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordDto;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * dal Interface:BizItemBaseRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizItemBaseRecordService {


    PageResult<BizItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria, Pageable pageable);

    List<BizItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria);

    void download(List<BizItemBaseRecordDto> bizItemBaseRecordDtos, HttpServletResponse response);

    void create(BizItemBaseRecord resources);

    void update(BizItemBaseRecord resources);

    void delete(Set<Long> ids);
}