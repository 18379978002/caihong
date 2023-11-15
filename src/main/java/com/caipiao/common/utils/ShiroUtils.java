package com.caipiao.common.utils;

import com.caipiao.common.exception.RRException;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 *
 */
public class ShiroUtils {

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static SysCompStaffEntity getUserEntity() {
        SysCompStaffEntity principal = (SysCompStaffEntity) SecurityUtils.getSubject().getPrincipal();
        principal.setCompanyId("10000");
        return principal;
    }

    public static UserInfo getAppClientUserEntity() {
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        return userInfo;
    }

    public static Boolean isApp(){
        return SecurityUtils.getSubject().getPrincipal() instanceof UserInfo;
    }

    public static String getAppClientUserId() {
        return getAppClientUserEntity().getId();
    }
    public static String getUserId() {
        return getUserEntity().getStaffId();
    }

    public static void setSessionAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    public static boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    public static String getKaptcha(String key) {
        Object kaptcha = getSessionAttribute(key);
        if (kaptcha == null) {
            throw new RRException("验证码已失效");
        }
        getSession().removeAttribute(key);
        return kaptcha.toString();
    }

}
