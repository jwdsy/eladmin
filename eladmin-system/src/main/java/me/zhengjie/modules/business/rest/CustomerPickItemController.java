package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.business.rest.request.GetDisplayPickItemListRequest;
import me.zhengjie.modules.business.rest.request.GetSubmitItemListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.rest.response.GetSubmitItemListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/17
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：顾客喜欢的产品")
@RequestMapping("/customer/pick")
public class CustomerPickItemController {


    @ApiOperation("获取顾客喜欢的产品列表")
    @AnonymousPostMapping(value = "/v1/getCustomerPickItemList")
    public ResponseEntity<GetItemDetailListResponse> getCustomerPickItemList(@RequestBody GetDisplayPickItemListRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation("导出顾客喜欢的产品列表")
    @AnonymousPostMapping(value = "/v1/exportCustomerPickItemList")
    public void exportCustomerPickItemList(@RequestBody GetDisplayPickItemListRequest request, HttpServletResponse servletResponse) throws Exception {
    }

    @ApiOperation("获取顾客提交的产品列表")
    @AnonymousPostMapping(value = "/v1/getCustomerSubmitItemList")
    public ResponseEntity<GetSubmitItemListResponse> getCustomerSubmitItemList(@RequestBody GetSubmitItemListRequest request) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
