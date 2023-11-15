package com.caipiao.modules.oss.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.oss.entity.SysOssVideo;
import com.caipiao.modules.oss.entity.req.VideoReq;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;

public interface SysOssVideoService extends IService<SysOssVideo> {
    void delete(SysOssVideo video);

    IPage<SysOssVideo> queryPage(IPage<SysOssVideo> pg, VideoReq req);

    void changeHot(String ids, SysCompStaffEntity user);

    void changeCollect(String ids, SysCompStaffEntity user);
}
