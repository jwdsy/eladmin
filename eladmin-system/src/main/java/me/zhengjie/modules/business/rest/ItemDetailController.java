package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.service.ItemDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Api(tags = "管理：产品详情")
@RequestMapping("/item/detail")
public class ItemDetailController {

    @Autowired
    private ItemDetailService itemDetailService;

    @ApiOperation("创建/更新产品信息")
    @AnonymousPostMapping(value = "/v1/createOrUpdateItemDetail")
    public ResponseEntity<CreateItemDetailResponse> createOrUpdateItemDetail(@RequestBody CreateItemDetailRequest request) throws Exception {
        CreateItemDetailResponse response = itemDetailService.createOrUpdateItemDetail(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("更新产品状态")
    @AnonymousPostMapping(value = "/v1/updateItemStatus")
    public ResponseEntity<Object> updateItemStatus(@RequestBody UpdateItemStatusRequest request) throws Exception {
        if(null == request.getItemId() || null == request.getItemStatus()){
            throw new BadRequestException("产品ID、状态不能为空");
        }
        itemDetailService.updateItemStatus(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("删除产品信息")
    @AnonymousPostMapping(value = "/v1/deleteItemDetail")
    public ResponseEntity<Object> deleteItemDetail(@RequestBody DeleteItemDetailRequest request) throws Exception {
        if(null == request.getItemId()){
            throw new BadRequestException("产品ID、状态不能为空");
        }
        itemDetailService.deleteItemDetail(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("查询产品列表")
    @AnonymousPostMapping(value = "/v1/getItemDetailList")
    public ResponseEntity<GetItemDetailListResponse> getItemDetailList(@RequestBody GetItemDetailListRequest request) throws Exception {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("导出产品列表")
    @AnonymousPostMapping(value = "/v1/exportItemDetailList")
    public void customerSubmitItem(@RequestBody GetItemDetailListRequest request, HttpServletResponse servletResponse) throws Exception {

    }
}
