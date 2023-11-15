package com.caipiao.modules.permutation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.permutation.dao.PermutationResultDao;
import com.caipiao.modules.permutation.entity.PermutationResult;
import com.caipiao.modules.permutation.service.PermutationResultService;
import org.springframework.stereotype.Service;

@Service
public class PermutationResultServiceImpl extends ServiceImpl<PermutationResultDao, PermutationResult> implements PermutationResultService {
}
