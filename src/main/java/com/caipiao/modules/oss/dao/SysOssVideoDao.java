package com.caipiao.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.modules.oss.entity.SysOssVideo;
import com.caipiao.modules.oss.entity.req.VideoReq;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysOssVideoDao extends BaseMapper<SysOssVideo> {
    IPage<SysOssVideo> queryPage(IPage<SysOssVideo> pg, @Param("req") VideoReq req);
}
