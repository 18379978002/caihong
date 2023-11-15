package com.caipiao.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.dao.SysOssVideoDao;
import com.caipiao.modules.oss.entity.SysOssVideo;
import com.caipiao.modules.oss.entity.req.VideoReq;
import com.caipiao.modules.oss.service.SysOssVideoService;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SysOssVideoServiceImpl extends ServiceImpl<SysOssVideoDao, SysOssVideo> implements SysOssVideoService {


    @Autowired
    private OSSFactory ossFactory;

    @Override
    @Transactional
    public void delete(SysOssVideo video) {
        //删除七牛云文件
//        int delete = ossFactory.build().delete(video.getFolder() + video.getFileName(), videoBucket);
//        if(delete == 200)
            this.removeById(video.getId());
    }

    @Override
    public IPage<SysOssVideo> queryPage(IPage<SysOssVideo> pg, VideoReq req) {
        return baseMapper.queryPage(pg, req);
    }

    @Override
    @Transactional
    public void changeHot(String ids, SysCompStaffEntity user) {
        List<SysOssVideo> videos = new ArrayList<>();
        for(String id : ids.split(",")){
            SysOssVideo video = this.getById(Long.parseLong(id));
            if(null == video || !user.getCompanyId().equals(video.getCompanyId())){
                throw new RRException("视频不存在");
            }
            video.setIsHot(true);
            videos.add(video);
        }

        this.updateBatchById(videos);

    }

    @Override
    @Transactional
    public void changeCollect(String ids, SysCompStaffEntity user) {
        List<SysOssVideo> videos = new ArrayList<>();
        for(String id : ids.split(",")){
            SysOssVideo video = this.getById(Long.parseLong(id));
            if(null == video || !user.getCompanyId().equals(video.getCompanyId())){
                throw new RRException("视频不存在");
            }
            video.setIsCollect(true);
            videos.add(video);
        }

        this.updateBatchById(videos);
    }
}
