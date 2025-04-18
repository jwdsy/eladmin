package me.zhengjie.modules.biz.service;

import me.zhengjie.modules.biz.repository.domain.BizCustomerSubmitRecord;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordDto;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordQueryCriteria;
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
public interface BizCustomerSubmitRecordService {


    PageResult<BizCustomerSubmitRecordDto> queryAll(BizCustomerSubmitRecordQueryCriteria criteria, Pageable pageable);

    List<BizCustomerSubmitRecordDto> queryAll(BizCustomerSubmitRecordQueryCriteria criteria);

    void download(Long submitId, HttpServletResponse response) throws Exception;

    void update(BizCustomerSubmitRecord resources);

    void delete(Set<Long> ids);
}