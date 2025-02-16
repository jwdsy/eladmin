package me.zhengjie.modules.business.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.domain.BizCustomerPickRecord;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.domain.BizItemLabel;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.enums.LabelLevelEnum;
import me.zhengjie.modules.business.repository.BizCustomerPickRecordMapper;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.repository.BizItemLabelMapper;
import me.zhengjie.modules.business.rest.request.GetDisplayItemListRequest;
import me.zhengjie.modules.business.rest.request.GetDisplayLabelListRequest;
import me.zhengjie.modules.business.rest.response.GetDisplayItemListResponse;
import me.zhengjie.modules.business.rest.response.GetDisplayLabelListResponse;
import me.zhengjie.modules.business.utils.BigDecimalUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/24
 */
@Slf4j
@Service
public class CustomerItemDisplayService {
    @Resource
    private BizItemLabelMapper bizItemLabelMapper;
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;
    @Resource
    private BizCustomerPickRecordMapper bizCustomerPickRecordMapper;


    public GetDisplayLabelListResponse getDisplayLabelList(GetDisplayLabelListRequest request){
        LambdaQueryWrapper<BizItemLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizItemLabel::getLabelStatus, 1)
                .eq(BizItemLabel::getDelFlag, IsTypeInteger.NO.getCode())
                .orderByAsc(BizItemLabel::getId);
        List<BizItemLabel> labelList = bizItemLabelMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(labelList)){
            return null;
        }
        List<GetDisplayLabelListResponse.LabelModel> labelModelList = new ArrayList<>();
        Map<Long, List<GetDisplayLabelListResponse.LabelModel>> secondLabelMap = new HashMap<>();
        // 添加一级标签
        for (BizItemLabel label : labelList){
            if(LabelLevelEnum.LEVEL_1.getCode().equals(label.getLabelLevel())){
                GetDisplayLabelListResponse.LabelModel labelModel = new GetDisplayLabelListResponse.LabelModel();
                labelModel.setLabelId(label.getId());
                labelModel.setLabelName(label.getLabelName());
                labelModel.setLabelLevel(label.getLabelLevel());
                labelModelList.add(labelModel);
                continue;
            }
            if(LabelLevelEnum.LEVEL_2.getCode().equals(label.getLabelLevel()) && null != label.getFirstLabelId()){
                List<GetDisplayLabelListResponse.LabelModel> secondLabelList = secondLabelMap.get(label.getFirstLabelId());
                if(CollectionUtils.isEmpty(secondLabelList)){
                    secondLabelList = new ArrayList<>();
                }
                GetDisplayLabelListResponse.LabelModel labelModel = new GetDisplayLabelListResponse.LabelModel();
                labelModel.setLabelId(label.getId());
                labelModel.setLabelName(label.getLabelName());
                labelModel.setLabelLevel(label.getLabelLevel());
                secondLabelList.add(labelModel);
                secondLabelMap.put(label.getFirstLabelId(), secondLabelList);
            }
        }

        if(CollectionUtils.isEmpty(labelModelList)){
            return null;
        }

        // 添加二级标签
        for (GetDisplayLabelListResponse.LabelModel firstLabel : labelModelList){
            firstLabel.setSecondLabelList(secondLabelMap.get(firstLabel.getLabelId()));
        }

        // 封装返回数据
        GetDisplayLabelListResponse response = new GetDisplayLabelListResponse();
        response.setLabelList(labelModelList);
        return response;
    }

    public GetDisplayItemListResponse getDisplayItemList(GetDisplayItemListRequest request){
        // 1、封装查询条件
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = new LambdaQueryWrapper<>();

        if(null != request.getFirstLabelId()){
            queryWrapper.eq(BizItemBaseRecord::getFirstLabelId, request.getFirstLabelId());
        }
        if(null!= request.getSecondLabelId()){
            queryWrapper.eq(BizItemBaseRecord::getSecondLabelId, request.getSecondLabelId());
        }

        Map<Long, String> pickItemIdMap = getPickItemMap(request.getUserId());
        Set<Long> pickItemIdSet = pickItemIdMap.keySet();
        if(IsTypeInteger.YES.getCode().equals(request.getPickFlag())){
            // 没有用户挑选的产品，直接返回空
            if(CollectionUtils.isEmpty(pickItemIdSet)){
                log.info("未查到用户喜欢的商品 userId = {}", request.getUserId());
                return null;
            }else {
                queryWrapper.in(BizItemBaseRecord::getId, pickItemIdSet);
            }
        }

        queryWrapper.eq(BizItemBaseRecord::getItemStatus, IsTypeInteger.YES.getCode())
                .eq(BizItemBaseRecord::getDelFlag, IsTypeInteger.NO.getCode())
                .orderByDesc(BizItemBaseRecord::getId);

        // 2、分页查询数据
        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);

        if(CollectionUtils.isEmpty(recordList)){
            log.info("查询列表为空 request = {}", JSON.toJSON(request));
            return null;
        }

        // 3、封装返回数据
        List<GetDisplayItemListResponse.ItemModel> itemModelList = new ArrayList<>();
        for(BizItemBaseRecord record : recordList){
            GetDisplayItemListResponse.ItemModel itemModel = new GetDisplayItemListResponse.ItemModel();
            itemModel.setItemId(record.getId());
            itemModel.setItemNo(record.getItemNo());
            itemModel.setDescription(record.getDescription());
            itemModel.setItemPic(record.getItemPic());
            itemModel.setItemNo(record.getItemNo());
            itemModel.setPickFlag(IsTypeInteger.NO.getCode());
            if(pickItemIdSet.contains(record.getId())){
                itemModel.setPickFlag(IsTypeInteger.YES.getCode());
                itemModel.setPickRemark(pickItemIdMap.get(record.getId()));
            }

            itemModel.setItemLength(BigDecimalUtil.convertToString(record.getItemLength()));
            itemModel.setItemWidth(BigDecimalUtil.convertToString(record.getItemWidth()));
            itemModel.setItemHeight(BigDecimalUtil.convertToString(record.getItemHeight()));
            itemModelList.add(itemModel);
        }
        GetDisplayItemListResponse response = new GetDisplayItemListResponse();
        response.setItemList(itemModelList);
        return response;
    }

    public Map<Long, String> getPickItemMap(Long userId){
        LambdaQueryWrapper<BizCustomerPickRecord> pickQueryWrapper = new LambdaQueryWrapper<>();
        pickQueryWrapper.eq(BizCustomerPickRecord::getUserId, userId);
        List<BizCustomerPickRecord> pickRecordList = bizCustomerPickRecordMapper.selectList(pickQueryWrapper);
        Map<Long, String> pickRemarkMap = new HashMap<>();
        if(CollectionUtils.isEmpty(pickRecordList)){
           return pickRemarkMap;
        }
        for(BizCustomerPickRecord pickRecord : pickRecordList){
            pickRemarkMap.put(pickRecord.getItemId(), pickRecord.getItemRemark());
        }
        return pickRemarkMap;
    }
}
