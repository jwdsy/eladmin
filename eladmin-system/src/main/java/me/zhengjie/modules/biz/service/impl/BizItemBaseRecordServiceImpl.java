package me.zhengjie.modules.biz.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.modules.biz.repository.BizItemBaseRecordRepository;
import me.zhengjie.modules.biz.service.BizItemBaseRecordService;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordQueryCriteria;
import me.zhengjie.modules.biz.service.mapstruct.BizItemBaseRecordStructMapper;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
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
    public PageResult<ItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria, Pageable pageable) {
        Page<ItemBaseRecord> page = bizItemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(bizItemBaseRecordStructMapper::toDto).getContent(), page.getTotalElements());
    }

    @Override
    public List<ItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria) {
        List<ItemBaseRecord> list = bizItemBaseRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return bizItemBaseRecordStructMapper.toDto(list);
    }

    @Override
    public void download(List<ItemBaseRecordDto> bizItemBaseRecordDtos, HttpServletResponse response) {

    }

    @Override
    public void create(ItemBaseRecordDto resources) {
        ItemBaseRecord bizItem = bizItemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (bizItem != null) {
            throw new EntityExistException(ItemBaseRecord.class, "itemNo", resources.getItemNo());
        }
        bizItemBaseRecordRepository.save(bizItemBaseRecordStructMapper.toEntity(resources));
    }

    @Override
    public void update(ItemBaseRecordDto resources) {
        ItemBaseRecord bizItem = bizItemBaseRecordRepository.findById(resources.getId()).orElseGet(ItemBaseRecord::new);
        ItemBaseRecord old = bizItemBaseRecordRepository.findByItemNo(resources.getItemNo());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(ItemBaseRecord.class, "itemNo", resources.getItemNo());
        }
        ValidationUtil.isNull(bizItem.getId(), "bizItem", "id", resources.getId());
        ItemBaseRecord entity = bizItemBaseRecordStructMapper.toEntity(resources);
        entity.setId(bizItem.getId());
        bizItemBaseRecordRepository.save(entity);
    }

    @Override
    public void delete(Set<Long> ids) {
        bizItemBaseRecordRepository.deleteAllByIdIn(ids);
    }
}
