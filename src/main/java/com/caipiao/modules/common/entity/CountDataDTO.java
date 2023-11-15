package com.caipiao.modules.common.entity;


import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CountDataDTO {
    private Integer saleSum;
    private String staffId;
    private String staffName;

    private String headimgUrl;

    public static CountDataDTO coverCountDataDTOS(CountDataDO countDataDO){
        if(null==countDataDO){
            return new CountDataDTO();
        }
        CountDataDTO countDataDTO = new CountDataDTO();
        countDataDTO.setSaleSum(countDataDO.getSaleSum());
        countDataDTO.setStaffId(countDataDO.getStaffId());
        countDataDTO.setStaffName(countDataDO.getStaffName());
        countDataDTO.setHeadimgUrl(countDataDO.getHeadimgUrl());
        return countDataDTO;
    }

    public static List<CountDataDTO> coverCountDataDTOS(List<CountDataDO> countDataDO){
        return countDataDO.stream().map(CountDataDTO::coverCountDataDTOS).collect(Collectors.toList());
    }
}
