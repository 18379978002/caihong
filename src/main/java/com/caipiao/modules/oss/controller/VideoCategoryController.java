package com.caipiao.modules.oss.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import com.caipiao.modules.oss.entity.VideoCategory;
import com.caipiao.modules.oss.entity.req.VideoCategoryReq;
import com.caipiao.modules.oss.service.VideoCategoryService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/22 09:03
 */
@RestController
@Slf4j
@RequestMapping("/sys/video/category")
@Api(tags = "视频分类")
public class VideoCategoryController extends AbstractController {

    @Autowired
    VideoCategoryService videoCategoryService;

    @ApiOperation("分类新增")
    @PostMapping
    public R save(@RequestBody VideoCategory category){
        ValidatorUtils.validateEntity(category, AddGroup.class);
        category.setCompanyId(getCompanyId());
        videoCategoryService.save(category);
        return ok();

    }

    @ApiOperation("分类修改")
    @PutMapping
    public R update(@RequestBody VideoCategory category){
        ValidatorUtils.validateEntity(category, UpdateGroup.class);
        VideoCategory byId = videoCategoryService.getById(category.getId());
        if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
            return error("暂无权限");
        }
        category.setCompanyId(getCompanyId());
        videoCategoryService.updateById(category);
        return ok();
    }

    @ApiOperation("分类删除")
    @DeleteMapping
    public R delete(@RequestBody List<Long> ids){
        for (Long id : ids) {
            VideoCategory byId = videoCategoryService.getById(id);
            if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
                return error("暂无权限");
            }
        }
        videoCategoryService.removeByIds(ids);
        return ok();
    }

    @ApiOperation("分类查询")
    @GetMapping("{id}")
    public R get(@PathVariable Long id){
        VideoCategory byId = videoCategoryService.getById(id);
        if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
            return error("暂无权限");
        }
        return ok().put(byId);
    }

    @ApiOperation("分类分页查询")
    @GetMapping("list")
    public R list(VideoCategoryReq req){
        IPage<VideoCategory> pg = new Page<>(req.getPage(), req.getLimit());

        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(req.getName()), VideoCategory::getName, req.getName())
                .eq(VideoCategory::getCompanyId, getCompanyId()).orderByAsc(VideoCategory::getOrderNum);
        IPage<VideoCategory> page = videoCategoryService.page(pg, wrapper);

        return ok().put("page", new PageUtils<>(page));
    }

}
