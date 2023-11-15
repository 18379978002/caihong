package com.caipiao.modules.sys.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.sys.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统配置信息
 *
 */
@Mapper
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SysConfigDao extends BaseMapper<SysConfigEntity> {

    /**
     * 根据key，查询value
     */
    SysConfigEntity queryByKey(@Param("paramKey") String paramKey, @Param("companyId") String companyId);

    /**
     * 根据key，更新value
     */
    int updateValueByKey(@Param("paramKey") String paramKey, @Param("paramValue") String paramValue);

}
