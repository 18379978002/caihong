package com.caipiao.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.oss.entity.SysOssEntity;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:22
 */
public interface SysOssService extends IService<SysOssEntity> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    SysOssEntity upload(String fileName, byte[] data, OSSFactory ossFactory, MultipartFile file, File destFIle, SysCompStaffEntity user);

    default SysOssEntity upload(String fileName, OSSFactory ossFactory, File destFIle, SysCompStaffEntity user) {
        return this.upload(fileName, null, ossFactory, null, destFIle, user);
    }

    default SysOssEntity upload(String fileName, byte[] data, OSSFactory ossFactory, MultipartFile file, SysCompStaffEntity user) {
        return this.upload(fileName, data, ossFactory, file, null, user);
    }
}
