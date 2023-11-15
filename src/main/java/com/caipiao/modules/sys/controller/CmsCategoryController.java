package com.caipiao.modules.sys.controller;

import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.service.CmsCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/12/3 下午4:35
 */
@RestController
@RequestMapping("/cms/category")
@Slf4j
@RequiredArgsConstructor
public class CmsCategoryController {
    private final CmsCategoryService cmsCategoryService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = cmsCategoryService.queryPage1(params);
        return R.ok().put("page", page);
    }


}
