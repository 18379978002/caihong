package com.caipiao.modules.app.manage;

import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import com.caipiao.modules.app.entity.GradeInfo;
import com.caipiao.modules.app.service.GradeInfoService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:35
 */
@RestController
@RequestMapping("manage/gradeinfo")
@Api(tags = "等级定义《后台管理》")
@RequiredArgsConstructor
public class GradeInfoManageController extends AbstractController {
    private final GradeInfoService gradeInfoService;

    @ApiOperation("新增")
    @PostMapping
    public R save(@RequestBody GradeInfo gradeInfo){
        ValidatorUtils.validateEntity(gradeInfo, AddGroup.class);
        gradeInfoService.save(gradeInfo);
        return ok();
    }

    @ApiOperation("修改")
    @PutMapping
    public R update(@RequestBody GradeInfo gradeInfo){
        ValidatorUtils.validateEntity(gradeInfo, UpdateGroup.class);
        gradeInfoService.updateById(gradeInfo);
        return ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("{id}")
    public R del(@PathVariable Long id){

        if(id==1L){
            return error("系统会员级别，禁止删除");
        }
        GradeInfo gradeInfo = new GradeInfo();
        gradeInfo.setDelFlag(Constant.YES);
        gradeInfo.setId(id);
        gradeInfoService.updateById(gradeInfo);
        return ok();
    }

    @ApiOperation("查询")
    @GetMapping("{id}")
    public R get(@PathVariable Long id){
        GradeInfo info = gradeInfoService.getById(id);
        return ok().put(info);
    }

    @ApiOperation("分页查询")
    @GetMapping("list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils pageUtils = gradeInfoService.queryPage(params);
        return ok().put("page", pageUtils);
    }


}
