package com.caipiao.modules.sys.constant;

/**
 * 定义应用类型
 */
public enum AppEnum {

    NEWS(1, "新闻资讯"),
    NPC(2, "人大联络站");
    /**
     * 数据库属性值
     */
    private int value;

    private String appName;

    AppEnum(int type, String appName) {
        this.value = type;
        this.appName = appName;
    }

    public int getValue() {
        return this.value;
    }

    public String getAppName(){
        return this.appName;
    }

    public static AppEnum find(int type) {
        AppEnum[] values = AppEnum.values();

        for (AppEnum value : values) {
            if(value.getValue() == type){
                return value;
            }
        }

        return null;
    }
}
