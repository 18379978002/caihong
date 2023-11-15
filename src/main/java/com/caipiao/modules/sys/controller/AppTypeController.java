package com.caipiao.modules.sys.controller;

import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.constant.AppEnum;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/12/17 下午5:30
 */
@RestController
@RequestMapping("sys/app")
@Api(tags = "应用类型管理")
@Slf4j
public class AppTypeController extends AbstractController {

    @GetMapping("types")
    public R types(){
        AppEnum[] values = AppEnum.values();

        List<Map<String, Object>> ls = new ArrayList<>();

        for (AppEnum value : values) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", value.getValue());
            mp.put("name", value.getAppName());
            ls.add(mp);
        }
        return ok().put(ls);
    }
}
