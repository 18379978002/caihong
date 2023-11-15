package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.SysCompany;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:18
 */
public interface SysCompanyService extends IService<SysCompany> {
    String newCompany(SysCompany company);

    SysCompany queryDetail(String id);

    Object editCompany(SysCompany company);
}
