package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.*;
import me.zhengjie.modules.business.rest.response.CreateItemDetailResponse;
import me.zhengjie.modules.business.rest.response.GetItemDetailListResponse;
import me.zhengjie.modules.business.service.ItemDetailExportService;
import me.zhengjie.modules.business.service.ItemDetailService;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品管理")
@RequestMapping("/api/product")
public class ItemDetailController {

    @Autowired
    private ItemDetailService itemDetailService;
    @Autowired
    private ItemDetailExportService itemDetailExportService;

    @ApiOperation("导出产品列表")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('product:list')")
    public void exportItemDetailList(GetItemDetailListRequest request, HttpServletResponse servletResponse) throws Exception {
        itemDetailExportService.exportItemDetailList(request, servletResponse.getOutputStream());
    }

    @ApiOperation("查询产品列表")
    @GetMapping
    @PreAuthorize("@el.check('product:list')")
    public ResponseEntity<PageResult<GetItemDetailListResponse.ItemModel>> getItemDetailList(GetItemDetailListRequest request) throws Exception {
        GetItemDetailListResponse response = itemDetailService.getItemDetailList(request);
        PageResult<GetItemDetailListResponse.ItemModel> page = PageUtil.toPage(response.getItemList(), response.getTotalNum());
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Log("创建/更新产品信息")
    @ApiOperation("创建/更新产品信息")
    @PostMapping
    @PreAuthorize("@el.check('product:add')")
    public ResponseEntity<CreateItemDetailResponse> createOrUpdateItemDetail(CreateItemDetailRequest request) throws Exception {
        CreateItemDetailResponse response = itemDetailService.createOrUpdateItemDetail(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Log("更新产品状态")
    @ApiOperation("更新产品状态")
    @PutMapping
    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> updateItemStatus(UpdateItemStatusRequest request) throws Exception {
        if(null == request.getItemId() || null == request.getItemStatus()){
            throw new BadRequestException("产品ID、状态不能为空");
        }
        itemDetailService.updateItemStatus(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Log("删除产品信息")
    @ApiOperation("删除产品信息")
    @DeleteMapping
    @PreAuthorize("@el.check('product:del')")
    public ResponseEntity<Object> deleteItemDetail(DeleteItemDetailRequest request) throws Exception {
        if(null == request.getItemId()){
            throw new BadRequestException("产品ID、状态不能为空");
        }
        itemDetailService.deleteItemDetail(request);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
