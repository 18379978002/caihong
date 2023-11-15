package com.caipiao.modules.oss.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.utils.DateUtils;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.entity.SysOssVideo;
import com.caipiao.modules.oss.entity.req.VideoReq;
import com.caipiao.modules.oss.service.SysOssVideoService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("sys/oss/video")
@Api(tags = "视频管理")
public class SysOssVideoController extends AbstractController {

    @Autowired
    SysOssVideoService sysOssVideoService;



    @Value("${qiniu.domain}")
    private String domain;



    @Autowired
    private OSSFactory ossFactory;

    @PostMapping
    @ApiOperation("新增视频")
    public R save(@RequestBody SysOssVideo video){
        ValidatorUtils.validateEntity(video, AddGroup.class);
        video.setCreateUserId(getUserId());
        video.setCompanyId(getCompanyId());
        video.setAddTime(new Date());
        video.setCreateStaffName(getUser().getStaffName());
        sysOssVideoService.save(video);
        return ok();
    }

    @PostMapping("changeOriginalFileName/{id}")
    @ApiOperation("更改视频信息")
    public R changeOriginalFileName(@PathVariable Long id,
                                    @RequestParam("originalFileName") String originalFileName,
                                    @RequestParam(value = "summary", required = false)String summary){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        if(null == video || !getCompanyId().equals(video.getCompanyId())){
            return error("视频不存在");
        }
        video.setOriginalFileName(originalFileName);
        video.setSummary(summary);
        this.sysOssVideoService.updateById(video);
        return ok();
    }

    @PostMapping("detectFileSize")
    @ApiOperation("检测文件是否存在")
    public R detectFileSize(@RequestParam Long fileSize){

        LambdaQueryWrapper<SysOssVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOssVideo::getFileSize, fileSize)
                .eq(SysOssVideo::getVideoType, 1)
                .orderByDesc(SysOssVideo::getAddTime)
                .last("limit 1");

        SysOssVideo video = sysOssVideoService.getOne(wrapper);

        if(null != video){
            return ok().put(video).put("exist", true);
        }

        return ok().put("exist", false);
    }

    @GetMapping("getUploadConfig")
    @ApiOperation("获取上传参数")
    @RequiresPermissions("video:upload:config")
    public R getUploadToken(){
        return ok()
                .put("uploadToken",ossFactory.build().getUploadToken())
                .put("domain", domain)
                .put("companyId", getCompanyId());
    }


    @DeleteMapping("{id}")
    @ApiOperation("删除视频")
    public R delete(@PathVariable Long id){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        if(null == video || !getCompanyId().equals(video.getCompanyId())){
            return error("视频不存在");
        }
        this.sysOssVideoService.delete(video);
        return ok();
    }

    @PutMapping("click/{id}")
    @ApiOperation("增加点击次数")
    public R click(@PathVariable Long id){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        if(null == video || !getCompanyId().equals(video.getCompanyId())){
            return error("视频不存在");
        }
        video.setClickNumber((video.getClickNumber()==null?1:(video.getClickNumber()+1)));
        this.sysOssVideoService.updateById(video);
        return ok();
    }

    @PostMapping("editCoverImg/{id}")
    @ApiOperation("修改封面")
    public R editCoverImg(@PathVariable Long id,
                          @RequestParam MultipartFile file){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        if(null == video || !getCompanyId().equals(video.getCompanyId())){
            return error("视频不存在");
        }

        String originalFilename = file.getOriginalFilename();

        String extend = originalFilename.substring(originalFilename.lastIndexOf("."));

        String format = DateUtils.format(new Date(), "yyyy-MM-dd");

        String path = getCompanyId() + "/" + format + "/" + IdUtil.createSnowflake(1,1).nextIdStr() + extend;

        try {
            ossFactory.build().upload(file.getInputStream(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        video.setCoverImg(path);
        this.sysOssVideoService.updateById(video);
        return ok();
    }

    @PutMapping("changeHot")
    @ApiOperation("设置为热门视频")
    public R hot(@RequestParam String ids){
        this.sysOssVideoService.changeHot(ids, getUser());
        return ok();
    }

    @PutMapping("changeCollect")
    @ApiOperation("设置为精选视频")
    public R collect(@RequestParam String ids){
        this.sysOssVideoService.changeCollect(ids, getUser());
        return ok();
    }

    @PutMapping("{id}/{categoryId}")
    @ApiOperation("更改分类")
    public R editCategory(@PathVariable("id") Long id,
                          @PathVariable("categoryId")Long categoryId){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        if(null == video || !getCompanyId().equals(video.getCompanyId())){
            return error("视频不存在");
        }
        video.setCategoryId(categoryId);
        this.sysOssVideoService.updateById(video);
        return ok();
    }

    @ApiOperation("分页查询")
    @GetMapping("list")
    public R list(VideoReq req){
        IPage<SysOssVideo> pg = new Page<>(req.getPage(), req.getLimit());
        req.setCompanyId(getCompanyId());
        IPage<SysOssVideo> page = sysOssVideoService.queryPage(pg, req);
        return ok().put("page", new PageUtils<>(page)).put("coverImgDomain", domain);
    }

}
