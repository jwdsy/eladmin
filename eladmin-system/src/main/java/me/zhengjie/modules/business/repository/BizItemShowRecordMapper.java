package me.zhengjie.modules.business.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.business.domain.BizItemShowRecord;

import java.util.List;

/**
 * dal Interface:BizItemShowRecord
 * @author wangpengfei
 * @date 2025-10-6
 */
public interface BizItemShowRecordMapper extends BaseMapper<BizItemShowRecord> {

    List<BizItemShowRecord> selectAll();

    List<BizItemShowRecord> select(BizItemShowRecord record);

    Integer getCount(BizItemShowRecord record);

    BizItemShowRecord getByPrimaryKey(Object key);

    Integer insertSelective(BizItemShowRecord record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizItemShowRecord record);









}