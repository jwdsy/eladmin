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
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.bizitem.domain.ItemBaseRecord;
import me.zhengjie.modules.bizitem.service.ItemBaseRecordService;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordDto;
import me.zhengjie.modules.bizitem.service.dto.ItemBaseRecordQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2026-05-07
 */
@RestController
@RequiredArgsConstructor
@ApiIgnore
@Api(tags = "商品管理：商品基本信息")
@RequestMapping("/api/bizitem/record")
public class ItemBaseRecordController {

    private final ItemBaseRecordService itemBaseRecordService;
    private static final String ENTITY_NAME = "itemBaseRecord";

    @ApiOperation("导出商品数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('itemRecord:list')")
    public void exportItemBaseRecord(HttpServletResponse response, ItemBaseRecordQueryCriteria criteria) throws Exception {
        itemBaseRecordService.download(itemBaseRecordService.queryAll(criteria), response);
    }

    @ApiOperation("查询商品")
    @GetMapping
    @PreAuthorize("@el.check('user:list','itemRecord:list')")
    public ResponseEntity<PageResult<ItemBaseRecordDto>> queryItemBaseRecord(ItemBaseRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        return new ResponseEntity<>(itemBaseRecordService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("匿名查询商品（用于前台展示）")
    @AnonymousGetMapping("/list")
    public ResponseEntity<PageResult<ItemBaseRecordDto>> queryItemBaseRecordAnonymous(ItemBaseRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        // 默认只查询上线且未删除的商品
        criteria.setItemStatus(1);
        criteria.setDelFlag(0);
        return new ResponseEntity<>(itemBaseRecordService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("新增商品")
    @ApiOperation("新增商品")
    @PostMapping
    @PreAuthorize("@el.check('itemRecord:add')")
    public ResponseEntity<Object> createItemBaseRecord(@Validated @RequestBody ItemBaseRecord resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        itemBaseRecordService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改商品")
    @ApiOperation("修改商品")
    @PutMapping
    @PreAuthorize("@el.check('itemRecord:edit')")
    public ResponseEntity<Object> updateItemBaseRecord(@Validated @RequestBody ItemBaseRecord resources) {
        itemBaseRecordService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除商品")
    @ApiOperation("删除商品")
    @DeleteMapping
    @PreAuthorize("@el.check('itemRecord:del')")
    public ResponseEntity<Object> deleteItemBaseRecord(@RequestBody Set<Long> ids) {
        itemBaseRecordService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
