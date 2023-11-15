package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("minio_upload")
public class MinioEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.INPUT)
    private String id;
    private String fileName;
    private String fileSize;
    private String filePath;
    /**
     * create_time
     */
    private Date createTime;

    /**
     * company_id
     */
    private String companyId;

    /**
     * file_id
     */
    private String fileId;
}
