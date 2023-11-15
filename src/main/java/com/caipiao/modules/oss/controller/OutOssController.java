package com.caipiao.modules.oss.controller;

import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.R;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.entity.SysOssEntity;
import com.caipiao.modules.oss.service.SysOssService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:50
 */
@RestController
@RequestMapping("oss")
@Api(tags = {"对象存储/文件上传(外部)"})
@Slf4j
public class OutOssController extends AbstractController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private OSSFactory ossFactory;

    @Value("${qiniu.domain}")
    private String domain;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件到OSS")
    public R upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();

        SysOssEntity upload = sysOssService.upload(fileName, file.getBytes(), ossFactory, file, null, null);

        return R.ok().put("file", upload).put("domain", domain);
    }

    /**
     * 上传多个文件
     */
    @PostMapping("/uploadMulti")
    @ApiOperation(value = "上传多个文件到OSS")
    public R uploadMulti(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<SysOssEntity> ups = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            SysOssEntity upload = sysOssService.upload(fileName, file.getBytes(), ossFactory, file, null, getUser());

            ups.add(upload);
        }

        return R.ok().put("fileList", ups).put("domain", domain);
    }

}

