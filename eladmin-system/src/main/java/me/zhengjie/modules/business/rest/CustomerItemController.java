package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping(value = "/label/list")
    @PreAuthorize("@el.check('clabel:list')")
    public ResponseEntity<GetDisplayLabelListResponse> getDisplayLabelList(GetDisplayLabelListRequest request) throws Exception {
        GetDisplayLabelListResponse response = customerItemDisplayService.getDisplayLabelList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("获取展示产品列表")
    @GetMapping(value = "/list")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<GetDisplayItemListResponse> getDisplayItemList(GetDisplayItemListRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        GetDisplayItemListResponse response = customerItemDisplayService.getDisplayItemList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("顾客选择喜欢的产品")
    @PostMapping(value = "/pick")
    public ResponseEntity<Object> customerPickItem(CustomerPickItemRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.customerPickItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客取消全部喜欢的产品")
    @PostMapping(value = "/clean")
    public ResponseEntity<Object> cancelAllPickItems(CancelAllPickItemsRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.cancelAllPickItems(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("顾客提交喜欢的产品")
    @PostMapping(value = "/submit")
    public ResponseEntity<Object> customerSubmitItem(CustomerSubmitItemRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        customerItemPickService.customerSubmitItem(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
