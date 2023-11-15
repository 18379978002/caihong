package com.caipiao.modules.company.entity.enmu;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum AuthenticationStatus implements IEnum<Integer> {
    Authenticated(1),
    UNAuthenticated(2),
    FailedAuthenticated(3)
    ;
    private Integer status;

    AuthenticationStatus(Integer status){
        this.status=status;
    }

    @Override
    public Integer getValue() {
        return this.status;
    }
}
