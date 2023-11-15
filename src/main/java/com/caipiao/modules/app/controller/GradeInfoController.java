package com.caipiao.modules.app.controller;

import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.entity.GradeInfo;
import com.caipiao.modules.app.service.GradeInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午2:56
 */
@RestController
@RequestMapping("/app/gradeinfo")
@Api(tags = "《app端》会员等级")
@Slf4j
@RequiredArgsConstructor
public class GradeInfoController extends AppAbstractController {
    private final GradeInfoService gradeInfoService;


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
