package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.common.validator.group.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:20
 */
@Data
@TableName("sys_oss_video")
public class SysOssVideo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 文件夹
     */
    @NotBlank(message = "文件夹不能为空", groups = AddGroup.class)
    private String folder;

    /**
     * company_id
     */
    private String companyId;

    /**
     * file_name
     */
    @NotBlank(message = "文件名不能为空", groups = AddGroup.class)
    private String fileName;

    @NotBlank(message = "文件原始名称不能为空", groups = AddGroup.class)
    private String originalFileName;
    @NotNull(message = "文件大小不能为空", groups = AddGroup.class)
    private Long fileSize;

    /**
     * create_user_id
     */
    private String createUserId;
    private String createStaffName;

    /**
     * add_time
     */
    private Date addTime;

    private Long categoryId;

    private Boolean isHot;

    private Boolean isCollect;

    private Integer clickNumber;

    private String coverImg;

    //视频类型0、电子书视频 1、红色电影视频
    private Integer videoType;

    //简介
    private String summary;

    @TableField(exist = false)
    private String categoryName;


}
