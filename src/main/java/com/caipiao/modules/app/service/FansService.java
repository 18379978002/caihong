package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.app.entity.Fans;
import com.caipiao.modules.app.entity.UserInfo;

public interface FansService extends IService<Fans> {
    /**
     * 关注和取消关注
     * @param userId
     * @param user
     */
    void attention(String userId, UserInfo user);
}
