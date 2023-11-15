package com.caipiao.modules.sys.oauth;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.SpringContextUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * oauth2过滤器
 *
 */
public class AccessUserTokenFilter extends AuthenticatingFilter {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Set<String> anonymousUrls;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token
        String token = getRequestToken((HttpServletRequest) request);

        if (StringUtils.isBlank(token)) {
            return null;
        }

        return new AccessUserToken(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }

        return false;
    }


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token，如果token不存在，直接返回401
        String token = getRequestToken((HttpServletRequest) request);
        Set<String> anonymousUrls = getAnonymousUrls();
        if(anonymousUrls.contains(((HttpServletRequest) request).getRequestURI().replace(SpringContextUtils.applicationContext.getApplicationName(),""))){
            return true;
        }
        if (StringUtils.isBlank(token)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            String json = JSON.toJSONString(R.error(HttpStatus.SC_UNAUTHORIZED, "invalid token"));
            httpResponse.getWriter().print(json);
            return false;
        }
        return executeLogin(request, response);
    }

    public Set<String> getAnonymousUrls() {
        if(null==anonymousUrls){
            Collection<RequestMappingHandlerMapping> requestMappingHandlerMappings = SpringContextUtils.applicationContext.getBeansOfType(RequestMappingHandlerMapping.class).values();
            anonymousUrls = new HashSet<>();
            for (RequestMappingHandlerMapping requestMappingHandlerMapping : requestMappingHandlerMappings) {
                for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
                    HandlerMethod handlerMethod = infoEntry.getValue();
                    AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
                    if (null != anonymousAccess) {
                        anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                    }
                }
            }
        }
        return anonymousUrls;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        //httpResponse.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            R r = R.error(HttpStatus.SC_UNAUTHORIZED, throwable.getMessage());

            String json = JSON.toJSONString(r);
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {
            logger.error("onLoginFailure", e1);
        }

        return false;
    }

    /**
     * 获取请求的token
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        //从header中获取token
        String token = httpRequest.getHeader("appToken");

        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter("appToken");
        }

        return token;
    }


}
