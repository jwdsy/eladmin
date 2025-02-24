package me.zhengjie.modules.biz.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.biz.repository.domain.BizItemBaseRecord;
import me.zhengjie.modules.biz.service.BizItemBaseRecordService;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordDto;
import me.zhengjie.modules.biz.service.dto.BizItemBaseRecordQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 产品管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品管理-jpa版本")
@RequestMapping("/api/biz/item")
public class BizItemBaseRecordController {

    private final BizItemBaseRecordService bizItemBaseRecordService;
    private static final String ENTITY_NAME = "bizItem";

    @ApiOperation("导出产品列表")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('product:list')")
    public void exportBizItem(HttpServletResponse response, BizItemBaseRecordQueryCriteria criteria) throws Exception {
        bizItemBaseRecordService.download(bizItemBaseRecordService.queryAll(criteria), response);
    }

    @ApiOperation("查询产品列表")
    @GetMapping
    @PreAuthorize("@el.check('product:list')")
    public ResponseEntity<PageResult<BizItemBaseRecordDto>> queryBizItem(BizItemBaseRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        return new ResponseEntity<>(bizItemBaseRecordService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Log("创建/更新产品信息")
    @ApiOperation("创建/更新产品信息")
    @PostMapping
    @PreAuthorize("@el.check('product:add')")
    public ResponseEntity<Object> createBizItem(@Validated @RequestBody BizItemBaseRecord resources) throws Exception {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        bizItemBaseRecordService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("更新产品状态")
    @ApiOperation("更新产品状态")
    @PutMapping
    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> updateBizItem(@Validated(BizItemBaseRecord.Update.class) @RequestBody BizItemBaseRecord resources) throws Exception {
        bizItemBaseRecordService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除产品信息")
    @ApiOperation("删除产品信息")
    @DeleteMapping
    @PreAuthorize("@el.check('product:del')")
    public ResponseEntity<Object> deleteBizItem(@RequestBody Set<Long> ids) throws Exception {
        bizItemBaseRecordService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
