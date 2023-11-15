package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description 租户应用管理
 * @author ls
 * @date 2022-01-09
 */
@Data
@TableName("sys_company_app")
public class CompanyApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * app_code
     */
    private String appCode;

    /**
     * app_name
     */
    private String appName;

    /**
     * parent_id
     */
    private Long parentId;

    /**
     * 层级 1、2
     */
    private int level;

    /**
     * 租户id
     */
    private String companyId;

    /**
     * app_url
     */
    private String appUrl;

    /**
     * 图标
     */
    private String appIcon;

    /**
     * 链接类型1、内部路由 2、外部链接
     */
    private String appUrlType;

    /**
     * 价格
     */
    private BigDecimal appPrice;

    /**
     * 开始日期
     */
    private Date appStartDate;

    /**
     * 结束日期
     */
    private Date appEndDate;

    private Integer sortNumber;

    /**
     * 状态 1、正常 0、禁止
     */
    private int state;
    @TableField(exist = false)
    private List<CompanyApp> children;

    public CompanyApp() {}
}
