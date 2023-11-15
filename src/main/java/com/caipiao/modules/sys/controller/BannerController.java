package com.caipiao.modules.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.sys.entity.Banner;
import com.caipiao.modules.sys.entity.SysConfigEntity;
import com.caipiao.modules.sys.service.BannerService;
import com.caipiao.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xiaoyinandan
 * @date 2021/8/24 下午3:17
 */
@RestController
@RequestMapping("sys/banner")
@Api(tags = "轮播图管理")
public class BannerController extends AbstractController {
    @Autowired
    BannerService bannerService;
    @Autowired
    SysConfigService sysConfigService;

    @GetMapping("allType")
    @ApiOperation("所有APP类型")
    public R allType(){
        SysConfigEntity banner = sysConfigService.getSysConfig("BANNER");
        JSONArray array = JSON.parseArray(banner.getParamValue());
        return ok().put(array);
    }


    @PostMapping("createOrUpdate")
    @ApiOperation("新增或者更新")
    public R createOrUpdate(@RequestBody Banner banner){

        if(null != banner.getId()){
            Banner byId = bannerService.getById(banner.getId());
            if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
                return error("不存在记录");
            }
            banner.setCompanyId(getCompanyId());
            bannerService.updateById(banner);
            return ok();
        }

        banner.setCreateTime(new Date());
        banner.setCreateStaff(getUserId());
        banner.setCompanyId(getCompanyId());
        bannerService.save(banner);
        return ok();
    }

    @PostMapping("{id}")
    @ApiOperation("查看")
    public R query(@PathVariable Long id){
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getCompanyId, getCompanyId())
                .eq(Banner::getId, id);
        return ok().put(bannerService.getOne(wrapper));
    }

    @DeleteMapping("batchDelete")
    @ApiOperation("批量删除")
    public R batchDelete(@RequestBody List<Long> ids){
        bannerService.batchDelete(ids);
        return ok();
    }

    @GetMapping("list")
    @ApiOperation("分页")
    public R list(@RequestParam(value = "page", required = false, defaultValue = "1")Integer page,
                  @RequestParam(value = "limit", required = false, defaultValue = "10")Integer limit,
                  @RequestParam(value = "appName", required = false)String appName,
                  @RequestParam(value = "companyId", required = false)String companyId){

        if(StringUtils.isBlank(companyId)){
            companyId = getCompanyId();
        }

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(appName),Banner::getAppName, appName)
                .eq(Banner::getCompanyId, companyId).orderByAsc(Banner::getOrderNumber);
        IPage<Banner> pg = new Page<>(page, limit);
        IPage<Banner> page1 = bannerService.page(pg, wrapper);
        return ok().put("page",new PageUtils<>(page1));
    }

}
