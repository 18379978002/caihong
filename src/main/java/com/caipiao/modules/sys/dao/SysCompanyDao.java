package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.sys.entity.SysCompany;
import org.springframework.stereotype.Repository;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:17
 */
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
@Repository
public interface SysCompanyDao extends BaseMapper<SysCompany> {
}
