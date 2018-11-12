package com.aotain.zongfen.interceptor;


import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 导入拦截器，统一设置响应的内容类型以及编码方式
 * @author Chenzr
 */
public class ImportInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o ) throws Exception {

        httpServletResponse.setContentType("text/html;charset=UTF-8");
        return true;

    }

}
