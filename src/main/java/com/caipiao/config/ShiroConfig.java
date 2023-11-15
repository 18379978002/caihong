package com.caipiao.config;

import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.annotation.IgnoreTenantQuery;
import com.caipiao.modules.sys.oauth.AccessTokenRealm;
import com.caipiao.modules.sys.oauth.AccessUserTokenFilter;
import com.caipiao.modules.sys.oauth.OAuth2Filter;
import com.caipiao.modules.sys.oauth.OAuth2Realm;
import com.google.common.collect.Lists;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Shiro配置
 *
 */
@Configuration
public class ShiroConfig {

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm, AccessTokenRealm accessTokenRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(Lists.newArrayList(oAuth2Realm,accessTokenRealm));
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean
    public AccessUserTokenFilter accessUserTokenFilter(){
        return new AccessUserTokenFilter();
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager,AccessUserTokenFilter accessUserTokenFilter) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        filters.put("accessToken",new AccessUserTokenFilter() );
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/sys/login", "anon");

        filterMap.put("/sys/banner/list", "anon");
        //预览PDF匿名访问
        filterMap.put("/sys/**", "oauth2");
        filterMap.put("/manage/**", "oauth2");
        filterMap.put("/ding_mobile/**", "oauth2");
        filterMap.put("/app/**", "accessToken");
        filterMap.put("/**", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
