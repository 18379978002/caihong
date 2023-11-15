package com.caipiao.modules.common.enums;

public class AppEnums {

    /**
     * 方案状态 订单状态 0未支付 1下单 2已出票 3已撤单 4、超时取消
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum PlanStatus {
        /**
         * 0、未支付
         */
        NOT_PAY(0, "未支付"),
        /**
         * 1、未出票
         */
        TICKET_NOT_OUT(1, "未出票"),

        /**
         * 2、已出票
         *
         */
        TICKET_OUT(2,"已出票"),

        /**
         * 3、已撤单
         *
         */
        ORDER_CANCEL(3,"已撤单"),

        /**
         * 4、已超时
         *
         */
        ORDER_TIMEOUT(4,"已超时"),


        ;

        private int value;
        private String desc;

        PlanStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }

    /**
     * 彩票类型
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum LotteryType {
        /**
         * 1、竞彩足球
         */
        FOOTBALL(1, "竞彩足球"),
        /**
         * 2、竞彩篮球
         */
        BASKETBALL(2, "竞彩篮球"),

        /**
         * 3、排列三
         *
         */
        PERMUTATION_THREE(3,"排列三"),

        /**
         * 4、超级大乐透
         *
         */
        SUPER_LOTTERY(4,"超级大乐透"),


        ;

        private int value;
        private String desc;

        LotteryType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }

    /**
     * 是否
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum YesOrNo {
        /**
         * 0、否
         */
        NO(0, "否"),
        /**
         * 1、是
         */
        YES(1, "是"),
        ;

        private int value;
        private String desc;

        YesOrNo(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }

    /**
     * 支付类型
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum PayType {
        /**
         * 0、余额
         */
        BALANCE(0, "余额"),
        /**
         * 1、支付宝
         */
        ALIPAY(1, "支付宝"),

        /**
         * 2、微信
         */
        WEIXIN(2, "微信"),
        ;

        private int value;
        private String desc;

        PayType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }

    /**
     * 中奖状态 1未开奖 2未中奖 3已中奖 4、已派奖
     * @author xiaoyinandan
     * @email
     * @date
     */
    public enum WinStatus {
        /**
         * 1、未开奖
         */
        LOTTERY_NOT_OUT(1, "未开奖"),

        /**
         * 2、未中奖
         *
         */
        LOTTERY_LOSING(2,"未中奖"),

        /**
         * 3、已中奖
         *
         */
        LOTTERY_WIN(3,"已中奖"),

        /**
         * 4、已派奖
         *
         */
        LOTTERY_DISPATCH_OUT(4,"已派奖"),


        ;

        private int value;
        private String desc;

        WinStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }

}
