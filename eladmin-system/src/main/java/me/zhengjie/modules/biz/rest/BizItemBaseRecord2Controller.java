package me.zhengjie.modules.biz.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.biz.service.ImageSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * 产品管理
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：产品管理-jpa版本2")
@RequestMapping("/api/biz/item2")
public class BizItemBaseRecord2Controller {

    private final ImageSearchService imageSearchService;

    @AnonymousPostMapping
    @ApiOperation("相似图片搜索")
    @PostMapping("/search")
    public Object imageSearch(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "topN", required = false) Integer topN) throws IOException {
        return imageSearchService.search(file, topN);
    }

    @ApiOperation("写入商品向量（单张）")
    @PostMapping("/vector/upsert")
//    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> upsertVector(@RequestParam("imageId") Long imageId,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        imageSearchService.upsertVector(imageId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("批量回填商品向量")
    @GetMapping("/vector/backfill")
//    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> backfillVectors(@RequestParam(value = "startId", required = false) Long startId,
                                                  @RequestParam(value = "endId", required = false) Long endId,
                                                  @RequestParam(value = "batchSize", required = false) Integer batchSize) {
        return new ResponseEntity<>(imageSearchService.backfillVectors(startId, endId, batchSize), HttpStatus.OK);
    }

    @ApiOperation("按ID列表重试回填向量")
    @GetMapping("/vector/retry")
//    @PreAuthorize("@el.check('product:edit')")
    public ResponseEntity<Object> retryVectors(@RequestBody Set<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            throw new BadRequestException("imageIds不能为空");
        }
        return new ResponseEntity<>(imageSearchService.retryVectors(imageIds), HttpStatus.OK);
    }
}
