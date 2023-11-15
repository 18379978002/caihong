package com.caipiao.modules.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.entity.CountDataDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CountDataDao  extends BaseMapper<CountData> {

    CountData countData(@Param("staffId") String staffId, @Param("ym") String ym, @Param("ymd") String ymd);

    List<CountDataDO> countSaleLimit(@Param(value = "dateTime") Date dateTime, @Param(value = "limit") Integer limit);

}
