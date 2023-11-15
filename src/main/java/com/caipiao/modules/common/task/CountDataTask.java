package com.caipiao.modules.common.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.app.dao.UserInfoDao;
import com.caipiao.modules.app.dao.UserRechargeRecordDao;
import com.caipiao.modules.app.dto.RechargeCountDTO;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.service.CountDataService;
import com.caipiao.modules.order.dto.AmountCountDTO;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.sys.controller.AbstractController;
import com.caipiao.modules.sys.dao.SysCompStaffDao;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class CountDataTask extends AbstractController {

    @Resource
    private ShopService shopService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserRechargeRecordDao userRechargeRecordDao;
    @Autowired
    SysCompStaffDao sysCompStaffDao;
    @Autowired
    CountDataService countDataService;

    //1
    @Scheduled(cron = "0 7/7 * * * ?")
    public void countDataTask(){
        log.info("++++++++++++++++++++开始进行数据统计++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        LambdaQueryWrapper<SysCompStaffEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysCompStaffEntity::getPosition,"super").eq(SysCompStaffEntity::getStaffStatus, "S0A");
        List<SysCompStaffEntity> staffs = sysCompStaffDao.selectList(wrapper);

        for (SysCompStaffEntity staff : staffs) {
            //示例
            TenantService.putTenantContext(new TenantContext(staff.getSubordinateStore()));
            countData(staff.getStaffId());
        }
        List<Shop> shopList = shopService.list();
        log.info("shopList {}",shopList);
        for (Shop shop : shopList) {
            String staffId = shop.getStaffId();
            TenantService.putTenantContext(new TenantContext(String.valueOf(shop.getId())));
            extracted(staffId, new CountData(), queryNoManagerUserList());
        }
        watch.stop();
        log.info("++++++++++++++++++++结束数据统计,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());

    }

    private List<String> queryNoManagerUserList(){

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(UserInfo::getManageStaffId);
        List<UserInfo> userInfos = userInfoDao.selectList(wrapper);

        if(CollUtil.isEmpty(userInfos)){
            return new ArrayList<>();
        }

        return userInfos.stream().map(UserInfo::getId).collect(Collectors.toList());

    }


    public void countData(String staffId){
        CountData data = new CountData();
        List<String> userList = queryStaffManageList(staffId);
        extracted(staffId, data, userList);
    }

    private void extracted(String staffId, CountData data, List<String> userList) {
        data.setCountDate(DateUtil.formatDate(new Date()));
        data.setStaffId(staffId);

        LambdaUpdateWrapper<CountData> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CountData::getStaffId, staffId)
                .eq(CountData::getCountDate, data.getCountDate());

        if(CollUtil.isEmpty(userList)){

            countDataService.saveOrUpdate(data, updateWrapper);
            return;
        }

        CommonLotteryDTO dto = new CommonLotteryDTO();
        dto.setPeriod(1);
        dto.setUserIds(userList);


        // 根据用户列表查询订单的销售金额
        List<AmountCountDTO> amountCountDTOS = orderService.queryAmountCount(dto);

        for (AmountCountDTO amountCountDTO : amountCountDTOS) {
            int matchType = amountCountDTO.getMatchType();
            if (matchType == 1) {
                data.setFootballSaleAmt(amountCountDTO.getAmount().add(data.getFootballSaleAmt()));
            } else if (matchType == 2) {
                data.setBasketballSaleAmt(amountCountDTO.getAmount().add(data.getBasketballSaleAmt()));
            } else if (matchType == 3) {
                data.setPlsSaleAmt(amountCountDTO.getAmount().add(data.getPlsSaleAmt()));
            } else if (matchType == 4) {
                data.setDltSaleAmt(amountCountDTO.getAmount().add(data.getDltSaleAmt()));
            }
        }

        // 根据用户列表查询充值金额
        List<RechargeCountDTO> rechargeCountDTOS = userRechargeRecordDao.queryCountData(dto);
        for (RechargeCountDTO rechargeCountDTO : rechargeCountDTOS) {
            String paymentWay = rechargeCountDTO.getPaymentWay();
            if ("2".equals(paymentWay)) {
                data.setAlipayRecharge(rechargeCountDTO.getRechargeAmount().add(data.getAlipayRecharge()));
            } else if ("3".equals(paymentWay)) {
                data.setStaffRecharge(rechargeCountDTO.getRechargeAmount().add(data.getStaffRecharge()));
            } else if ("4".equals(paymentWay)) {
                data.setDispatchAmt(rechargeCountDTO.getRechargeAmount().add(data.getDispatchAmt()));
            }
        }

        // 查询当天新注册用户数量
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfo::getId, userList)
                .apply("date_format(now(),'%Y-%m-%d')=date_format(create_time, '%Y-%m-%d')");
        Long newUserNum = userInfoDao.selectCount(queryWrapper);
        data.setNewUserNum(newUserNum.intValue());

        // 查询用户余额总和
        double sum = userInfoDao.selectList(new QueryWrapper<UserInfo>().in("id", userList))
                .stream()
                .map(UserInfo::getBalance)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        data.setUserBalance(BigDecimal.valueOf(sum));

        countDataService.saveOrUpdate(data, updateWrapper);
    }

}
