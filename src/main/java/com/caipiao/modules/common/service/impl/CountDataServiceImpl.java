package com.caipiao.modules.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.common.dao.CountDataDao;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.entity.CountDataDTO;
import com.caipiao.modules.common.service.CountDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CountDataServiceImpl extends ServiceImpl<CountDataDao, CountData> implements CountDataService {
    @Override
    public List<CountDataDTO> getCountSaleList(Date date, int i) {
        return CountDataDTO.coverCountDataDTOS(baseMapper.countSaleLimit(date,i));
    }
}
