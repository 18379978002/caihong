package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:41
 */
@Data
@TableName("sys_company_app_config")
public class CompanyAppConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * corp_id
     */
    private String corpId;

    /**
     * agent_id
     */
    private Long agentId;

    /**
     * app_name
     */
    private String appName;

    /**
     * secret
     */
    private String secret;

    /**
     * token
     */
    private String token;

    /**
     * aes_key
     */
    private String aesKey;

    /**
     * company_id
     */
    private String companyId;

    private Integer companyStatus;

    public CompanyAppConfig() {}
}
