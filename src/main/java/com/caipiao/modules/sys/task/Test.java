package com.caipiao.modules.sys.task;

import com.caipiao.common.utils.MD5Util;

/**
 * @author xiaoyinandan
 * @date 2022/3/19 上午9:39
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(MD5Util.getMD5AndSalt("123456"));
    }
}
