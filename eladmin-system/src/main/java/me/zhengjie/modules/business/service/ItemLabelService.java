package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizItemLabel;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.enums.LabelLevelEnum;
import me.zhengjie.modules.business.enums.LabelStatusEnum;
import me.zhengjie.modules.business.repository.BizItemLabelMapper;
import me.zhengjie.modules.business.rest.request.CreateItemLabelRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemLabelRequest;
import me.zhengjie.modules.business.rest.request.GetItemLabelListRequest;
import me.zhengjie.modules.business.rest.request.UpdateItemLabelStatusRequest;
import me.zhengjie.modules.business.rest.response.CreateItemLabelResponse;
import me.zhengjie.modules.business.rest.response.GetFirstLabelListResponse;
import me.zhengjie.modules.business.rest.response.GetItemLabelListResponse;
import me.zhengjie.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Slf4j
@Service
public class ItemLabelService {
    private static final Map<Long, String> labelNameMap = new HashMap<>();
    @Autowired
    private BizItemLabelMapper bizItemLabelMapper;
    @Autowired
    private RedisUtils redisUtils;

    public CreateItemLabelResponse createItemLabel(CreateItemLabelRequest request){
        CreateItemLabelResponse response = new CreateItemLabelResponse();
        if(LabelLevelEnum.LEVEL_2.getCode().equals(request.getLabelLevel())){
            BizItemLabel firstLabel = bizItemLabelMapper.getByPrimaryKey(request.getFirstLabelId());
            if(null == firstLabel){
                throw new BadRequestException("一级标签不存在");
            }
        }

        if(null == request.getLabelId()){
            // 插入标签
            BizItemLabel record = new BizItemLabel();
            record.setLabelName(request.getLabelName());
            record.setLabelLevel(request.getLabelLevel());
            record.setDescription(request.getDescription());
            record.setLabelStatus(LabelStatusEnum.OFFLINE.getCode());
            record.setCreateTime(new Date());
            record.setCreateUserId(request.getUserId());
            if(LabelLevelEnum.LEVEL_2.getCode().equals(request.getLabelLevel())){
                record.setFirstLabelId(request.getFirstLabelId());
            }
            bizItemLabelMapper.insertSelective(record);
            response.setLabelLevel(request.getLabelLevel());
            response.setLabelId(record.getId());
        }else {
            // 更新标签
            BizItemLabel itemLabel = bizItemLabelMapper.getByPrimaryKey(request.getLabelId());
            if(null == itemLabel){
                throw new BadRequestException("未查到标签信息");
            }
            itemLabel.setLabelName(request.getLabelName());
            itemLabel.setLabelLevel(request.getLabelLevel());
            itemLabel.setDescription(request.getDescription());
            itemLabel.setModifyUserId(request.getUserId());
            itemLabel.setLastModifyTime(new Date());
            if(LabelLevelEnum.LEVEL_2.getCode().equals(request.getLabelLevel())){
                itemLabel.setFirstLabelId(request.getFirstLabelId());
            }
            bizItemLabelMapper.updateByPrimaryKey(itemLabel);
            response.setLabelLevel(request.getLabelLevel());
            response.setLabelId(itemLabel.getId());
        }
        return response;
    }

    public void updateItemLabelStatus(UpdateItemLabelStatusRequest request){
        BizItemLabel itemLabel = bizItemLabelMapper.getByPrimaryKey(request.getLabelId());
        if(null == itemLabel){
            throw new BadRequestException("未查到标签信息");
        }
        if(IsTypeInteger.YES.getCode().equals(itemLabel.getDelFlag())){
            throw new BadRequestException("标签已被删除");
        }
        itemLabel.setLabelStatus(request.getLabelStatus());
        itemLabel.setLastModifyTime(new Date());
        itemLabel.setModifyUserId(request.getUserId());
        bizItemLabelMapper.updateByPrimaryKey(itemLabel);
    }

    public void deleteItemLabel(DeleteItemLabelRequest request){
        bizItemLabelMapper.batchDeleteByIds(request.getLabelIdList(), request.getUserId());
    }

