package me.zhengjie.modules.biz.service;

import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordQueryCriteria;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
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


    PageResult<ItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria, Pageable pageable);

    List<ItemBaseRecordDto> queryAll(BizItemBaseRecordQueryCriteria criteria);

    void download(List<ItemBaseRecordDto> bizItemBaseRecordDtos, HttpServletResponse response);

    void create(ItemBaseRecordDto resources);

    void update(ItemBaseRecordDto resources);

    void delete(Set<Long> ids);
}