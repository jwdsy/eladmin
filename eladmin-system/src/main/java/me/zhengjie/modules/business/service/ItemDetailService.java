package me.zhengjie.modules.business.service;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.CreateItemDetailRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemDetailRequest;
import me.zhengjie.modules.business.rest.request.UpdateItemStatusRequest;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Slf4j
@Service
public class ItemDetailService {
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;

    public CreateItemDetailResponse createOrUpdateItemDetail(CreateItemDetailRequest request){
        CreateItemDetailResponse response = new CreateItemDetailResponse();
        if(null == request.getItemId()){
            BizItemBaseRecord record = new BizItemBaseRecord();


            bizItemBaseRecordMapper.insertSelective(record);
        }else {
            BizItemBaseRecord record = bizItemBaseRecordMapper.getByPrimaryKey(request.getItemId());
            if(null == record){
                throw new BadRequestException("未查到产品信息");
            }
            response.setItemId(record.getId());
        }
        return response;
    }

    public void updateItemStatus(UpdateItemStatusRequest request){
        BizItemBaseRecord record = bizItemBaseRecordMapper.getByPrimaryKey(request.getItemId());
        if(null == record){
            throw new BadRequestException("未查到产品信息");
        }
        record.setItemStatus(request.getItemStatus());
        record.setLastModifyTime(new Date());
        record.setModifyUserId(request.getUserId());
        bizItemBaseRecordMapper.updateByPrimaryKey(record);
    }

    public void deleteItemDetail(DeleteItemDetailRequest request){
        BizItemBaseRecord record = bizItemBaseRecordMapper.getByPrimaryKey(request.getItemId());
        if(null == record){
            throw new BadRequestException("未查到产品信息");
        }
        record.setDelFlag(IsTypeInteger.YES.getCode());
        record.setLastModifyTime(new Date());
        record.setModifyUserId(request.getUserId());
        bizItemBaseRecordMapper.updateByPrimaryKey(record);
    }
}
