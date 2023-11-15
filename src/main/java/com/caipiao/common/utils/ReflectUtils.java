package com.caipiao.common.utils;

import java.lang.reflect.Field;

/**
 * @author xiaoyinandan
 * @date 2021/12/8 下午10:35
 */
public class ReflectUtils {

    public static  <T> T transfer(T t)  {
        try{
            Class<?> tClass = t.getClass();

            Field[] declaredFields = tClass.getDeclaredFields();

            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object o = declaredField.get(t);
                String name = declaredField.getName();
                if(o instanceof String && !name.equals("allManageFlag") && !name.equals("priceChangeType")){
                    declaredField.set(t, "'" + o +"'");
                }

            }
        }catch (Exception e){

        }


        return t;
    }

    public static <T> boolean isAllFieldNull(Object obj, Class<T> t) {
        Field[] fs = t.getDeclaredFields();//得到属性集合
        boolean flag = true;
        for (Field f : fs) {//遍历属性
            f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
            try{
                Object val = f.get(obj);// 得到此属性的值
                if(val!=null) {//只要有1个属性不为空,那么就不是所有的属性值都为空
                    flag = false;
                    break;
                }
            }catch (Exception e){
                return false;
            }
        }
        return flag;
    }


}
