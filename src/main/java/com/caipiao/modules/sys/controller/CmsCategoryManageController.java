package com.caipiao.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.modules.sys.entity.CmsCategory;
import com.caipiao.modules.sys.service.CmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:34
 */
@RestController
@RequestMapping("/manage/category")
public class CmsCategoryManageController {
    @Autowired
    private CmsCategoryService categoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 顶级目录
     */
    @GetMapping("/top")
    public R findTop() {
        QueryWrapper<CmsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("top_level", 1).eq("company_id", ShiroUtils.getUserEntity().getCompanyId());
        return R.ok().put("data", categoryService.list(queryWrapper));
    }

    /**
     * 根据parentId查找
     */
    @GetMapping("/level/{parentId}")
    public R level(@PathVariable Long parentId) {
        QueryWrapper<CmsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId).eq("company_id", ShiroUtils.getUserEntity().getCompanyId());
        return R.ok().put("data", categoryService.list(queryWrapper));
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CmsCategory category) {
        if(null != category.getId()){
            LambdaUpdateWrapper<CmsCategory> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(CmsCategory::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                    .eq(CmsCategory::getId, category.getId());
            categoryService.update(category, wrapper);
        }else{
            category.setCompanyId(ShiroUtils.getUserEntity().getCompanyId());
            categoryService.save(category);
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        LambdaQueryWrapper<CmsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsCategory::getCompanyId, ShiroUtils.getUserEntity().getCompanyId())
                .in(CmsCategory::getId, Arrays.asList(ids));
        categoryService.remove(wrapper);
        return R.ok();
    }

}
