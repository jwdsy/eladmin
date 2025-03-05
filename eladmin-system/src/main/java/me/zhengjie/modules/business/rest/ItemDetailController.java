package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.CreateItemDetailRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemDetailRequest;
import me.zhengjie.modules.business.rest.request.GetItemDetailListRequest;
import me.zhengjie.modules.business.rest.request.UpdateItemStatusRequest;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.service.ItemDetailExportService;
import me.zhengjie.modules.business.service.ItemDetailService;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品管理")
@RequestMapping("/api/product")
public class ItemDetailController {

    @Autowired
    private ItemDetailService itemDetailService;
    @Autowired
    private ItemDetailExportService itemDetailExportService;

    @ApiOperation("导出产品列表")
    @PostMapping(value = "/v1/exportItemDetailList")
    @PreAuthorize("@el.check('product:list')")
    public void exportItemDetailList(@RequestBody GetItemDetailListRequest request, HttpServletResponse servletResponse) throws Exception {
        itemDetailExportService.exportItemDetailList(request, servletResponse.getOutputStream());
    }

    @ApiOperation("查询产品列表")
    @PostMapping(value = "/v1/getItemDetailList")
    @PreAuthorize("@el.check('product:list')")
    public ResponseEntity<GetItemDetailListResponse> getItemDetailList(@RequestBody GetItemDetailListRequest request) throws Exception {
        GetItemDetailListResponse response = itemDetailService.getItemDetailList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Log("创建/更新产品信息")
    @ApiOperation("创建/更新产品信息")
    @PostMapping(value = "/v1/createOrUpdateItemDetail")
    @PreAuthorize("@el.check('product:add')")
    public ResponseEntity<CreateItemDetailResponse> createOrUpdateItemDetail(@RequestBody CreateItemDetailRequest request) throws Exception {
        request.setUserId(this.checkUserId(request.getUserId()));
        CreateItemDetailResponse response = itemDetailService.createOrUpdateItemDetail(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Log("更新产品状态")
    @ApiOperation("更新产品状态")
    @PostMapping(value = "/v1/updateItemStatus")
    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> updateItemStatus(@RequestBody UpdateItemStatusRequest request) throws Exception {
        if(null == request.getItemId() || null == request.getItemStatus()){
            throw new BadRequestException("产品ID、状态不能为空");
        }
        request.setUserId(this.checkUserId(request.getUserId()));
        itemDetailService.updateItemStatus(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Log("删除产品信息")
    @ApiOperation("删除产品信息")
    @PostMapping(value = "/v1/deleteItemDetail")
    @PreAuthorize("@el.check('product:del')")
    public ResponseEntity<Object> deleteItemDetail(@RequestBody DeleteItemDetailRequest request) throws Exception {
        if(CollectionUtils.isEmpty(request.getItemIdList())){
            throw new BadRequestException("产品ID不能为空");
        }
        request.setUserId(this.checkUserId(request.getUserId()));
        itemDetailService.deleteItemDetail(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
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
