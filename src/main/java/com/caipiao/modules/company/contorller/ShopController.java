package com.caipiao.modules.company.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.CollectionUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.entity.enmu.AuthenticationStatus;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.company.vo.ShopuAthenticatedRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/company/shop")
@Api(tags = "《app端》店铺操作")
@Slf4j
public class ShopController extends AppAbstractController {

    @Resource
    private ShopService shopService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 查询未认证店铺
     */
    @GetMapping("getallunverifiedshop")
    @ApiOperation(value = "查询未认证店铺",notes = "查询未认证店铺")
    public R getAllUnverifiedShop() {

        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getAuthenticationStatus,AuthenticationStatus.UNAuthenticated);
        List<Shop> list = shopService.list(wrapper);
        if (CollectionUtils.isEmpty(list)){
            return R.ok();
        }
        return R.ok().put(list);
    }

    /**
     * 更新为已认证店铺
     */
    @PutMapping("updateshop")
    @ApiOperation(value = "更新为已认证店铺",notes = "更新为已认证店铺")
    public R update(@RequestBody ShopuAthenticatedRequestDTO shopVo) {

        if(null==shopVo.getShopId()){
            throw new RRException("参数为空");
        }
        Shop shop = shopService.getById(shopVo.getShopId());
        if(null==shop){
            throw new RRException("不存在该店铺");
        }
        if (StringUtils.isBlank(shopVo.getReason())) {
            shop.setAuthenticationStatus(AuthenticationStatus.Authenticated);
            shopService.updateById(shop);
            return ok();
        }
        shop.setReason(shopVo.getReason());
        shop.setAuthenticationStatus(AuthenticationStatus.FailedAuthenticated);
        shopService.updateById(shop);
        return ok();
    }

    /**
     * 查询所有店铺
     * @return
     */
    @GetMapping("getallshop")
    @ApiOperation(value = "查询所有店铺",notes = "查询所有店铺")
    public R getAllShop() {
        List<Shop> list = shopService.list();
        if (CollectionUtils.isEmpty(list)){
            return R.ok();
        }
        return R.ok().put(list);
    }

    /**
     * 更新为违规店铺
     */
    @PutMapping("updateviolationshop")
    @ApiOperation(value = "更新为违规店铺",notes = "更新为违规店铺")
    public R updateviolationshop(@RequestBody ShopuAthenticatedRequestDTO shopVo) {
        if(null==shopVo.getShopId()){
            throw new RRException("参数为空");
        }
        if (StringUtils.isBlank(shopVo.getShopStatus())) {
            throw new RRException("状态为空");
        }
        Shop shop = shopService.getById(shopVo.getShopId());
        if (shop==null){
            throw new RRException("没有该店铺");
        }

        if ("1".equals(shopVo.getShopStatus())) {
            shop.setShopStatus("2");
        }
        if ("2".equals(shopVo.getShopStatus())){
            shop.setShopStatus("1");
        }
        shopService.updateById(shop);
        return ok();
    }

}
