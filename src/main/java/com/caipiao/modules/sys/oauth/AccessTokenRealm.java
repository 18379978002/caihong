package com.caipiao.modules.sys.oauth;

import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.config.JwtProperties;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import com.caipiao.modules.sys.service.ShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 认证
 *
 */
@Component
@Slf4j
@EnableConfigurationProperties(value = {JwtProperties.class})
public class AccessTokenRealm extends AuthorizingRealm {
    @Resource
    private ShiroService shiroService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    ISysCompStaffService sysCompStaffService;;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof AccessUserToken;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserInfo user = (UserInfo) principals.getPrimaryPrincipal();
        String userId = user.getId();
        SysCompStaffEntity staffEntity = sysCompStaffService.getByUserId(userId);
        //用户权限列表
        Set<String> permsSet = shiroService.getUserPermissions(userId, staffEntity.getCompanyId());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        if (accessToken == null) {
            log.debug("登录失效：{}", accessToken);
            throw new RRException("请登录", 401);
        }
        UserInfo session = (UserInfo) redisComponent.get(accessToken);
        if (session == null) {
            log.debug("登录失效：{}", accessToken);
            throw new RRException("登录失效", 401);
        }

        // 获取用户信息并检查用户是否被删除
        UserInfo userInfo = userInfoService.getById(session.getId());
        if (userInfo == null || userInfo.getDelFlag().equals(Constant.YES)) {
            throw new RRException("用户不存在", 401);
        }
        TenantService.putTenantContext(new TenantContext(String.valueOf(userInfo.getSubordinateStore())));
        return new SimpleAuthenticationInfo(userInfo, accessToken, getName());
    }
}
