package com.aotain.zongfen.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;


@Component
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
 
    /*
      * <p>Title: setAuthenticationManager</p>
      * <p>指定AuthenticationManager</p>
      * @param authenticationManager
      */

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        System.out.println("setAuthenticationManager(AuthenticationManager) - start");
        super.setAuthenticationManager(authenticationManager);
        System.out.println("setAuthenticationManager(AuthenticationManager) - end");
    }

    /*
      * <p>Title: attemptAuthentication</p>
      * <p>登录验证做的出来: </p>
      * @param request
      * @param response
      * @return
      * @throws AuthenticationException
      */

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    	System.out.println("attemptAuthentication(HttpServletRequest, HttpServletResponse) - start"); //$NON-NLS-1$

    	System.out.println("attemptAuthentication(HttpServletRequest, HttpServletResponse) - end"); //$NON-NLS-1$
        return super.attemptAuthentication(request, response);
    }

    /*
      * <p>Title: successfulAuthentication</p>
      * <p>登录成功: </p>
      * @param request
      * @param response
      * @param chain
      * @param authResult
      * @throws IOException
      * @throws ServletException
      */

    @Override
    protected void successfulAuthentication(HttpServletRequest request,HttpServletResponse response,
        FilterChain chain,Authentication authResult) throws IOException, ServletException {
    	System.out.println("successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication) - start"); 
        super.successfulAuthentication(request, response, chain, authResult);
        System.out.println(new StringBuffer("登录成功！用户是:").append(authResult.getName()));
        request.getSession().setAttribute("user", authResult.getName());
        System.out.println("successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication) - end"); 
    }

    /*
      * <p>Title: unsuccessfulAuthentication</p>
      * <p>登录失败: </p>
      * @param request
      * @param response
      * @param failed
      * @throws IOException
      * @throws ServletException
      */

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)throws IOException, ServletException {
    	System.out.println("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - start"); 
        super.unsuccessfulAuthentication(request, response, failed);
        System.out.println("登录失败！");
        System.out.println("unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException) - end"); 
    }

}