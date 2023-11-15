package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.sys.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色管理
 *
 */
@Mapper
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)//缓存五分钟过期
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {

    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(String createUserId);
}
