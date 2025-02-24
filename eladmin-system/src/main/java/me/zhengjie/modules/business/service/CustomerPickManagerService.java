package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.GetDisplayPickItemListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/2/15
 */
@Slf4j
@Service
public class CustomerPickManagerService {
    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;
    @Autowired
    private ItemDetailService itemDetailService;
    @Autowired
    private CustomerItemDisplayService customerItemDisplayService;
    @Autowired
    private ItemDetailExportService itemDetailExportService;


    public GetItemDetailListResponse getCustomerPickItemList(GetDisplayPickItemListRequest request){
        // 没传用户，或者没有用户喜欢的商品，则不查询
        if(null == request.getCustomerUserId()){
            return null;
        }
        Map<Long, String> pickRemarkMap = customerItemDisplayService.getPickItemMap(request.getCustomerUserId());
        if(CollectionUtils.isEmpty(pickRemarkMap)){
            return null;
        }
        // 1、封装查询条件
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = itemDetailService.getItemListQueryWrapper(request);
        queryWrapper.in(BizItemBaseRecord::getId, pickRemarkMap.keySet())
                .orderByAsc(BizItemBaseRecord::getId);
        // 2、分页查询
        PageHelper.startPage(request.getPage(), request.getSize(), true);
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);
        PageInfo<BizItemBaseRecord> pageInfo = new PageInfo<>(recordList);

        // 3、封装返回结果
        List<GetItemDetailListResponse.ItemModel> itemModelList= getItemModelList(recordList, pickRemarkMap);
        GetItemDetailListResponse response = new GetItemDetailListResponse();
        response.setItemList(itemModelList);
        response.setTotalNum(pageInfo.getTotal());
        response.setHasMore(pageInfo.isHasNextPage() ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        return response;
    }

    public void exportCustomerPickItemList(GetDisplayPickItemListRequest request, OutputStream out) throws Exception {
        Map<Long, String> pickRemarkMap = customerItemDisplayService.getPickItemMap(request.getCustomerUserId());
        if(CollectionUtils.isEmpty(pickRemarkMap)){
            throw new BadRequestException("没有查询到要导出的结果");
        }
        // 1、封装查询条件
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = itemDetailService.getItemListQueryWrapper(request);
        queryWrapper.in(BizItemBaseRecord::getId, pickRemarkMap.keySet())
                .orderByAsc(BizItemBaseRecord::getId);
        // 2、查询
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);
        // 3、封装返回结果
        List<GetItemDetailListResponse.ItemModel> itemModelList= getItemModelList(recordList, pickRemarkMap);
        // 4、导出数据
        itemDetailExportService.exportItemDetailList(itemModelList, out);
    }

    public List<GetItemDetailListResponse.ItemModel> getItemModelList(List<BizItemBaseRecord> recordList, Map<Long, String> pickRemarkMap){
        List<GetItemDetailListResponse.ItemModel> itemModelList = itemDetailService.getItemModelList(recordList, IsTypeInteger.YES.getCode());
        for (GetItemDetailListResponse.ItemModel itemModel : itemModelList){
            itemModel.setPickRemark(pickRemarkMap.get(itemModel.getItemId()));
        }
        return itemModelList;
    }
}
