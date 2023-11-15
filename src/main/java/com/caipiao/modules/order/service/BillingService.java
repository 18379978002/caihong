package com.caipiao.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.order.dto.AddBillingDTO;
import com.caipiao.modules.order.entity.Billing;

public interface BillingService extends IService<Billing> {
    /**
     * 跟单
     * @param dto
     * @param user
     */
    Long addBilling(AddBillingDTO dto, UserInfo user);

    /**
     * 查询详情
     * @param billingId
     * @param user
     * @return
     */
    Billing queryDetail(Long billingId, UserInfo user);
}
