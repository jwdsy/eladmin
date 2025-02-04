package me.zhengjie.modules.business.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.business.domain.BizCustomerSubmitDetail;

import java.util.List;

/**
 * dal Interface:BizCustomerSubmitDetail
 * @author wangpengfei
 * @date 2025-1-24
 */
public interface BizCustomerSubmitDetailMapper extends BaseMapper<BizCustomerSubmitDetail> {

    List<BizCustomerSubmitDetail> selectAll();

    List<BizCustomerSubmitDetail> select(BizCustomerSubmitDetail record);

    Integer getCount(BizCustomerSubmitDetail record);

    BizCustomerSubmitDetail getByPrimaryKey(Object key);

    Integer insertSelective(BizCustomerSubmitDetail record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizCustomerSubmitDetail record);









}