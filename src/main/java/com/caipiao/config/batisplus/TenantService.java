package com.caipiao.config.batisplus;

import com.caipiao.common.annotation.IgnoreTenantQuery;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.config.batisplus.co.TenantContext;
import com.google.common.collect.Lists;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TenantService {
    private static final ThreadLocal<TenantContext> tenantContextThreadLocal = new ThreadLocal<>();

    @Resource
    private ApplicationContext applicationContext;
    private List<String> methodSeats;

    public Boolean isQueryTenant() {
        try {
            //用户上下文如果为空,查全局,不使用租户
            ShiroUtils.isApp();
        }catch (UnavailableSecurityManagerException e){
            return false;
        }
        List<String> methodSeats = getIgnoreTenantQuerySeats();
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String methodName = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            if(methodSeats.contains(className+methodName)){
                return false;
            }
        }

        return true;
    }


    protected List<String> getIgnoreTenantQuerySeats() {
        if (null == methodSeats) {
            List<Object> beans = Arrays.stream(applicationContext.getBeanDefinitionNames()).map(item -> applicationContext.getBean(item)).collect(Collectors.toList());
            methodSeats = Lists.newArrayList();
            for (Object bean : beans) {
                Class<?> beanClass = bean.getClass();
                Method[] methods = beanClass.getMethods();
                for (Method method : methods) {
                    Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                    for (Annotation declaredAnnotation : declaredAnnotations) {
                        if (declaredAnnotation instanceof IgnoreTenantQuery) {
                            methodSeats.add(beanClass.getName() + method.getName());
                        }
                    }
                }
            }
        }
        return methodSeats;
    }

    public static void putTenantContext(TenantContext tenantContext) {
        tenantContextThreadLocal.set(tenantContext);
    }

    public TenantContext queryTenantContext() {
        return tenantContextThreadLocal.get();
    }
}
