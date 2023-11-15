package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.sys.entity.DeptEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DeptDao extends BaseMapper<DeptEntity> {

}
