package com.caipiao.modules.sys.oauth;

import com.caipiao.common.utils.JwtUtils;
import com.caipiao.config.JwtProperties;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.service.ShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 认证
 *
 */
@Component
@Slf4j
@EnableConfigurationProperties(value = {JwtProperties.class})
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysCompStaffEntity user = (SysCompStaffEntity) principals.getPrimaryPrincipal();
        String userId = user.getStaffId();

        //用户权限列表
        Set<String> permsSet = shiroService.getUserPermissions(userId, user.getCompanyId());

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
        SysCompStaffEntity sysCompStaffEntity = JwtUtils.getInfoFromToken(accessToken, jwtProperties.getPublicKey());
        //插入租户
        TenantService.putTenantContext(new TenantContext(sysCompStaffEntity.getSubordinateStore()));
        return new SimpleAuthenticationInfo(sysCompStaffEntity, accessToken, getName());
    }
}
