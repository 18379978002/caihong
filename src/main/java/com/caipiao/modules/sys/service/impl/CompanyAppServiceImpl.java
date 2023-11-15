package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.sys.dao.CompanyAppDao;
import com.caipiao.modules.sys.entity.CompanyApp;
import com.caipiao.modules.sys.service.CompanyAppService;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/1/9 上午10:49
 */
@Service
public class CompanyAppServiceImpl extends ServiceImpl<CompanyAppDao, CompanyApp> implements CompanyAppService {
}
