package com.caipiao.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.sys.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单管理
 *
 */
@Mapper
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)//缓存五分钟过期
public interface SysMenuDao extends BaseMapper<SysMenuEntity> {

    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     */
    List<SysMenuEntity> queryListParentId(@Param("parentId") Long parentId, @Param("companyId") String companyId);

    /**
     * 获取不包含按钮的菜单列表
     */
    List<SysMenuEntity> queryNotButtonList(String companyId);

}
