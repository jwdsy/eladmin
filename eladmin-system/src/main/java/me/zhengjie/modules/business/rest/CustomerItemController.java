package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.GetDisplayItemListResponse;
import me.zhengjie.modules.business.rest.response.GetDisplayLabelListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/customer/item")
public class CustomerItemController {

    @ApiOperation("获取展示产品标签列表")
    @AnonymousPostMapping(value = "/v1/getDisplayLabelList")
    public ResponseEntity<GetDisplayLabelListResponse> getDisplayLabelList(@RequestBody GetDisplayLabelListRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("获取展示产品列表")
    @AnonymousPostMapping(value = "/v1/getCustomerItemList")
    public ResponseEntity<GetDisplayItemListResponse> getDisplayItemList(@RequestBody GetDisplayItemListRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客选择喜欢的产品")
    @AnonymousPostMapping(value = "/v1/customerPickItem")
    public ResponseEntity<Object> customerPickItem(@RequestBody CustomerPickItemRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客取消全部喜欢的产品")
    @AnonymousPostMapping(value = "/v1/cancelAllPickItems")
    public ResponseEntity<Object> cancelAllPickItems(@RequestBody CancelAllPickItemsRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客提交喜欢的产品")
    @AnonymousPostMapping(value = "/v1/customerSubmitItem")
    public ResponseEntity<Object> customerSubmitItem(@RequestBody CustomerSubmitItemRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
