package com.caipiao.modules.permutation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.permutation.dao.LotteryResultDao;
import com.caipiao.modules.permutation.entity.LotteryResult;
import com.caipiao.modules.permutation.service.LotteryResultService;
import org.springframework.stereotype.Service;

@Service
public class LotteryResultServiceImpl extends ServiceImpl<LotteryResultDao, LotteryResult> implements LotteryResultService {
}
