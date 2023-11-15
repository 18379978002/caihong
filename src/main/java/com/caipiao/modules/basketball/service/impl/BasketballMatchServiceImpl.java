package com.caipiao.modules.basketball.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.basketball.dao.BasketballMatchDao;
import com.caipiao.modules.basketball.entity.BasketballMatch;
import com.caipiao.modules.basketball.service.BasketballMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午8:54
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BasketballMatchServiceImpl extends ServiceImpl<BasketballMatchDao, BasketballMatch> implements BasketballMatchService {
}
