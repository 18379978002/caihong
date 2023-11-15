package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:20
 */
@Data
@TableName("sys_oss")
public class SysOssEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 业务id
     */
    private String fileBusiId;

    /**
     * 文件夹
     */
    private String folder;

    /**
     * company_id
     */
    private String companyId;

    /**
     * file_name
     */
    private String fileName;

    private String originalFileName;

    /**
     * file_type
     */
    private String fileType;

    private Long fileSize;

    /**
     * create_user_id
     */
    private String createUserId;

    /**
     * add_time
     */
    private Date addTime;


}
