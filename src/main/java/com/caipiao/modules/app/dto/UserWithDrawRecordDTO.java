package com.caipiao.modules.app.dto;

import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.common.entity.CountDataDO;
import com.caipiao.modules.common.entity.CountDataDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Data
@ApiModel("查看提现记录")
public class UserWithDrawRecordDTO implements Serializable {


    @ApiModelProperty("提现记录id")
    private String id;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    @ApiModelProperty("申请金额")
    @NotNull(message = "提现金额不能为空")
    private BigDecimal applyAmount;

    @ApiModelProperty("收款方式（1：银行卡）")
    private String paymentMethod;

    @NotBlank(message = "用户姓名不能为空")
    @ApiModelProperty("用户姓名")
    private String realName;

    @NotBlank(message = "支付宝账号不能为空")
    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    @ApiModelProperty("审核状态（0：审核中；1：审核通过；2：审核不通过）")
    private String status;


    public static UserWithDrawRecordDTO coverUserWithDrawRecordDTO(UserWithdrawRecord userWithDrawRecord){
        if(null==userWithDrawRecord){
            return new UserWithDrawRecordDTO();
        }
        UserWithDrawRecordDTO userWithDrawRecordDTO = new UserWithDrawRecordDTO();
        userWithDrawRecordDTO.setId(userWithDrawRecord.getId());
        userWithDrawRecordDTO.setCreateTime(userWithDrawRecord.getCreateTime());
        userWithDrawRecordDTO.setUpdateTime(userWithDrawRecord.getUpdateTime());
        userWithDrawRecordDTO.setApplyAmount(userWithDrawRecord.getApplyAmount());
        userWithDrawRecordDTO.setPaymentMethod(userWithDrawRecord.getPaymentMethod());
        userWithDrawRecordDTO.setRealName(userWithDrawRecord.getRealName());
        userWithDrawRecordDTO.setAlipayAccount(userWithDrawRecord.getAlipayAccount());
        userWithDrawRecordDTO.setStatus(userWithDrawRecord.getStatus());
        return userWithDrawRecordDTO;
    }

    public static List<UserWithDrawRecordDTO> coverUserWithDrawRecordDTOS(List<UserWithdrawRecord> userWithDrawRecord){
        return userWithDrawRecord.stream().map(UserWithDrawRecordDTO::coverUserWithDrawRecordDTO).collect(Collectors.toList());
    }
}
