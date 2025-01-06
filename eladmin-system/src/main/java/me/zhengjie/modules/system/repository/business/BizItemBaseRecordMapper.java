package me.zhengjie.modules.system.repository.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.system.domain.business.BizItemBaseRecord;

import java.util.List;

/**
 * dal Interface:BizItemBaseRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizItemBaseRecordMapper extends BaseMapper<BizItemBaseRecord> {

    List<BizItemBaseRecord> selectAll();

    List<BizItemBaseRecord> select(BizItemBaseRecord record);

    Integer getCount(BizItemBaseRecord record);

    BizItemBaseRecord getByPrimaryKey(Object key);

    Integer insertSelective(BizItemBaseRecord record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizItemBaseRecord record);









}