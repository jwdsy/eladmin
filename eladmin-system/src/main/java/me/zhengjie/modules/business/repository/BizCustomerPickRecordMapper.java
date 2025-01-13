package me.zhengjie.modules.business.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.business.domain.BizCustomerPickRecord;

import java.util.List;

/**
 * dal Interface:BizCustomerPickRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizCustomerPickRecordMapper extends BaseMapper<BizCustomerPickRecord> {

    List<BizCustomerPickRecord> selectAll();

    List<BizCustomerPickRecord> select(BizCustomerPickRecord record);

    Integer getCount(BizCustomerPickRecord record);

    BizCustomerPickRecord getByPrimaryKey(Object key);

    Integer insertSelective(BizCustomerPickRecord record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizCustomerPickRecord record);









}