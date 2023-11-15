package com.caipiao.modules.oss.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.DateUtils;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.dao.SysOssDao;
import com.caipiao.modules.oss.entity.SysOssEntity;
import com.caipiao.modules.oss.service.SysOssService;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:22
 */
@Service("sysOssService")
@Slf4j
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

    private final static String THUMBNAIL_PREFIX = "thumbnail_";
    public final static String TMP_PATH = "/tmp/cut_tmp/";
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysOssEntity> page = this.page(
                new Query<SysOssEntity>().getPage(params)
        );

        return new PageUtils(page);
    }


    public SysOssEntity upload(String fileName, byte[] data, OSSFactory ossFactory, MultipartFile file, File destFIle, SysCompStaffEntity user) {
        String ext = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdUtil.getSnowflake(1, 1).nextIdStr();

        String split = DateUtils.format(new Date());
        String path;
        if(user != null){
            path = user.getCompanyId() + "/" + split + "/";
        }else{
            path = split + "/";
        }


        long size = 0;

        //上传正常的文件

        if(null == data){
            try {
                size = destFIle.length();
                ossFactory.build().upload(new FileInputStream(destFIle), path + fName + ext);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            size = file.getSize();
            ossFactory.build().upload(data, path + fName + ext);
        }

        //保存文件信息
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setFileSize(size);
        ossEntity.setAddTime(new Date());
        if(user == null){
            ossEntity.setCompanyId(null);
        }else{
            ossEntity.setCompanyId(user.getCompanyId());
            ossEntity.setCreateUserId(user.getStaffId());
        }


        ossEntity.setOriginalFileName(fileName);
        ossEntity.setFileName(fName + ext);
        ossEntity.setFolder(path);
        this.save(ossEntity);
        return ossEntity;
    }

}
