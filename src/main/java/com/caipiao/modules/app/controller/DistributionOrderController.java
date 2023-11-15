package com.caipiao.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.entity.DistributionOrder;
import com.caipiao.modules.app.service.DistributionOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:33
 */
@RestController
@RequestMapping("/app/distributionorder")
@Api(tags = "《app端》分销订单管理")
@Slf4j
@RequiredArgsConstructor
public class DistributionOrderController extends AppAbstractController {

    private final DistributionOrderService distributionOrderService;


    @GetMapping("list")
    @ApiOperation("分销订单查询")
    public R list(@RequestParam Map<String, Object> params){

        IPage<DistributionOrder> page = new Query<DistributionOrder>().getPage(params);

        LambdaQueryWrapper<DistributionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DistributionOrder::getDistributionUserId, getUser().getId());

        IPage<DistributionOrder> page1 = distributionOrderService.page(page, wrapper);

        return ok().put("page", new PageUtils<>(page1));
    }



}
