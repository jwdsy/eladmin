package me.zhengjie.modules.biz.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.biz.repository.BizCustomerSubmitRecordRepository;
import me.zhengjie.modules.biz.repository.domain.BizCustomerSubmitRecord;
import me.zhengjie.modules.biz.service.BizCustomerSubmitRecordService;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordDto;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordQueryCriteria;
import me.zhengjie.modules.biz.service.mapstruct.BizCustomerSubmitRecordStructMapper;
import me.zhengjie.modules.business.rest.request.GetSimpleSubmitItemListRequest;
import me.zhengjie.modules.business.service.CustomerSubmitManagerService;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "bizCustomerSubmitRecord")
public class BizCustomerSubmitRecordServiceImpl implements BizCustomerSubmitRecordService {

    private final BizCustomerSubmitRecordRepository bizCustomerSubmitRecordRepository;
    private final BizCustomerSubmitRecordStructMapper bizCustomerSubmitRecordStructMapper;
    @Autowired
    private CustomerSubmitManagerService customerSubmitManagerService;

    @Override
    public PageResult<BizCustomerSubmitRecordDto> queryAll(BizCustomerSubmitRecordQueryCriteria criteria, Pageable pageable) {
        Page<BizCustomerSubmitRecord> page = bizCustomerSubmitRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(bizCustomerSubmitRecordStructMapper::toDto).getContent(), page.getTotalElements());
    }

    @Override
    public List<BizCustomerSubmitRecordDto> queryAll(BizCustomerSubmitRecordQueryCriteria criteria) {
        List<BizCustomerSubmitRecord> list = bizCustomerSubmitRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return bizCustomerSubmitRecordStructMapper.toDto(list);
    }

    @Override
    public void download(Long submitId, HttpServletResponse response) throws Exception {
        GetSimpleSubmitItemListRequest request = new GetSimpleSubmitItemListRequest();
        request.setSubmitId(submitId);
        customerSubmitManagerService.exportSimpleSubmitItemList(request, response.getOutputStream());
    }

    @Override
    public void update(BizCustomerSubmitRecord resources) {
        BizCustomerSubmitRecord bizItem = bizCustomerSubmitRecordRepository.findById(resources.getId()).orElseGet(BizCustomerSubmitRecord::new);
        bizCustomerSubmitRecordRepository.save(resources);
    }

    @Override
    public void delete(Set<Long> ids) {
        bizCustomerSubmitRecordRepository.deleteAllByIdIn(ids);
    }
}
