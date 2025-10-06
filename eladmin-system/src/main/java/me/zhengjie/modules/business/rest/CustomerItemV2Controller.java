package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.GetDisplayItemListResponse;
import me.zhengjie.modules.business.rest.response.GetDisplayLabelListResponse;
import me.zhengjie.modules.business.rest.response.GetItemShowListResponse;
import me.zhengjie.modules.business.service.CustomerItemDisplayService;
import me.zhengjie.modules.business.service.CustomerItemPickService;
import me.zhengjie.modules.business.service.CustomerItemShowService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "用户：顾客产品V2")
@RequestMapping("/api/item")
public class CustomerItemV2Controller {
    @Autowired
    private CustomerItemDisplayService customerItemDisplayService;
    @Autowired
    private CustomerItemPickService customerItemPickService;
    @Autowired
    private CustomerItemShowService customerItemShowService;

    @ApiOperation("获取展示产品标签列表")
    @PostMapping(value = "/v2/getDisplayLabelList")
    @PreAuthorize("@el.check('clabel:list')")
    public ResponseEntity<GetDisplayLabelListResponse> getDisplayLabelList(@RequestBody GetDisplayLabelListRequest request) throws Exception {
        GetDisplayLabelListResponse response = customerItemDisplayService.getDisplayLabelList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("获取展示产品列表")
    @PostMapping(value = "/v2/getDisplayItemList")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<GetDisplayItemListResponse> getDisplayItemList(@RequestBody GetDisplayItemListRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        GetDisplayItemListResponse response = customerItemDisplayService.getDisplayItemList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("顾客选择喜欢的产品")
    @PostMapping(value = "/v2/customerPickItem")
    public ResponseEntity<Object> customerPickItem(@RequestBody CustomerPickItemRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.customerPickItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客取消全部喜欢的产品")
    @PostMapping(value = "/v2/cancelAllPickItems")
    public ResponseEntity<Object> cancelAllPickItems(@RequestBody CancelAllPickItemsRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.cancelAllPickItems(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客提交喜欢的产品")
    @PostMapping(value = "/v2/customerSubmitItem")
    public ResponseEntity<Object> customerSubmitItem(@RequestBody CustomerSubmitItemRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.customerSubmitItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("获取商品展览列表")
    @PostMapping(value = "/v2/getItemShowList")
    public ResponseEntity<GetItemShowListResponse> getItemShowList(@RequestBody GetItemShowListRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        GetItemShowListResponse response = customerItemShowService.getItemShowList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
