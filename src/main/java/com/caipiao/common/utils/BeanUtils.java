package com.caipiao.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: create by xiaoyinandan
 * @version: v1.0
 * @description: Bean工具类
 * @date:2019/4/25
 */
public class BeanUtils {

    /**
     * 将原对象的属性值拷贝到目标对象，并返回目标对象.
     *
     * @param source source
     * @param target target
     * @return T
     */
    public static <T> T copyProperties(Object source, T target) {
        if (null == source || null == target) {
            return target;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 将原对象的属性值拷贝到目标对象，并返回目标对象.
     *
     * @param source source
     * @param targetClazzType targetClazzType
     * @return T
     */
    public static <T> T copyProperties(Object source, Class<T> targetClazzType) {
        if (null == source) {
            return null;
        }
        try {
            T target = targetClazzType.newInstance();
            return copyProperties(source, target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T copyProperties(JSONObject source, Class<T> targetClazzType) {
        if (null == source) {
            return null;
        }
        T target = null;
        try {
            Method[] methods = targetClazzType.getDeclaredMethods();
            target = targetClazzType.newInstance();
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(target, new Object[]{ source.get(field) });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;

    }

    /**
     * 将集合拷贝成目标类集合.
     *
     * @param sourceList sourceList
     * @param targetClazzType targetClazzType
     * @return List
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClazzType) {
        if (null == sourceList || sourceList.size()==0|| null == targetClazzType) {
            return new ArrayList<T>();
        }
        List<T> targetList = new ArrayList<T>();
        if (sourceList.size() > 0) {
            for (Object source : sourceList) {
                targetList.add(copyProperties(source, targetClazzType));
            }
        }
        return targetList;
    }

    /**
     * 克隆属性到另一个对象.
     *
     * @param from from
     * @param to to
     * @param excludsArray  excludsArray 排除的属性列表
     * @throws Exception Exception
     */
    @SuppressWarnings("rawtypes")
    public static void copyPropertiesExclude(Object from, Object to, String[] excludsArray)
            throws Exception {
        List<String> excludesList = null;
        if (excludsArray != null && excludsArray.length > 0) {
            excludesList = Arrays.asList(excludsArray); // 构造列表对象
        }
        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod = null;
        Method toMethod = null;
        String fromMethodName = null;
        String toMethodName = null;
        for (int i = 0; i < fromMethods.length; i++) {
            fromMethod = fromMethods[i];
            fromMethodName = fromMethod.getName();
            if (!fromMethodName.contains("get")) {
                continue;
            }
            // 排除列表检测
            if (excludesList != null
                    && excludesList.contains(fromMethodName.substring(3).toLowerCase())) {
                continue;
            }
            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);
            if (toMethod == null) {
                continue;
            }
            Object value = fromMethod.invoke(from, new Object[0]);
            if (value == null) {
                continue;
            }
            // 集合类判空处理
            if (value instanceof Collection) {
                Collection newValue = (Collection) value;
                if (newValue.size() <= 0) {
                    continue;
                }
            }
            toMethod.invoke(to, new Object[] { value });
        }
    }

    /**
     * 从方法数组中获取指定名称的方法.
     *
     * @param methods methods
     * @param name name
     * @return Method
     */
    public static Method findMethodByName(Method[] methods, String name) {
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name)) {
                return methods[j];
            }
        }
        return null;
    }

    /**
     * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
     *
     * @param obj
     * @return
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

}
