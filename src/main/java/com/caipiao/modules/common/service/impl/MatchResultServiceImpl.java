package com.caipiao.modules.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.common.dao.MatchResultDao;
import com.caipiao.modules.common.entity.MatchResult;
import com.caipiao.modules.common.service.MatchResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 上午9:45
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MatchResultServiceImpl extends ServiceImpl<MatchResultDao, MatchResult> implements MatchResultService {
}
