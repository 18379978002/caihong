package com.caipiao.modules.app.controller;

import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午2:39
 */
@Component
public abstract class AppAbstractController extends R {

    @Autowired
    HttpServletRequest request;
    @Autowired
    RedisComponent redisComponent;

    public UserInfo getUser(){
        return (UserInfo) redisComponent.get(request.getHeader("appToken"));
    }

    public String getUserId(){
        return getUser().getId();
    }

}
