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
        // 校验商品编号不能为空
        if(StringUtils.isBlank(request.getItemNo())){
            throw new BadRequestException("商品编号不能为空");
        }
        
        if(null == request.getItemId()){
            // 新增商品
            // 校验商品编号是否已存在
            LambdaQueryWrapper<BizItemBaseRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BizItemBaseRecord::getItemNo, request.getItemNo())
                    .eq(BizItemBaseRecord::getDelFlag, IsTypeInteger.NO.getCode());
            BizItemBaseRecord existRecord = bizItemBaseRecordMapper.selectOne(queryWrapper);
            if(null != existRecord){
                throw new BadRequestException("商品编号已存在");
            }
            
            BizItemBaseRecord record = new BizItemBaseRecord();
            // 设置基本信息
            record = getBizItemBaseRecord(record, request);
            record.setItemNo(request.getItemNo());
            
            // 设置默认值
            record.setCreateTime(new Date());
            record.setCreateUserId(request.getUserId());
            
            bizItemBaseRecordMapper.insertSelective(record);
            response.setItemId(record.getId());
            
        }else {
            // 修改商品
            BizItemBaseRecord record = bizItemBaseRecordMapper.getByPrimaryKey(request.getItemId());
            if(null == record){
                throw new BadRequestException("未查到产品信息");
            }
            if(IsTypeInteger.YES.getCode().equals(record.getDelFlag())){
                throw new BadRequestException("该产品已被删除");
            }
            getBizItemBaseRecord(record, request);
            record.setLastModifyTime(new Date());
            record.setModifyUserId(request.getUserId());
            
            bizItemBaseRecordMapper.updateByPrimaryKey(record);
            response.setItemId(record.getId());
        }
        return response;
    }

    public BizItemBaseRecord getBizItemBaseRecord(BizItemBaseRecord record, CreateItemDetailRequest request){

        record.setItemCompressPic(request.getItemPic());
        // 设置itemPic字段，去掉compress路径
        if(!StringUtils.isBlank(request.getItemPic())){
            String itemPic = request.getItemPic().replace("/compress/", "/");
            record.setItemPic(itemPic);
        }
        record.setDescription(request.getDescription());
        record.setDeliveryPort(request.getDeliveryPort());
        record.setItemLength(request.getItemLength());
        record.setItemWidth(request.getItemWidth());
        record.setItemHeight(request.getItemHeight());
        record.setInnerBox(request.getInnerBox());
        record.setOuterCtn(request.getOuterCtn());
        record.setWeightPieces(request.getWeightPieces());
        record.setMininumOrderQuantity(request.getMininumOrderQuantity());
        record.setCartonLength(request.getCartonLength());
        record.setCartonWidth(request.getCartonWidth());
        record.setCartonHeight(request.getCartonHeight());
        record.setUnitPrice(request.getUnitPrice());
        record.setFactoryName(request.getFactoryName());
        record.setItemCraft(request.getItemCraft());
        record.setFirstLabelId(request.getFirstLabelId());
        record.setSecondLabelId(request.getSecondLabelId());
        record.setItemRemark(request.getItemRemark());
        record.setYear(request.getYear());
        record.setSeason(request.getSeason());
        return record;
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
        List<GetItemDetailListResponse.ItemModel> itemModelList = getItemModelList(recordList);
        // 图片换成压缩图片
        if(!CollectionUtils.isEmpty(itemModelList)){
            for (GetItemDetailListResponse.ItemModel itemModel : itemModelList){
                itemModel.setItemPic(itemModel.getItemDepressPic());
            }
        }
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
        if(null != request.getItemStatus()){
            queryWrapper.eq(BizItemBaseRecord::getItemStatus, request.getItemStatus());
        }
        queryWrapper.eq(BizItemBaseRecord::getDelFlag, IsTypeInteger.NO.getCode())
                .orderByDesc(BizItemBaseRecord::getId);
        return queryWrapper;
    }

    public List<GetItemDetailListResponse.ItemModel> getItemModelList(List<BizItemBaseRecord> recordList){
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
            itemModel.setItemDepressPic(record.getItemCompressPic());
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
            itemModel.setYear(record.getYear());
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
