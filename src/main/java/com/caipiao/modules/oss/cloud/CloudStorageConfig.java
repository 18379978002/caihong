package com.caipiao.modules.oss.cloud;

import com.caipiao.common.validator.group.QiniuGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:25
 */
@Data
@ConfigurationProperties(prefix = "qiniu")
@Configuration
public class CloudStorageConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    //类型 1：七牛  2：阿里云  3：腾讯云
    @Range(min = 1, max = 3, message = "类型错误")
    private Integer type;

    //七牛绑定的域名
    @NotBlank(message = "七牛绑定的域名不能为空", groups = QiniuGroup.class)
    @URL(message = "七牛绑定的域名格式不正确", groups = QiniuGroup.class)
    private String domain;
    //七牛ACCESS_KEY
    @NotBlank(message = "七牛AccessKey不能为空", groups = QiniuGroup.class)
    private String accessKey;
    private String prefix;
    //七牛SECRET_KEY
    @NotBlank(message = "七牛SecretKey不能为空", groups = QiniuGroup.class)
    private String secretKey;
    //七牛存储空间名
    @NotBlank(message = "七牛空间名不能为空", groups = QiniuGroup.class)
    private String bucketName;
    private String videoDomain;
    private String videoBucket;

}
