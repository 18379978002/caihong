package com.caipiao.modules.football.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.football.dao.FootballMatchDao;
import com.caipiao.modules.football.entity.FootballMatch;
import com.caipiao.modules.football.service.FootballMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午8:27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FootballMatchServiceImpl extends ServiceImpl<FootballMatchDao, FootballMatch> implements FootballMatchService {
}
