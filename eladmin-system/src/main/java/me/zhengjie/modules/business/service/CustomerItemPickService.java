package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizCustomerPickRecord;
import me.zhengjie.modules.business.domain.BizCustomerSubmitDetail;
import me.zhengjie.modules.business.domain.BizCustomerSubmitRecord;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizCustomerPickRecordMapper;
import me.zhengjie.modules.business.repository.BizCustomerSubmitDetailMapper;
import me.zhengjie.modules.business.repository.BizCustomerSubmitRecordMapper;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.CancelAllPickItemsRequest;
import me.zhengjie.modules.business.rest.request.CustomerPickItemRequest;
import me.zhengjie.modules.business.rest.request.CustomerSubmitItemRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/24
 */
@Slf4j
@Service
public class CustomerItemPickService {
    @Resource
    private BizCustomerPickRecordMapper bizCustomerPickRecordMapper;
    @Resource
    private BizCustomerSubmitRecordMapper bizCustomerSubmitRecordMapper;
    @Resource
    private BizCustomerSubmitDetailMapper bizCustomerSubmitDetailMapper;
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;

    public void customerPickItem(CustomerPickItemRequest request){
        BizItemBaseRecord bizItemBaseRecord = bizItemBaseRecordMapper.getByPrimaryKey(request.getItemId());
        if(null == bizItemBaseRecord
                || IsTypeInteger.YES.getCode().equals(bizItemBaseRecord.getDelFlag())
                || IsTypeInteger.NO.getCode().equals(bizItemBaseRecord.getItemStatus())){
            throw new BadRequestException("商品不存在");
        }
        LambdaQueryWrapper<BizCustomerPickRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizCustomerPickRecord::getUserId, request.getUserId())
                .eq(BizCustomerPickRecord::getItemId, request.getItemId());
        bizCustomerPickRecordMapper.delete(queryWrapper);
        if(IsTypeInteger.YES.getCode().equals(request.getPickFlag())){
            BizCustomerPickRecord pickRecord = new BizCustomerPickRecord();
            pickRecord.setUserId(request.getUserId());
            pickRecord.setItemId(request.getItemId());
            pickRecord.setItemRemark(request.getItemRemark());
            pickRecord.setCreateTime(new Date());
            bizCustomerPickRecordMapper.insertSelective(pickRecord);
        }
    }

    public void cancelAllPickItems(CancelAllPickItemsRequest request){
        LambdaQueryWrapper<BizCustomerPickRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizCustomerPickRecord::getUserId, request.getUserId());
        bizCustomerPickRecordMapper.delete(queryWrapper);
    }

    public void customerSubmitItem(CustomerSubmitItemRequest request){
        LambdaQueryWrapper<BizCustomerPickRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizCustomerPickRecord::getUserId, request.getUserId());
        List<BizCustomerPickRecord> pickRecordList = bizCustomerPickRecordMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(pickRecordList)){
            return;
        }

        BizCustomerSubmitRecord submitRecord = new BizCustomerSubmitRecord();
        submitRecord.setUserId(request.getUserId());
        submitRecord.setCreateTime(new Date());
        bizCustomerSubmitRecordMapper.insertSelective(submitRecord);
        for(BizCustomerPickRecord pickRecord : pickRecordList){
            BizCustomerSubmitDetail detail = new BizCustomerSubmitDetail();
            BeanUtils.copyProperties(pickRecord, detail);
            detail.setCreateTime(submitRecord.getCreateTime());
            detail.setSubmitRecordId(submitRecord.getId());
            bizCustomerSubmitDetailMapper.insertSelective(detail);
        }
    }
}
