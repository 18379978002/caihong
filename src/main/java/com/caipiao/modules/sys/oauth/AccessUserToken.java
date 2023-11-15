package com.caipiao.modules.sys.oauth;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * token
 *
 */
public class AccessUserToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;
    private String token;

    public AccessUserToken(String token) {
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
