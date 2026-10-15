package me.zhengjie.modules.business.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.picture.CompressPictureRequest;
import me.zhengjie.modules.business.rest.request.picture.FormatPicSuffixRequest;
import me.zhengjie.modules.business.service.picture.CompressPictureService;
import me.zhengjie.modules.business.service.picture.FormatPicSuffixService;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/3/10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "工具：图片处理")
@RequestMapping("/picture/utils")
public class PictureUtilsController {
    @Autowired
    private CompressPictureService compressPictureService;
    @Autowired
    private FormatPicSuffixService formatPicSuffixService;


    @ApiOperation("工具/压缩指定路径下的图片")
    @AnonymousPostMapping(value = "/v1/compressPicture")
    public ResponseEntity<Object> compressPicture(@RequestBody CompressPictureRequest request) throws Exception {
        if(StringUtils.isEmpty(request.getSourcePath())){
            throw new BadRequestException("图片路径不能为空");
        }
        Integer count = compressPictureService.compressPicture(request);
        String result = "共压缩" + count + "张图片";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 工具：格式化图片后缀 —— 把 sourcePath 下的 jpeg / jpg（忽略大小写）统一改成 .JPG，其他格式不动。
     *
     * sourcePath：必填，源文件夹。
     * targetPath：选填，不传则原地重命名；传了则复制到该目录，源文件保留。
     */
    @ApiOperation(value = "工具/格式化图片后缀", notes = "jpeg/jpg（忽略大小写）统一改成 .JPG")
    @AnonymousPostMapping(value = "/v1/formatPicSuffix")
    public ResponseEntity<Object> formatPicSuffix(@RequestBody FormatPicSuffixRequest request) throws Exception {
        if(StringUtils.isEmpty(request.getSourcePath())){
            throw new BadRequestException("图片路径不能为空");
        }
        Integer count = formatPicSuffixService.formatPicSuffix(request);
        String result = "共修改" + count + "张图片的后缀";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
