package com.caipiao.common.utils;

public class Validator {

    // 反证法
    public static boolean canExactDivisor(int big, int small) {
        int result = big / small;
        return result * small == big;
    }

    // 正则匹配
    public static boolean testExactDivisor(int big, int small) {
        double result = (double) big / small;
        String str = String.valueOf(result);
        return str.matches("^([1-9]\\d*(\\.0+))$");
    }
}

