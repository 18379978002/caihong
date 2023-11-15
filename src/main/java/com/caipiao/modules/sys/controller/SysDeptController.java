package com.caipiao.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.entity.DeptEntity;
import com.caipiao.modules.sys.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sys/dept")
@Api(tags = "部门管理")
public class SysDeptController extends AbstractController{

    @Autowired
    private ISysDeptService sysDeptService;

    @ApiOperation("获取部门下拉树列表")
    @GetMapping("/treeSelect")
    public R treeSelect() {
        LambdaQueryWrapper<DeptEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeptEntity::getCompanyId, getCompanyId());
        List<DeptEntity> deptEntities = sysDeptService.list(wrapper);
        return R.ok().put(sysDeptService.buildDeptTreeSelect(deptEntities));
    }

    @ApiOperation("获取部门下拉树列表")
    @GetMapping("/queryAll")
    public R queryAll() {
        LambdaQueryWrapper<DeptEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeptEntity::getCompanyId, getCompanyId());
        return R.ok().put(sysDeptService.list(wrapper));
    }
}
