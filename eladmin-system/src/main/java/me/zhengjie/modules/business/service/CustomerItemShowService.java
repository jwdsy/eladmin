package me.zhengjie.modules.business.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.domain.*;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizItemShowRecordMapper;
import me.zhengjie.modules.business.rest.request.GetItemShowListRequest;
import me.zhengjie.modules.business.rest.response.GetItemShowListResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/10/6
 */
@Slf4j
@Service
public class CustomerItemShowService {

    @Resource
    private BizItemShowRecordMapper bizItemShowRecordMapper;

    public GetItemShowListResponse getItemShowList(GetItemShowListRequest request){
        GetItemShowListResponse response = new GetItemShowListResponse();
        LambdaQueryWrapper<BizItemShowRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (null != request.getYear()) {
            queryWrapper.eq(BizItemShowRecord::getYear, request.getYear());
        }
        if (null != request.getSeason()) {
            queryWrapper.eq(BizItemShowRecord::getSeason, request.getSeason());
        }
        queryWrapper.eq(BizItemShowRecord::getItemStatus, IsTypeInteger.YES.getCode())
                .eq(BizItemShowRecord::getDelFlag, IsTypeInteger.NO.getCode())
                .orderByDesc(BizItemShowRecord::getId);
        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<BizItemShowRecord> showRecordList = bizItemShowRecordMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(showRecordList)) {
            log.info("查询商品展示列表为空 request = {}", JSON.toJSON(request));
            return response;
        }

        List<GetItemShowListResponse.ItemModel> itemModelList = new ArrayList<>();
        for (BizItemShowRecord record : showRecordList) {
            GetItemShowListResponse.ItemModel itemModel = new GetItemShowListResponse.ItemModel();
            itemModel.setItemId(record.getId());
            itemModel.setItemNo(record.getItemNo());
            itemModel.setDescription(record.getDescription());
            itemModel.setItemPic(record.getItemCompressPic());
            itemModel.setItemNo(record.getItemNo());
            itemModelList.add(itemModel);
        }
        response.setItemList(itemModelList);
        return response ;
    }
}
