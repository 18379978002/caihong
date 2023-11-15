package com.caipiao.modules.sys.entity.dto;

import com.caipiao.modules.sys.entity.bo.ComponentMetaData;
import lombok.Data;

import java.util.List;

@Data
public class LotteryCategoryDTO {
    private String lotteryName;
    private String lotteryNameCode;
    private List<ComponentMetaData> componentMetaDatas;
}
