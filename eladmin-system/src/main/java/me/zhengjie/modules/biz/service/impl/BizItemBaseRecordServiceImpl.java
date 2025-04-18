package me.zhengjie.modules.biz.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.modules.biz.repository.BizItemBaseRecordRepository;
import me.zhengjie.modules.biz.repository.domain.BizItemBaseRecord;
import me.zhengjie.modules.biz.service.BizItemBaseRecordService;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordDto;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordQueryCriteria;
import me.zhengjie.modules.biz.service.mapstruct.BizItemBaseRecordStructMapper;
import me.zhengjie.modules.system.domain.Job;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "bizItemBaseRecord")
public class BizItemBaseRecordServiceImpl implements BizItemBaseRecordService {

    private final BizItemBaseRecordRepository bizItemBaseRecordRepository;
    private final BizItemBaseRecordStructMapper bizItemBaseRecordStructMapper;

    @Override
    public PageResult<BizItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria, Pageable pageable) {
        Page<BizItemBaseRecord> page = bizItemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(bizItemBaseRecordStructMapper::toDto).getContent(), page.getTotalElements());
    }

    @Override
    public List<BizItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria) {
        List<BizItemBaseRecord> list = bizItemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return bizItemBaseRecordStructMapper.toDto(list);
    }

    @Override
    public void download(List<BizItemBaseRecordDto> bizItemBaseRecordDtos, HttpServletResponse response) {

    }

    @Override
    public void create(BizItemBaseRecord resources) {
        BizItemBaseRecord bizItem = bizItemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (bizItem != null) {
            throw new EntityExistException(BizItemBaseRecord.class, "itemNo", resources.getItemNo());
        }
        bizItemBaseRecordRepository.save(resources);
    }

    @Override
    public void update(BizItemBaseRecord resources) {
        BizItemBaseRecord bizItem = bizItemBaseRecordRepository.findById(resources.getId()).orElseGet(BizItemBaseRecord::new);
        BizItemBaseRecord old = bizItemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(Job.class, "itemNo", resources.getItemNo());
        }
        ValidationUtil.isNull(bizItem.getId(), "bizItem", "id", resources.getId());
        resources.setId(bizItem.getId());
        bizItemBaseRecordRepository.save(resources);
    }

    @Override
    public void delete(Set<Long> ids) {
        bizItemBaseRecordRepository.deleteAllByIdIn(ids);
    }
}
