/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.bizitem.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.bizitem.service.*;
import me.zhengjie.modules.bizitem.service.dto.*;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author Zheng Jie
 * @date 2026-05-07
 */
@RestController
@RequiredArgsConstructor
@ApiIgnore
@Api(tags = "C端商品页相关服务")
@RequestMapping("/api/bizitem")
public class ItemBaseRecordController {

    private final LabelService labelService;
    private final ItemBaseRecordService itemBaseRecordService;
    private final ItemDisplayService itemDisplayService;
    private final CustomerSubmitService customerSubmitService;
    private final CustomerPickService customerPickService;
    private final ItemShowRecordService itemShowRecordService;


    @ApiOperation("标签列表")
    @GetMapping("/label/list")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<List<LabelDto>> queryLabelList(LabelQueryCriteria criteria) throws Exception {
        // 默认只查询上线且未删除的标签
        criteria.setLabelStatus(1);
        criteria.setDelFlag(0);
        return new ResponseEntity<>(labelService.queryAllWithHierarchy(criteria), HttpStatus.OK);
    }

    @ApiOperation("商品分页查询")
    @GetMapping("/record/list")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<PageResult<ItemBaseRecordDto>> queryItemBaseRecord(ItemBaseRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        // 默认只查询上线且未删除的商品
        criteria.setItemStatus(1);
        criteria.setDelFlag(0);
        return new ResponseEntity<>(itemBaseRecordService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("购物车商品列表")
    @GetMapping("/cart/list")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<PageResult<ItemDisplayDto>> getDisplayItemList(ItemDisplayQueryCriteria criteria, Pageable pageable) throws Exception {
        // 如果没有传用户ID，使用当前登录用户ID
        if (criteria.getUserId() == null) {
            try {
                criteria.setUserId(SecurityUtils.getCurrentUserId());
            } catch (Exception e) {
                // 如果获取不到当前用户，设置为null
                criteria.setUserId(null);
            }
        }
        // 默认只查询上线且未删除的商品
        criteria.setItemStatus(1);
        criteria.setDelFlag(0);
        return new ResponseEntity<>(itemDisplayService.queryDisplayItems(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("加入购物车")
    @PostMapping("/cart/pick")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<Object> customerPickItem(@RequestParam Long itemId, 
                                                    @RequestParam(required = false) String itemRemark, 
                                                    @RequestParam Integer pickFlag) throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        customerPickService.pickItem(userId, itemId, itemRemark, pickFlag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("提交购物车")
    @PostMapping("/cart/submit")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<Object> customerSubmitItem() throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        customerSubmitService.submitPickedItems(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("清空购物车")
    @PostMapping("/cart/clean")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<Object> cancelAllPickItems() throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        customerPickService.cleanPickedItems(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("集合商品列表")
    @GetMapping("/collection/list")
    @PreAuthorize("@el.check('cproduct:list')")
    public ResponseEntity<PageResult<ItemShowRecordDto>> queryItemShowRecord(ItemShowRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        // 默认只查询上线且未删除的商品
        criteria.setItemStatus(1);
        criteria.setDelFlag(0);
        return new ResponseEntity<>(itemShowRecordService.queryAll(criteria, pageable), HttpStatus.OK);
    }
}
