package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_pm_staff_dept")
public class StaffDeptEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String deptId;

    private String companyId;

}
