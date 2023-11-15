package com.caipiao.common.utils;

import java.math.BigDecimal;

/**
 * 常量
 *
 */
public class Constant {

    public static final String BASE_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public final static String SUPER_ADMINISTRATOR_ROLE_ST = "super";

    public final static Double COMMISSION_RATE = 0.004;

    public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
    public static final String JWT_KEY_ID = "staffId";
    public static final String JWT_KEY_USER_NAME = "staffName";
    public static final String JWT_KEY_USER_AVATAR = "avatar";
    public static final String USER_DEFAULT_AVATAR = "382/46/1d6fd5f8-bbbe-4492-9ef8-d3315d9a59f4.jpg";
    public static final String STAFF_DEFAULT_AVATAR = "009/54/54488fe1-0ea2-4cab-8b38-9ec3bf81fe48.jpg";

    public static final String JWT_KEY_COMPANY_ID = "companyId";
    public static final String JWT_KEY_STAFF_STATUS = "staffStatus";
    public static final String JWT_KEY_POSITION = "position";
    public static final String JWT_KEY_STAFF_MOBILE = "mobile";
    public static final String JWT_KEY_STAFF_TENANT = "Tenant";
    public static final String JWT_KEY_STAFF_USERID = "userId";
    /** 超级管理员ID */
    public static final String SUPER_ADMIN = "YJ4726SPP8853lssoonShuPingadminshupp";
    /** 根公司 */
    public static final String ROOT_COMPANY_ID = "10000";
    /** 根公司 */
    public static final String SUPER_STAFF_STATUS = "S0A";

    /** 公司超级管理员 */
    public static final String COMPANY_SUPER_STAFF_STATUS = "COMPANY";
    public static final String COMPANY_SUPER_STAFF_STATUS_VALID = "S0X";
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";

    /**
     *  是
     */
    public static final String YES = "1";

    /**
     *  否
     */
    public static final String NO = "0";


    public static final Integer STATUS_0 = 0;
    public static final Integer STATUS_1 = 1;
    public static final Integer STATUS_2 = 2;
    public static final Integer STATUS_3 = 3;
    public static final Integer STATUS_4 = 4;



    public static final String FILE = "file";
    public static final String IMAGE = "image";

    /**
     * 菜单类型
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年11月15日 下午1:24:29
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 定时任务状态
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 状态
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum Status {
        /**
         * 禁止
         */
        DISABLE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 状态
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum Level {
        /**
         * 禁止
         */
        ONE(1),
        /**
         * 正常
         */
        TWO(2);

        private int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 支付状态
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum PayStatus {
        /**
         * 0、未支付
         */
        NOT_PAY(0),
        /**
         * 1、已支付
         */
        PAY_SUCCESS(1),

        /**
         * 2、支付超时',
         *
         */
        PAY_TIMEOUT(2);

        private int value;

        PayStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 支付类型
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum PaymentWay {
        /**
         * 1、微信
         */
        WECHAT_PAY(1),
        /**
         * 2、支付宝
         */
        ALIPAY(2);

        private int value;

        PaymentWay(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * VIP时长
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum VipTime {
        /**
         * 1、月度
         */
        MONTH(1),
        /**
         * 2、季度
         */
        SEASON(2),
        /**
         * 3、半年
         */
        HALF_YEAR(3),
        /**
         * 4、年度
         */
        FULL_YEAR(4);

        private int value;

        VipTime(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
