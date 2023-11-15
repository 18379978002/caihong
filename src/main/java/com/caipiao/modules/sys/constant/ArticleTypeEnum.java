package com.caipiao.modules.sys.constant;

/**
 * 定义文章类型
 */
public enum ArticleTypeEnum {
    /**
     * 票据资讯
     */
    DRAFT_INFO(1),
    DRAFT_CALENDER(2),
    DRAFT_STUDY(3);
    /**
     * 数据库属性值
     */
    private int value;

    ArticleTypeEnum(int type) {
        this.value = type;
    }

    public int getValue() {
        return this.value;
    }

    public static ArticleTypeEnum of(String name) {
        try {
            return ArticleTypeEnum.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
