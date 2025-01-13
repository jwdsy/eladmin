package me.zhengjie.modules.business.service;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.domain.BizItemLabel;
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

        }
        return response;
    }
}
