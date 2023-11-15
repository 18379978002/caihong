package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.app.dto.RechargeCountDTO;
import com.caipiao.modules.app.dto.UserRechargeDTO;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.common.dto.CommonLotteryDTO;

import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午3:17
 */
public interface UserRechargeRecordService extends IService<UserRechargeRecord> {
    /**
     * 支付回调
     * @param out_trade_no
     */
    void payCallback(String out_trade_no, String tradeNo);

    /**
     * 支付
     * @param dto
     * @param user
     * @return
     */
    UserRechargeRecord recharge(UserRechargeDTO dto, UserInfo user);

    List<RechargeCountDTO> queryCountData(CommonLotteryDTO dto);
}
