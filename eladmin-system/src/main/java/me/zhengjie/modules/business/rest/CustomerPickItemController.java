package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.GetDisplayPickItemListRequest;
import me.zhengjie.modules.business.rest.request.GetSimpleSubmitItemListRequest;
import me.zhengjie.modules.business.rest.request.GetSubmitRecordListRequest;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.rest.response.GetSubmitRecordListResponse;
import me.zhengjie.modules.business.service.CustomerPickManagerService;
import me.zhengjie.modules.business.service.CustomerSubmitManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

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
    @Autowired
    private CustomerPickManagerService customerPickManagerService;
    @Autowired
    private CustomerSubmitManagerService customerSubmitManagerService;


    @ApiOperation("获取顾客喜欢的产品列表")
    @AnonymousPostMapping(value = "/v1/getCustomerPickItemList")
    public ResponseEntity<GetItemDetailListResponse> getCustomerPickItemList(@RequestBody GetDisplayPickItemListRequest request) throws Exception {
        GetItemDetailListResponse response = customerPickManagerService.getCustomerPickItemList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("导出顾客喜欢的产品列表")
    @AnonymousPostMapping(value = "/v1/exportCustomerPickItemList")
    public void exportCustomerPickItemList(@RequestBody GetDisplayPickItemListRequest request, HttpServletResponse servletResponse) throws Exception {
        // 查询条件对应的UserId
        if(null == request.getCustomerUserId()){
            throw new BadRequestException("用户ID不能为空");
        }
        customerPickManagerService.exportCustomerPickItemList(request, servletResponse.getOutputStream());
    }

    @ApiOperation("获取顾客提交记录列表")
    @AnonymousPostMapping(value = "/v1/getCustomerSubmitRecordList")
    public ResponseEntity<GetSubmitRecordListResponse> getCustomerSubmitRecordList(@RequestBody GetSubmitRecordListRequest request) throws Exception {
        GetSubmitRecordListResponse response = customerSubmitManagerService.getCustomerSubmitRecordList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("获取顾客单次提交的产品列表")
    @AnonymousPostMapping(value = "/v1/getSimpleSubmitItemList")
    public ResponseEntity<GetItemDetailListResponse> getSimpleSubmitItemList(@RequestBody GetSimpleSubmitItemListRequest request) throws Exception {
        GetItemDetailListResponse response = customerSubmitManagerService.getSimpleSubmitItemList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("导出顾客单次提交的产品列表")
    @AnonymousPostMapping(value = "/v1/exportSimpleSubmitItemList")
    public ResponseEntity<GetItemDetailListResponse> exportSimpleSubmitItemList(@RequestBody GetSimpleSubmitItemListRequest request, HttpServletResponse servletResponse) throws Exception {
        servletResponse.setContentType("application/octet-stream");
        servletResponse.setHeader("Content-Disposition", "attachment; filename=item.xlsx");
        customerSubmitManagerService.exportSimpleSubmitItemList2(request, servletResponse.getOutputStream());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @ApiOperation("导出顾客单次提交的产品列表2")
    @AnonymousPostMapping(value = "/v1/exportSimpleSubmitItemList2")
    public ResponseEntity<GetItemDetailListResponse> exportSimpleSubmitItemList2(@RequestBody GetSimpleSubmitItemListRequest request, HttpServletResponse servletResponse) throws Exception {
        // 1. 设置响应头
        servletResponse.setContentType("application/vnd.ms-excel");
//        servletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        servletResponse.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("填充后的数据", "UTF-8").replaceAll("\\+", "%20");
        servletResponse.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        customerSubmitManagerService.exportSimpleSubmitItemList2(request, servletResponse.getOutputStream());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}

