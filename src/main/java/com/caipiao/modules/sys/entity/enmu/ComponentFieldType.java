package com.caipiao.modules.sys.entity.enmu;


public enum ComponentFieldType {
    BOOL("bool"),
    INT("int");
    private String code;
    ComponentFieldType(String code){
        this.code=code;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
