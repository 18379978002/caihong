package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2021/12/23 下午2:56
 */
@Data
@TableName("sys_company_miniapp")
public class CompanyMiniapp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * app_id
     */
    private String appId;

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

    /**
     * company_status
     */
    private int companyStatus;

    /**
     * msg_data_format
     */
    private String msgDataFormat;

    public CompanyMiniapp() {}
}
