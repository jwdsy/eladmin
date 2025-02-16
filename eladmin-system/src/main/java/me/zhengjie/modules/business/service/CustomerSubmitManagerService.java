package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizCustomerSubmitDetail;
import me.zhengjie.modules.business.domain.BizCustomerSubmitRecord;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizCustomerSubmitDetailMapper;
import me.zhengjie.modules.business.repository.BizCustomerSubmitRecordMapper;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.GetSubmitItemListRequest;
import me.zhengjie.modules.business.rest.request.GetSubmitRecordListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.rest.response.GetSubmitRecordListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/2/15
 */
@Slf4j
@Service
public class CustomerSubmitManagerService {
    @Resource
    private BizCustomerSubmitRecordMapper bizCustomerSubmitRecordMapper;
    @Resource
    private BizCustomerSubmitDetailMapper bizCustomerSubmitDetailMapper;
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;
    @Autowired
    private ItemDetailService itemDetailService;
    @Autowired
    private CustomerPickManagerService customerPickManagerService;

    public GetSubmitRecordListResponse getCustomerSubmitRecordList(GetSubmitRecordListRequest request){
        LambdaQueryWrapper<BizCustomerSubmitRecord> queryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getUserId()){
            queryWrapper.eq(BizCustomerSubmitRecord::getUserId,request.getUserId());
        }
        if(null!= request.getStartDate() && null!= request.getEndDate()){
            queryWrapper.ge(BizCustomerSubmitRecord::getCreateTime,request.getStartDate())
                    .le(BizCustomerSubmitRecord::getCreateTime,request.getEndDate());
        }
        PageHelper.startPage(request.getPageNo(), request.getPageSize(), true);
        List<BizCustomerSubmitRecord> recordList = bizCustomerSubmitRecordMapper.selectList(queryWrapper);

        List<GetSubmitRecordListResponse.SubmitModel> modelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(recordList)){
            for (BizCustomerSubmitRecord record : recordList) {
                GetSubmitRecordListResponse.SubmitModel model = new GetSubmitRecordListResponse.SubmitModel();
                model.setRecordId(record.getId());
                model.setSubmitTime(record.getCreateTime());
                modelList.add(model);
            }
        }
        PageInfo<BizCustomerSubmitRecord> pageInfo = new PageInfo<>(recordList);
        GetSubmitRecordListResponse response = new GetSubmitRecordListResponse();
        response.setTotalNum(pageInfo.getTotal());
        response.setHasMore(pageInfo.isHasNextPage() ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        response.setRecordList(modelList);
        return response;
    }

    public GetItemDetailListResponse getCustomerSubmitItemList(GetSubmitItemListRequest request){
        Map<Long, String> pickRemarkMap = getSubmitRecordList(request);

        // 1、封装查询条件
        LambdaQueryWrapper<BizItemBaseRecord> ItemQueryWrapper = new LambdaQueryWrapper<>();
        ItemQueryWrapper.in(BizItemBaseRecord::getId, pickRemarkMap.keySet());
        // 2、分页查询
        PageHelper.startPage(request.getPageNo(), request.getPageSize(), true);
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(ItemQueryWrapper);
        PageInfo<BizItemBaseRecord> pageInfo = new PageInfo<>(recordList);

        // 3、封装返回结果
        List<GetItemDetailListResponse.ItemModel> itemModelList = customerPickManagerService.getItemModelList(recordList, pickRemarkMap);
        GetItemDetailListResponse response = new GetItemDetailListResponse();
        response.setItemList(itemModelList);
        response.setTotalNum(pageInfo.getTotal());
        response.setHasMore(pageInfo.isHasNextPage() ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        return response;
    }

    public Map<Long, String> getSubmitRecordList(GetSubmitItemListRequest request){
        if(null == request.getSubmitId()){
            throw new BadRequestException("提交ID不能为空");
        }
        if(null == request.getUserId()){
            throw new BadRequestException("用户ID不能为空");
        }

        // 查询提交记录
        LambdaQueryWrapper<BizCustomerSubmitDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizCustomerSubmitDetail::getId,request.getSubmitId())
                .eq(BizCustomerSubmitDetail::getUserId,request.getUserId())
                .orderByDesc(BizCustomerSubmitDetail::getCreateTime);
        List<BizCustomerSubmitDetail> submitRecordList = bizCustomerSubmitDetailMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(submitRecordList)){
            throw new BadRequestException("未查询到用户提交信息");
        }
        Map<Long, String> pickRemarkMap = new HashMap<>();
        for(BizCustomerSubmitDetail submitDetail : submitRecordList){
            pickRemarkMap.put(submitDetail.getItemId(), submitDetail.getItemRemark());
        }
        return pickRemarkMap;
    }

}
