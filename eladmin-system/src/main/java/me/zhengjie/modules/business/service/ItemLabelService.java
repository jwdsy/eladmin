package me.zhengjie.modules.business.service;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizItemLabel;
import me.zhengjie.modules.business.enums.ItemLevelEnum;
import me.zhengjie.modules.business.repository.BizItemLabelMapper;
import me.zhengjie.modules.business.rest.request.CreateItemLabelRequest;
import me.zhengjie.modules.business.rest.response.CreateItemLabelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Slf4j
@Service
public class ItemLabelService {
    @Autowired
    private BizItemLabelMapper bizItemLabelMapper;

    public CreateItemLabelResponse createItemLabel(CreateItemLabelRequest request){
        CreateItemLabelResponse response = new CreateItemLabelResponse();
        if(null == request.getLabelId()){
            BizItemLabel record = new BizItemLabel();
            record.setLabelName(request.getLabelName());
            record.setLabelLevel(request.getLabelLevel());
            record.setDescription(request.getDescription());
            record.setCreateTime(new Date());
            bizItemLabelMapper.insertSelective(record);
            response.setLabelLevel(request.getLabelLevel());
            response.setLabelId(record.getId());
        }else {
            BizItemLabel itemLabel = bizItemLabelMapper.getByPrimaryKey(request.getLabelId());
            if(null == itemLabel){
                throw new BadRequestException("未查到更新标签");
            }
            itemLabel.setLabelName(request.getLabelName());
            itemLabel.setLabelLevel(request.getLabelLevel());
            itemLabel.setDescription(request.getDescription());
            itemLabel.setCreateTime(new Date());
            bizItemLabelMapper.updateByPrimaryKey(itemLabel);
            response.setLabelLevel(request.getLabelLevel());
            response.setLabelId(itemLabel.getId());
        }
        return response;
    }
}
