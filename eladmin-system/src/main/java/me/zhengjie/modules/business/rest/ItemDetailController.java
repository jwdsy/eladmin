package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.business.rest.request.CreateItemDetailRequest;
import me.zhengjie.modules.business.rest.request.CustomerSubmitItemRequest;
import me.zhengjie.modules.business.rest.request.DeleteItemDetailRequest;
import me.zhengjie.modules.business.rest.request.GetItemDetailListRequest;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
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
@Api(tags = "管理：产品详情")
@RequestMapping("/item/detail")
public class ItemDetailController {



    @ApiOperation("创建/更新产品信息")
    @AnonymousPostMapping(value = "/v1/createOrUpdateItemDetail")
    public ResponseEntity<CreateItemDetailResponse> createOrUpdateItemDetail(@RequestBody CreateItemDetailRequest request) throws Exception {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("删除产品信息")
    @AnonymousPostMapping(value = "/v1/deleteItemDetail")
    public ResponseEntity<CreateItemDetailResponse> deleteItemDetail(@RequestBody DeleteItemDetailRequest request) throws Exception {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("查询产品列表")
    @AnonymousPostMapping(value = "/v1/getItemDetailList")
    public ResponseEntity<GetItemDetailListResponse> getItemDetailList(@RequestBody GetItemDetailListRequest request) throws Exception {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("导出产品列表")
    @AnonymousPostMapping(value = "/v1/exportItemDetailList")
    public ResponseEntity<Object> customerSubmitItem(@RequestBody GetItemDetailListRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
