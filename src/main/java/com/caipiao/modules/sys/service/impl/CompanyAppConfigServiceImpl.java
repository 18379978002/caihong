package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.sys.dao.CompanyAppConfigDao;
import com.caipiao.modules.sys.entity.CompanyAppConfig;
import com.caipiao.modules.sys.service.CompanyAppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:43
 */
@Slf4j
@Service
public class CompanyAppConfigServiceImpl extends ServiceImpl<CompanyAppConfigDao, CompanyAppConfig> implements CompanyAppConfigService {
}
