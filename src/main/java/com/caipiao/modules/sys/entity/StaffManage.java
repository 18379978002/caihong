package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("员工客户管理")
@TableName("tb_staff_manage")
public class StaffManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 员工id
     */
    @ApiModelProperty("员工id")
    private String staffId;

    /**
     * 客户id
     */
    @ApiModelProperty("客户id")
    private String userId;

    public StaffManage() {}
}
