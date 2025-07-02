package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.enums.LabelLevelEnum;
import me.zhengjie.modules.business.rest.request.CreateItemLabelRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemLabelRequest;
import me.zhengjie.modules.business.rest.request.GetItemLabelListRequest;
import me.zhengjie.modules.business.rest.request.UpdateItemLabelStatusRequest;
import me.zhengjie.modules.business.rest.response.CreateItemLabelResponse;
import me.zhengjie.modules.business.rest.response.GetFirstLabelListResponse;
import me.zhengjie.modules.business.rest.response.GetItemLabelListResponse;
import me.zhengjie.modules.business.service.ItemLabelService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品标签")
@RequestMapping("/item/label")
public class ItemLabelController {
    @Autowired
    private ItemLabelService itemLabelService;

    @ApiOperation("创建/更新产品标签")
    @AnonymousPostMapping(value = "/v1/createOrUpdateItemLabel")
    public ResponseEntity<CreateItemLabelResponse> createOrUpdateItemLabel(@RequestBody CreateItemLabelRequest request) throws Exception {
        Integer labelLevel = request.getLabelLevel();
        if(null == labelLevel || 0 == labelLevel){
            throw new BadRequestException("标签等级能为空");
        }
        if(LabelLevelEnum.LEVEL_2.getCode().equals(labelLevel) &&
                (null == request.getFirstLabelId() || 0 == request.getFirstLabelId())){
            throw new BadRequestException("创建二级标签时，一级标签不能为空");
        }
        request.setUserId(this.checkUserId(request.getUserId()));
        CreateItemLabelResponse response = itemLabelService.createItemLabel(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("更新产品标签状态")
    @AnonymousPostMapping(value = "/v1/updateItemLabelStatus")
    public ResponseEntity<Object> updateItemLabelStatus(@RequestBody UpdateItemLabelStatusRequest request) throws Exception {
        if(null == request.getLabelId() || null == request.getLabelStatus()){
            throw new BadRequestException("标签ID、状态不能为空");
        }
        request.setUserId(this.checkUserId(request.getUserId()));
        itemLabelService.updateItemLabelStatus(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("删除产品标签")
    @AnonymousPostMapping(value = "/v1/deleteItemLabel")
    public ResponseEntity<Object> deleteItemLabel(@RequestBody DeleteItemLabelRequest request) throws Exception {
        if(CollectionUtils.isEmpty(request.getLabelIdList())){
            throw new BadRequestException("标签ID不能为空");
        }
        request.setUserId(this.checkUserId(request.getUserId()));
        itemLabelService.deleteItemLabel(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("查询产品标签列表")
    @PostMapping(value = "/v1/getItemLabelList")
//    @PreAuthorize("@el.check('label:list')")
    public ResponseEntity<GetItemLabelListResponse> getItemLabelList(@RequestBody GetItemLabelListRequest request) throws Exception {
        GetItemLabelListResponse response = itemLabelService.getItemLabelList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("查询一级产品标签列表")
    @PostMapping(value = "/v1/getFirstLabelList")
    public ResponseEntity<GetFirstLabelListResponse> getFirstLabelList() throws Exception {
        GetFirstLabelListResponse response = itemLabelService.getFirstLabelList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Long checkUserId(Long userId){
        if(null == userId){
            userId = SecurityUtils.getCurrentUserId();
        }
        if(null == userId){
            throw new BadRequestException("用户ID不能为空");
        }
        return userId;
    }

}
