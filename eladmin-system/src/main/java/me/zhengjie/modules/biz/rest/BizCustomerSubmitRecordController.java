package me.zhengjie.modules.biz.rest;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.biz.service.BizCustomerSubmitRecordService;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordDto;
import me.zhengjie.modules.biz.service.dto.BizCustomerSubmitRecordQueryCriteria;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提交管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "管理：提交管理-jpa版本")
@RequestMapping("/api/biz/customer/submit/record")
public class BizCustomerSubmitRecordController {

    private final UserService userService;
    private final BizCustomerSubmitRecordService bizCustomerSubmitRecordService;

    @ApiOperation("导出产品列表")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('intention:download')")
    public void exportBizItem(HttpServletResponse response, BizCustomerSubmitRecordQueryCriteria criteria) throws Exception {
        bizCustomerSubmitRecordService.download(criteria.getId(), response);
    }

    @ApiOperation("查询产品列表")
    @GetMapping
    @PreAuthorize("@el.check('intention:list')")
    public ResponseEntity<PageResult<BizCustomerSubmitRecordDto>> queryBizItem(BizCustomerSubmitRecordQueryCriteria criteria, Pageable pageable) throws Exception {
        PageResult<BizCustomerSubmitRecordDto> pageResult = bizCustomerSubmitRecordService.queryAll(criteria, pageable);
        List<BizCustomerSubmitRecordDto> content = pageResult.getContent();
        Set<Long> userIds = content.stream().map(BizCustomerSubmitRecordDto::getUserId).collect(Collectors.toSet());
        Map<Long, UserDto> userDtoMap = userService.batchFindByIds(Lists.newArrayList(userIds));
        for (BizCustomerSubmitRecordDto submitRecordDto : content) {
            Long userId = submitRecordDto.getUserId();
            UserDto userDto = userDtoMap.get(userId);
            submitRecordDto.setUsername(userDto.getUsername());
            submitRecordDto.setNickName(userDto.getNickName());
            submitRecordDto.setEmail(userDto.getEmail());
            submitRecordDto.setPhone(userDto.getPhone());
        }
        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    @Log("删除产品信息")
    @ApiOperation("删除产品信息")
    @DeleteMapping
    @PreAuthorize("@el.check('intention:del')")
    public ResponseEntity<Object> deleteBizItem(@RequestBody Set<Long> ids) throws Exception {
        bizCustomerSubmitRecordService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
