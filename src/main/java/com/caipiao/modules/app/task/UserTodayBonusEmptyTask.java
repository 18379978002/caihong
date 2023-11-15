package com.caipiao.modules.app.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午5:02
 */
@EnableScheduling
@Component
@Slf4j
public class UserTodayBonusEmptyTask {

    @Autowired
    UserInfoService userInfoService;


    //1
    //每天的0点清空用户的当天中奖金额
    @Scheduled(cron = "0 0 0 * * ? ")
    public void emptyTodayBonus(){
        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(UserInfo::getTodayBonus, BigDecimal.ZERO);
        userInfoService.update(wrapper);
    }


}
