package me.zhengjie.modules.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.domain.BizItemBaseRecord;
import me.zhengjie.modules.business.enums.IsTypeInteger;
import me.zhengjie.modules.business.repository.BizItemBaseRecordMapper;
import me.zhengjie.modules.business.rest.request.CreateItemDetailRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemDetailRequest;
import me.zhengjie.modules.business.rest.request.GetItemDetailListRequest;
import me.zhengjie.modules.business.rest.request.UpdateItemStatusRequest;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.utils.BigDecimalUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/18
 */
@Slf4j
@Service
public class ItemDetailService {
    public static final Integer GP_20 = 27;
    public static final Integer GP_40 = 55;
    public static final Integer HC_40 = 65;

    @Resource
    private BizItemBaseRecordMapper bizItemBaseRecordMapper;
    @Autowired
    private ItemLabelService itemLabelService;

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
        if(IsTypeInteger.YES.getCode().equals(record.getDelFlag())){
            throw new BadRequestException("该产品已被删除");
        }
        record.setItemStatus(request.getItemStatus());
        record.setLastModifyTime(new Date());
        record.setModifyUserId(request.getUserId());
        bizItemBaseRecordMapper.updateByPrimaryKey(record);
    }

    public void deleteItemDetail(DeleteItemDetailRequest request){
        bizItemBaseRecordMapper.batchDeleteByIds(request.getItemIdList(), request.getUserId());
    }

    public GetItemDetailListResponse getItemDetailList(GetItemDetailListRequest request){
        // 1、封装查询条件
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = getItemListQueryWrapper(request);
        // 2、分页查询
        PageHelper.startPage(request.getPage(), request.getSize(), true);
        List<BizItemBaseRecord> recordList = bizItemBaseRecordMapper.selectList(queryWrapper);
        PageInfo<BizItemBaseRecord> pageInfo = new PageInfo<>(recordList);

        // 3、封装返回结果
        List<GetItemDetailListResponse.ItemModel> itemModelList = getItemModelList(recordList,
                // 第一页就从数据库取标签
                Integer.valueOf(1).equals(request.getPage()) ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        GetItemDetailListResponse response = new GetItemDetailListResponse();
        response.setItemList(itemModelList);
        response.setTotalNum(pageInfo.getTotal());
        response.setHasMore(pageInfo.isHasNextPage() ? IsTypeInteger.YES.getCode() : IsTypeInteger.NO.getCode());
        return response;
    }

    public LambdaQueryWrapper<BizItemBaseRecord> getItemListQueryWrapper(GetItemDetailListRequest request){
        LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = new LambdaQueryWrapper<>();

        if(!CollectionUtils.isEmpty(request.getItemIdList())){
            queryWrapper.in(BizItemBaseRecord::getId, request.getItemIdList());
        }
        if(!CollectionUtils.isEmpty(request.getItemNoList())){
            queryWrapper.in(BizItemBaseRecord::getItemNo, request.getItemNoList());
        }
        if(null != request.getFirstLabelId()){
            queryWrapper.eq(BizItemBaseRecord::getFirstLabelId, request.getFirstLabelId());
        }
        if(null!= request.getSecondLabelId()){
            queryWrapper.eq(BizItemBaseRecord::getSecondLabelId, request.getSecondLabelId());
        }
        if(!StringUtils.isBlank(request.getDescription())){
            queryWrapper.like(BizItemBaseRecord::getDescription, request.getDescription());
        }
        if(!StringUtils.isBlank(request.getFactoryName())){
            queryWrapper.like(BizItemBaseRecord::getFactoryName, request.getFactoryName());
        }
        queryWrapper.eq(BizItemBaseRecord::getDelFlag, IsTypeInteger.NO.getCode())
                .orderByDesc(BizItemBaseRecord::getId);
        return queryWrapper;
    }

    public List<GetItemDetailListResponse.ItemModel> getItemModelList(List<BizItemBaseRecord> recordList, Integer isFromDb){
        if(CollectionUtils.isEmpty(recordList)){
            return new ArrayList<>();
        }

        Map<Long, String> labelNameMap = itemLabelService.getLabelNameMapFromRedis();
        List<GetItemDetailListResponse.ItemModel> itemModelList = new ArrayList<>();
        for (BizItemBaseRecord record : recordList){
            GetItemDetailListResponse.ItemModel itemModel = new GetItemDetailListResponse.ItemModel();
            itemModel.setItemId(record.getId());
            itemModel.setItemNo(record.getItemNo());
            itemModel.setItemPic(record.getItemPic());
            itemModel.setDescription(record.getDescription());
            itemModel.setDeliveryPort(record.getDeliveryPort());
            itemModel.setItemLength(BigDecimalUtil.convertToStrTowScale(record.getItemLength()));
            itemModel.setItemWidth(BigDecimalUtil.convertToStrTowScale(record.getItemWidth()));
            itemModel.setItemHeight(BigDecimalUtil.convertToStrTowScale(record.getItemHeight()));
            itemModel.setInnerBox(record.getInnerBox());
            itemModel.setOuterCtn(record.getOuterCtn());
            itemModel.setWeightPieces(BigDecimalUtil.convertToStrTowScale(record.getWeightPieces()));
            itemModel.setMininumOrderQuantity(record.getMininumOrderQuantity());
            itemModel.setCartonLength(BigDecimalUtil.convertToStrTowScale(record.getCartonLength()));
            itemModel.setCartonWidth(BigDecimalUtil.convertToStrTowScale(record.getCartonWidth()));
            itemModel.setCartonHeight(BigDecimalUtil.convertToStrTowScale(record.getCartonHeight()));
            itemModel.setUnitPrice(BigDecimalUtil.convertToStrTowScale(record.getUnitPrice()));
            itemModel.setFactoryName(record.getFactoryName());
            itemModel.setItemCraft(record.getItemCraft());
//            itemModel.setUnitPrice4Dollar();
            BigDecimal cbmPerCTN = getCbmPerCTN(record.getCartonLength(), record.getCartonWidth(), record.getCartonHeight());
            if(!BigDecimalUtil.isEmpty(cbmPerCTN)){
                itemModel.setCbmPerCTN(cbmPerCTN.toString());
            }
            BigDecimal qtyIn20GP = getQtyInGP(GP_20, cbmPerCTN, record.getOuterCtn());
            if(!BigDecimalUtil.isEmpty(qtyIn20GP)){
                itemModel.setQtyIn20GP(qtyIn20GP.toString());
            }
            BigDecimal qtyIn40GP = getQtyInGP(GP_40, cbmPerCTN, record.getOuterCtn());
            if(!BigDecimalUtil.isEmpty(qtyIn40GP)){
                itemModel.setQtyIn40GP(qtyIn40GP.toString());
            }
            BigDecimal qtyIn40HC = getQtyInGP(HC_40, cbmPerCTN, record.getOuterCtn());
            if(!BigDecimalUtil.isEmpty(qtyIn40HC)){
                itemModel.setQtyIn40HC(qtyIn40HC.toString());
            }

            itemModel.setFirstLabelId(record.getFirstLabelId());
            itemModel.setFirstLabelName(labelNameMap.get(record.getFirstLabelId()));
            itemModel.setSecondLabelId(record.getSecondLabelId());
            itemModel.setSecondLabelName(labelNameMap.get(record.getSecondLabelId()));
            itemModel.setYear(itemModel.getYear());
            itemModel.setSeason(record.getSeason());
            itemModel.setItemStatus(record.getItemStatus());
            itemModel.setItemRemark(record.getItemRemark());
            itemModel.setCreateTime(record.getCreateTime());
            itemModel.setLastModifyTime(record.getLastModifyTime());
            itemModelList.add(itemModel);
        }
        return itemModelList;
    }

    private BigDecimal getCbmPerCTN(BigDecimal itemLength, BigDecimal itemWidth, BigDecimal itemHeight){
        if (BigDecimalUtil.isEmpty(itemLength) || BigDecimalUtil.isEmpty(itemWidth) || BigDecimalUtil.isEmpty(itemHeight)){
            return BigDecimal.ZERO;
        }
        // 立方厘米 / 100 * 100 * 100 = 立方米，四舍五入保留三位小数  确定要四舍五入么？
        BigDecimal cbmPerCTN = itemLength.multiply(itemWidth).multiply(itemHeight)
                .divide(new BigDecimal(100 * 100 * 100), 6, BigDecimal.ROUND_HALF_UP);
        return cbmPerCTN;
    }

    private BigDecimal getQtyInGP(Integer gpNum,BigDecimal cbmPerCTN, Integer outerCtn){
        if (BigDecimalUtil.isEmpty(cbmPerCTN) || null == outerCtn){
            return BigDecimal.ZERO;
        }
        // (20GP  55HC  65HC) * 外箱数 / 体积 = 件数(四舍五入)
        return BigDecimal.valueOf(gpNum).multiply(BigDecimal.valueOf(outerCtn)).divide(cbmPerCTN, 0, BigDecimal.ROUND_HALF_UP);
    }
}
