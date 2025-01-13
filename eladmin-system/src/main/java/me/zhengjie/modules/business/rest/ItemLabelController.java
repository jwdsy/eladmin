package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.CreateItemLabelRequest;
import me.zhengjie.modules.business.rest.response.CreateItemLabelResponse;
import me.zhengjie.modules.business.service.ItemLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/13
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品标签")
@RequestMapping("/item/label")
public class ItemLabelController {
    @Autowired
    private ItemLabelService itemLabelService;

    @ApiOperation("创建产品标签")
    @AnonymousPostMapping(value = "/v1/createItemLabel")
    public ResponseEntity<CreateItemLabelResponse> createItemLabel(@Validated @RequestBody CreateItemLabelRequest request) throws Exception {
        if(null == request.getLabelLevel()){
            throw new BadRequestException("标签等级能为空");
        }

        CreateItemLabelResponse response = itemLabelService.createItemLabel(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
