package com.caipiao.modules.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.entity.CountDataDTO;

import java.util.Date;
import java.util.List;

public interface CountDataService  extends IService<CountData> {
    List<CountDataDTO> getCountSaleList(Date date, int i);
}
