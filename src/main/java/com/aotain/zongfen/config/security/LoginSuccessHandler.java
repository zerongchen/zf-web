package com.aotain.zongfen.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;


public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("LoginSuccessHandler:onAuthenticationSuccess=>"+authentication.getName());
		// 获得授权后可得到用户信息 可使用SUserService进行数据库操作
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
