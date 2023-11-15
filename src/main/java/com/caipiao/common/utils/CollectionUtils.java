package com.caipiao.common.utils;

import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @Author
 * @Date 2021/7/25 9:31
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }
}
