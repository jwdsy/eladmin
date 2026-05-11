package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.GetItemShowListResponse;
import me.zhengjie.modules.business.service.CustomerItemShowService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private CustomerItemShowService customerItemShowService;

    @ApiOperation("获取商品展览列表")
    @PostMapping(value = "/v1/getItemShowList")
    public ResponseEntity<GetItemShowListResponse> getItemShowList(@RequestBody GetItemShowListRequest request) throws Exception {
        if(null == request.getUserId()){
            request.setUserId(SecurityUtils.getCurrentUserId());
        }
        GetItemShowListResponse response = customerItemShowService.getItemShowList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
