package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2021/8/24 下午3:15
 */
@Data
@TableName("sys_banner")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * url
     */
    private String url;

    /**
     * order_number
     */
    private Integer orderNumber;

    /**
     * 哪个应用
     */
    private String appName;

    /**
     * company_id
     */
    private String companyId;

    /**
     * create_staff
     */
    private String createStaff;

    /**
     * create_time
     */
    private Date createTime;

    public Banner() {}
}
