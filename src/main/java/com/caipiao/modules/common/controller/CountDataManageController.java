package com.caipiao.modules.common.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.common.dao.CountDataDao;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.task.CountDataTask;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/countdata")
@Slf4j
@Api(tags = "<管理端>数据统计")
public class CountDataManageController extends AbstractController {

    @Autowired
    CountDataDao countDataDao;

    @Autowired
    CountDataTask countDataTask;

    @PostMapping("refreshReportData")
    public R refresh(){

        if(isShopManager()){
            countDataTask.countDataTask();
        }else {
            countDataTask.countData(getUserId());
        }
        return ok();
    }

    @GetMapping("get")
    @ApiOperation("获取统计数据")
    public R get(@RequestParam(value = "staffId", required = false)String staffId,
                 @RequestParam(value = "ym", required = false)String ym,
                 @RequestParam(value = "ymd", required = false)String ymd){
        if(StringUtils.isBlank(ym) && StringUtils.isBlank(ymd)){
            ymd = DateUtil.formatDate(new Date());
        }

        if(StringUtils.isNotBlank(ym) && StringUtils.isNotBlank(ymd)){
            return error("参数错误");
        }

        if(!isShopManager()){
            staffId = getUserId();
        }

        CountData data = countDataDao.countData(staffId, ym, ymd);

        if(null == data){
            data = new CountData();
            data.setAlipayRecharge(BigDecimal.ZERO);
            data.setBasketballSaleAmt(BigDecimal.ZERO);
            data.setDispatchAmt(BigDecimal.ZERO);
            data.setNewUserNum(0);
            data.setFootballSaleAmt(BigDecimal.ZERO);
            data.setStaffRecharge(BigDecimal.ZERO);
        }


        //查询用户余额
        LambdaQueryWrapper<CountData> wrapper = new LambdaQueryWrapper<>();

        if(!isShopManager()){
            wrapper.eq(CountData::getStaffId, getUserId());
        }else{
            wrapper.eq(StringUtils.isNotBlank(staffId),CountData::getStaffId,staffId);
        }

        if(StringUtils.isNotBlank(ymd)){
            wrapper.eq(CountData::getCountDate, ymd);
        }else{
            wrapper.eq(CountData::getCountDate, DateUtil.formatDate(new Date()));
        }

        List<CountData> countDataList = countDataDao.selectList(wrapper);
        if(CollUtil.isEmpty(countDataList)){
            data.setUserBalance(BigDecimal.ZERO);
        }else{

            BigDecimal sum = countDataList.stream()
                    .map(CountData::getUserBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            data.setUserBalance(sum);

        }

        return ok().put(data);

    }


}
