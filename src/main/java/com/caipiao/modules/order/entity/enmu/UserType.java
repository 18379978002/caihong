package com.caipiao.modules.order.entity.enmu;


import com.baomidou.mybatisplus.annotation.IEnum;

public enum UserType implements IEnum<String> {
    AGENT("AGENT","代理用户"),
    ADMINISTRATOR("ADMINISTRATOR","管理员"),
    USER("USER","用户"),
    ;

    UserType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    private String code;
    private String desc;

    @Override
    public String getValue() {
        return code;
    }
}
