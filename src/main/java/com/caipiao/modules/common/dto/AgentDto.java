package com.caipiao.modules.common.dto;

import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentDto extends SysCompStaffEntity {
    private BigDecimal allSale;
}
