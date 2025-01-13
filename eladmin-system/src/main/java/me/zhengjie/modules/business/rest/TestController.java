package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.business.domain.BizCustomerPickRecord;
import me.zhengjie.modules.business.repository.BizCustomerPickRecordMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/1/12
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "测试")
@RequestMapping("/api/test")
public class TestController {
    @Resource
    private BizCustomerPickRecordMapper bizCustomerPickRecordMapper;

    @ApiOperation("测试插入数据")
    @AnonymousPostMapping(value = "/testMapper")
    public void testMapper(BizCustomerPickRecord record) throws Exception {
        record.setCreateTime(new Date());
        bizCustomerPickRecordMapper.insertSelective(record);
    }
}
