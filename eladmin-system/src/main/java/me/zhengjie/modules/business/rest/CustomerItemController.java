package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.GetDisplayItemListResponse;
import me.zhengjie.modules.business.rest.response.GetDisplayLabelListResponse;
import me.zhengjie.modules.business.service.CustomerItemDisplayService;
import me.zhengjie.modules.business.service.CustomerItemPickService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "用户：顾客产品")
@RequestMapping("/api/item")
public class CustomerItemController {
    @Autowired
    private CustomerItemDisplayService customerItemDisplayService;
    @Autowired
    private CustomerItemPickService customerItemPickService;

    @ApiOperation("获取展示产品标签列表")
    @AnonymousPostMapping(value = "/v1/getDisplayLabelList")
    public ResponseEntity<GetDisplayLabelListResponse> getDisplayLabelList(@RequestBody GetDisplayLabelListRequest request) throws Exception {
        GetDisplayLabelListResponse response = customerItemDisplayService.getDisplayLabelList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("获取展示产品列表")
//    @GetMapping
    @GetMapping(value = "/list")
    @PreAuthorize("@el.check('product:list')")
    public ResponseEntity<GetDisplayItemListResponse> getDisplayItemList(GetDisplayItemListRequest request) throws Exception {
        request.setUserId(SecurityUtils.getCurrentUserId());
        GetDisplayItemListResponse response = customerItemDisplayService.getDisplayItemList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("顾客选择喜欢的产品")
    @AnonymousPostMapping(value = "/v1/customerPickItem")
    public ResponseEntity<Object> customerPickItem(@RequestBody CustomerPickItemRequest request) throws Exception {
        if(null == request.getUserId()){
            throw new BadRequestException("用户ID不能为空");
        }
        customerItemPickService.customerPickItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客取消全部喜欢的产品")
    @AnonymousPostMapping(value = "/v1/cancelAllPickItems")
    public ResponseEntity<Object> cancelAllPickItems(@RequestBody CancelAllPickItemsRequest request) throws Exception {
        if(null == request.getUserId()){
            throw new BadRequestException("用户ID不能为空");
        }
        customerItemPickService.cancelAllPickItems(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客提交喜欢的产品")
    @AnonymousPostMapping(value = "/v1/customerSubmitItem")
    public ResponseEntity<Object> customerSubmitItem(@RequestBody CustomerSubmitItemRequest request) throws Exception {
        if(null == request.getUserId()){
            throw new BadRequestException("用户ID不能为空");
        }
        customerItemPickService.customerSubmitItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
