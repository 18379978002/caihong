package com.caipiao.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.app.dto.RechargeCountDTO;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午3:17
 */
@Repository
public interface UserRechargeRecordDao extends BaseMapper<UserRechargeRecord> {
    List<RechargeCountDTO> queryCountData(@Param("req")CommonLotteryDTO req);
}
