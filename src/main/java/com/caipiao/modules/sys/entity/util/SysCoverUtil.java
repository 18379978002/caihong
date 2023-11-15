package com.caipiao.modules.sys.entity.util;

import com.caipiao.modules.sys.entity.dto.LotteryCategoryDTO;
import com.caipiao.modules.sys.entity.enmu.LotteryCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SysCoverUtil {
    SysCoverUtil INSTANCES = Mappers.getMapper(SysCoverUtil.class);


    List<LotteryCategoryDTO> coverLotteryCategoryDTOs(LotteryCategory[] values);
}
