package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.TbLotteryTypeConf;
import com.caipiao.modules.sys.entity.enmu.LotteryCategory;

import java.util.List;
import java.util.Map;

public interface TbLotteryTypeConfService extends IService<TbLotteryTypeConf> {


    Map<String, String> queryLotteryCateggorySettings(LotteryCategory lotteryCategory, List<String> fieldNames);

    void putLotteryCateggorySettings(LotteryCategory lotteryCategory, String fieldName, String fieldValue);
}

