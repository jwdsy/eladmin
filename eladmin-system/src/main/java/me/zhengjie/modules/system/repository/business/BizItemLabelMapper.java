package me.zhengjie.modules.system.repository.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zhengjie.modules.system.domain.business.BizItemLabel;

import java.util.List;

/**
 * dal Interface:BizItemLabel
 * @author wangpengfei
 * @date 2025-1-6
 */
public interface BizItemLabelMapper extends BaseMapper<BizItemLabel> {

    List<BizItemLabel> selectAll();

    List<BizItemLabel> select(BizItemLabel record);

    Integer getCount(BizItemLabel record);

    BizItemLabel getByPrimaryKey(Object key);

    Integer insertSelective(BizItemLabel record);

    Integer deleteByPrimaryKey(Object key);

    Integer updateByPrimaryKey(BizItemLabel record);









}