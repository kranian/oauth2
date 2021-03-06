package com.david.api.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by david100gom on 2017. 9. 13.
 *
 * Github : https://github.com/david100gom
 */
public class LoginInfoInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean is = true;
        String username = request.getUserPrincipal().getName();
        logger.error(" Class : {} : Interceptor : {}", "LoginInfoInterceptor", username);
        request.setAttribute("username", username);
        return is;
    }
}
