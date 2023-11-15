package com.caipiao.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.app.dao.UserWithdrawRecordDao;
import com.caipiao.modules.app.dto.WithdrawReq;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.app.service.UserWithdrawRecordService;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWithdrawRecordServiceImpl extends ServiceImpl<UserWithdrawRecordDao, UserWithdrawRecord> implements UserWithdrawRecordService {
    private final UserInfoService userInfoService;
    private final UserRechargeRecordService userRechargeRecordService;
    @Transactional
    @Override
    public void verify(WithdrawReq req, SysCompStaffEntity user) {
        UserWithdrawRecord record = this.getById(req.getRecordId());
        record.setStatus(req.getResult());
        record.setRemarks(req.getRemark());
        this.updateById(record);

        if(req.getResult().equals("1")){
            //更新已提现
            UserWithdrawRecord withdrawRecord = this.getById(req.getRecordId());

            if(withdrawRecord.getWithdrawType().equals("1")){
                //更新用户已提现
                UserInfo userInfo = userInfoService.getById(withdrawRecord.getUserId());

                userInfo.setBalance(userInfo.getBalance().subtract(withdrawRecord.getApplyAmount()));

                userInfoService.updateById(userInfo);

                //资金明细记录
                UserRechargeRecord userRechargeRecord = new UserRechargeRecord();
                userRechargeRecord.setRechargeAmount(record.getApplyAmount().negate());
                userRechargeRecord.setSubject("提现");
                userRechargeRecord.setPayStatus(1);
                //提款
                userRechargeRecord.setPaymentType(4);
                userRechargeRecord.setTransactionId(withdrawRecord.getId());
                userRechargeRecord.setUserId(userInfo.getId());
                userRechargeRecord.setCreateTime(new Date());
                userRechargeRecord.setPaymentTime(new Date());
                userRechargeRecordService.save(userRechargeRecord);

            }
        }
    }
}