    public GetItemLabelListResponse getItemLabelList(GetItemLabelListRequest request){
        LambdaQueryWrapper<BizItemLabel> queryWrapper = getLabelListQueryWrapper(request);
        // 分页查询
        PageHelper.startPage(request.getPageNo(), request.getPageSize(), true);
        List<BizItemLabel> labelList = bizItemLabelMapper.selectList(queryWrapper);
        PageInfo<BizItemLabel> pageInfo = new PageInfo<>(labelList);

        // 封装返回数据
        Map<Long, String> firstLabelMap = getFirstLabelMap();
        List<GetItemLabelListResponse.LabelModel> modelList = new ArrayList<>();
        for (BizItemLabel label : labelList){
            GetItemLabelListResponse.LabelModel labelModel = new GetItemLabelListResponse.LabelModel();
            BeanUtils.copyProperties(label, labelModel);
            labelModel.setLabelId(label.getId());
            if(null != label.getLabelLevel()){
                labelModel.setLabelLevelName(LabelLevelEnum.getLookup().get(label.getLabelLevel()));
            }
            if(null != label.getFirstLabelId()){
                labelModel.setFirstLabelName(firstLabelMap.get(label.getFirstLabelId()));
            }
            modelList.add(labelModel);
        }
        GetItemLabelListResponse response = new GetItemLabelListResponse();
        response.setLabelList(modelList);
        response.setTotalNum(pageInfo.getTotal());
        response.setHasMore(pageInfo.isHasNextPage() ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        return response;
    }

    public GetFirstLabelListResponse getFirstLabelList(){
        LambdaQueryWrapper<BizItemLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizItemLabel::getLabelLevel, LabelLevelEnum.LEVEL_1.getCode());
        queryWrapper.eq(BizItemLabel::getDelFlag, IsTypeInteger.NO.getCode());
        List<BizItemLabel> labelList = bizItemLabelMapper.selectList(queryWrapper);
        GetFirstLabelListResponse response = new GetFirstLabelListResponse();
        List<GetFirstLabelListResponse.LabelModel> modelList = new ArrayList<>();
        if(CollectionUtils.isEmpty(labelList)){
            response.setLabelList(modelList);
            return response;
        }
        for (BizItemLabel label : labelList){
            GetFirstLabelListResponse.LabelModel labelModel = new GetFirstLabelListResponse.LabelModel();
            labelModel.setLabelId(label.getId());
            labelModel.setLabelName(label.getLabelName());
            modelList.add(labelModel);
        }
        response.setLabelList(modelList);
        return response;
    }

    private LambdaQueryWrapper<BizItemLabel> getLabelListQueryWrapper(GetItemLabelListRequest request){
        // 组装查询条件
        LambdaQueryWrapper<BizItemLabel> queryWrapper = new LambdaQueryWrapper<>();
        if(!CollectionUtils.isEmpty(request.getLabelIdList())){
            queryWrapper.in(BizItemLabel::getId, request.getLabelIdList());
        }
        if(null != request.getLabelLevel()){
            queryWrapper.eq(BizItemLabel::getLabelLevel, request.getLabelLevel());
        }

        if(null != request.getFirstLabelId()){
            queryWrapper.eq(BizItemLabel::getFirstLabelId, request.getFirstLabelId());
        }

        if(null != request.getLabelStatus()){
            queryWrapper.eq(BizItemLabel::getLabelStatus, request.getLabelStatus());
        }

        if(null != request.getDescription()){
            queryWrapper.like(BizItemLabel::getDescription, request.getDescription());
        }

        // 按照级别正序排列，按照创建时间倒序排列
        queryWrapper.eq(BizItemLabel::getDelFlag, IsTypeInteger.NO.getCode());
        queryWrapper.orderByAsc(BizItemLabel::getLabelLevel)
                .orderByDesc(BizItemLabel::getCreateTime);
        return queryWrapper;
    }

    private Map<Long, String> getFirstLabelMap(){
        LambdaQueryWrapper<BizItemLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizItemLabel::getLabelLevel, LabelLevelEnum.LEVEL_1.getCode());
        queryWrapper.eq(BizItemLabel::getDelFlag, IsTypeInteger.NO.getCode());
        List<BizItemLabel> labelList = bizItemLabelMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(labelList)){
            return new HashMap<>();
        }
        Map<Long, String> labelMap = new HashMap<>();
        for (BizItemLabel label : labelList){
            labelMap.put(label.getId(), label.getLabelName());
        }
        return labelMap;
    }

    public Map<Long, String> getLabelNameMap(Integer isFromDb){
        if(IsTypeInteger.NO.getCode().equals(isFromDb)){
            return labelNameMap;
        }
        List<BizItemLabel> labelList = bizItemLabelMapper.selectAll();
        if(CollectionUtils.isEmpty(labelList)){
            return labelNameMap;
        }
        for (BizItemLabel label : labelList){
            labelNameMap.put(label.getId(), label.getLabelName());
        }
        return labelNameMap;
    }

    public Map<Long, String> getLabelNameMapFromRedis(){
        Map<Long, String> labelNameMap = (Map<Long, String>) redisUtils.get("labelNameMap");
        if(!CollectionUtils.isEmpty(labelNameMap)){
            return labelNameMap ;
        }
        labelNameMap = getLabelNameMap(IsTypeInteger.YES.getCode());
        redisUtils.set("labelNameMap", labelNameMap, 30, TimeUnit.SECONDS);
        return labelNameMap;
    }

}
