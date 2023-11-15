package com.caipiao.modules.sys.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StaffManageDTO implements Serializable {
    @ApiModelProperty("员工ID")
    private String staffId;
    @ApiModelProperty("客户ID数组")
    private List<String> userIds = new ArrayList<>();

}
