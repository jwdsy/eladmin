package me.zhengjie.modules.system.repository.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.system.domain.business.BizCustomerPickRecord;

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