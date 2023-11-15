package com.caipiao.modules.oss.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.DateUtils;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.QiniuGroup;
import com.caipiao.modules.oss.cloud.CloudStorageConfig;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.entity.SysOssEntity;
import com.caipiao.modules.oss.service.SysOssService;
import com.caipiao.modules.sys.controller.AbstractController;
import com.caipiao.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:50
 */
@RestController
@RequestMapping("sys/oss")
@Api(tags = {"对象存储/文件上传"})
@Slf4j
public class OssController extends AbstractController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private OSSFactory ossFactory;

    @Value("${qiniu.domain}")
    private String domain;

    private final static String KEY = Constant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     * 列表
     */
    @ApiOperation(value = "文件列表", notes = "对象存储管理的文件")
    @GetMapping("/list")
    @RequiresPermissions("sys:oss:all")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysOssService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 保存
     */
    @ApiOperation(value = "保存文件", notes = "保存文件")
    @PostMapping("/save")
    public R save(@RequestBody SysOssEntity oss) {
        oss.setCompanyId(getCompanyId());
        oss.setAddTime(new Date());
        oss.setCreateUserId(getUserId());
        sysOssService.save(oss);
        return R.ok().put("data", oss);
    }

    @ApiOperation(value = "文件列表", notes = "对象存储管理的文件")
    @GetMapping("/listByBusiId/{fileBusiId}/{type}")
    public R listByBusiId(@PathVariable("fileBusiId")String fileBusiId,
                          @PathVariable("type")String type) {
        LambdaQueryWrapper<SysOssEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOssEntity::getFileBusiId, fileBusiId)
                .eq(SysOssEntity::getFileType, type);
        return R.ok().put(sysOssService.list(wrapper)).put("domain", domain);
    }

    @GetMapping("getUploadConfig")
    @ApiOperation("获取上传参数")
    public R getUploadToken(){
        return ok()
                .put("uploadToken",ossFactory.build().getToken())
                .put("domain", domain)
                .put("companyId", getCompanyId());
    }


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "云存储配置信息", notes = "首次使用前先管理后台新增配置")
    public R config() {
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

        return R.ok().put("config", config);
    }


    /**
     * 保存云存储配置信息
     */
    @PostMapping("/saveConfig")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "保存云存储配置信息")
    public R saveConfig(@RequestBody CloudStorageConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getType() == Constant.CloudService.QINIU.getValue()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(config, QiniuGroup.class);
        }

        sysConfigService.updateValueByKey(KEY, JSON.toJSONString(config));

        return R.ok();
    }


    /**
     * 上传文件
     */
    @PostMapping("/uploadCategory")
    @ApiOperation(value = "上传category文件到OSS")
    public R uploadCategory(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }

        String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        //上传文件
        String split = DateUtils.format(new Date());
        String path = getCompanyId() + "/" + split + "/" + IdUtil.getSnowflake(1, 1).nextIdStr() + substring;
        String url = Objects.requireNonNull(ossFactory.build()).uploadCategory(file.getBytes(), path);

        return R.ok().put("url", url).put("domain", domain);
    }

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

        SysOssEntity upload = sysOssService.upload(fileName, file.getBytes(), ossFactory, file, null, getUser());

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


    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("sys:oss:all")
    @ApiOperation(value = "删除文件", notes = "只删除记录，云端文件不会删除")
    public R delete(@RequestBody Long[] ids) {
        sysOssService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}

