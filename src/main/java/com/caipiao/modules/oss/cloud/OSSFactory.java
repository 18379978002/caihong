package com.caipiao.modules.oss.cloud;

import com.caipiao.common.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:26
 */
@EnableConfigurationProperties(value = CloudStorageConfig.class)
@Component
public class OSSFactory {
    @Autowired
    CloudStorageConfig cloudStorageConfig;

    public  AbstractCloudStorageService build() {
        //获取云存储配置信息

        if (cloudStorageConfig.getType() == Constant.CloudService.QINIU.getValue()) {
            return new QiniuAbstractCloudStorageService(cloudStorageConfig);
        }

        return null;
    }



}
