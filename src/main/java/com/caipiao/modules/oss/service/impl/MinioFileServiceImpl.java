package com.caipiao.modules.oss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.oss.dao.MinioFileDao;
import com.caipiao.modules.oss.entity.MinioEntity;
import com.caipiao.modules.oss.service.MinioFileService;
import org.springframework.stereotype.Service;

@Service("minioFileService")
public class MinioFileServiceImpl extends ServiceImpl<MinioFileDao, MinioEntity> implements MinioFileService {

}
