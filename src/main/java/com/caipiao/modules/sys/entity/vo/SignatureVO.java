package com.caipiao.modules.sys.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 09:46
 */
@Data
public class SignatureVO implements Serializable {
    private String appId;

    private String nonceStr;

    private long timestamp;

    private String url;

    private String signature;

    private Integer agentId;
}
