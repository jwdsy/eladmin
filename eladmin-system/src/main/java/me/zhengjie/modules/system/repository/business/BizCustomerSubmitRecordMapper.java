package me.zhengjie.modules.system.repository.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.system.domain.business.BizCustomerSubmitRecord;

import java.util.List;

/**
 * dal Interface:BizCustomerSubmitRecord
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizCustomerSubmitRecordMapper extends BaseMapper<BizCustomerSubmitRecord> {

    List<BizCustomerSubmitRecord> selectAll();

    List<BizCustomerSubmitRecord> select(BizCustomerSubmitRecord record);

    Integer getCount(BizCustomerSubmitRecord record);

    BizCustomerSubmitRecord getByPrimaryKey(Object key);

    Integer insertSelective(BizCustomerSubmitRecord record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizCustomerSubmitRecord record);









}