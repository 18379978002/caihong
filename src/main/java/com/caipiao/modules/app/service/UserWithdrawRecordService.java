package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.app.dto.WithdrawReq;
import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:47
 */
public interface UserWithdrawRecordService extends IService<UserWithdrawRecord> {
    void verify(WithdrawReq req, SysCompStaffEntity user);
}
