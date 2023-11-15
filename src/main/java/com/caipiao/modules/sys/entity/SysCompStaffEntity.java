package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.modules.company.entity.enmu.AuthenticationStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@TableName(value = "TB_SM_SYSSTAFF")
public class SysCompStaffEntity implements Serializable {
    @TableId(type = IdType.INPUT)
    private String staffId;

    private String staffName;

    private Date createDate;

    private String avatar;

    private String staffPasswd;

    private String staffStatus;

    private String searchKeyWords;
    private String mobile;
    private boolean isLeader;
    private String position;
    private String jobNumber;

    @TableField(exist = false)
    private List<Long> roleIdList;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private Long deptId;

    @TableField(exist = false)
    @ApiModelProperty("认证状态")
    private AuthenticationStatus authenticationStatus;

    private Date hiredDate;

    private Date lastWorkDay;

    private String companyId;
    //账户余额
    private BigDecimal balance;
    //总金额
    private BigDecimal totalAmount;

//    对应的用户id
    private String userId;

    private String subordinateStore;

}
