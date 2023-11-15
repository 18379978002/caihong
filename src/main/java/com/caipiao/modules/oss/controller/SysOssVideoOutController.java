package com.caipiao.modules.oss.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.oss.entity.SysOssVideo;
import com.caipiao.modules.oss.entity.VideoCategory;
import com.caipiao.modules.oss.entity.req.VideoCategoryReq;
import com.caipiao.modules.oss.entity.req.VideoReq;
import com.caipiao.modules.oss.service.SysOssVideoService;
import com.caipiao.modules.oss.service.VideoCategoryService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oss/video")
@Api(tags = "手机端视频管理")
public class SysOssVideoOutController extends AbstractController {

    @Autowired
    SysOssVideoService sysOssVideoService;

    @Value("${qiniu.domain}")
    private String domain;

    @Autowired
    VideoCategoryService videoCategoryService;

    @GetMapping("{id}")
    @ApiOperation("查询视频")
    public R delete(@PathVariable Long id){
        SysOssVideo video = this.sysOssVideoService.getById(id);

        VideoCategory category = videoCategoryService.getById(video.getCategoryId());
        if(null != category){
            video.setCategoryName(category.getName());
        }
        return ok().put(video).put("coverImgDomain", domain);
    }

    @PutMapping("click/{id}")
    @ApiOperation("增加点击次数")
    public R click(@PathVariable Long id){
        SysOssVideo video = this.sysOssVideoService.getById(id);
        video.setClickNumber((video.getClickNumber()==null?1:(video.getClickNumber()+1)));
        this.sysOssVideoService.updateById(video);
        return ok();
    }


    @ApiOperation("视频分页查询")
    @GetMapping("list")
    public R list(VideoReq req){
        IPage<SysOssVideo> pg = new Page<>(req.getPage(), req.getLimit());
        if(StringUtils.isEmpty(req.getCompanyId())){
            return error("companyId不能为空");
        }
        IPage<SysOssVideo> page = sysOssVideoService.queryPage(pg, req);
        return ok().put("page", new PageUtils<>(page)).put("coverImgDomain", domain);
    }

    @ApiOperation("视频分类查询")
    @GetMapping("category/list")
    public R list(VideoCategoryReq req){
        IPage<VideoCategory> pg = new Page<>(req.getPage(), req.getLimit());
        if(StringUtils.isEmpty(req.getCompanyId())){
            return error("companyId不能为空");
        }
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(req.getName()), VideoCategory::getName, req.getName())
                .eq(VideoCategory::getCompanyId, req.getCompanyId()).orderByAsc(VideoCategory::getOrderNum);
        IPage<VideoCategory> page = videoCategoryService.page(pg, wrapper);

        return ok().put("page", new PageUtils<>(page));
    }

}
