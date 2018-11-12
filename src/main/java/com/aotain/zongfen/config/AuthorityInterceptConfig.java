package com.aotain.zongfen.config;

import com.aotain.common.utils.monitorstatistics.ExceptionCollector;
import com.aotain.login.support.PermissionFilter;
import com.aotain.login.support.RestLoginFilter;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 权限拦截配置bean
 *
 * @author daiyh@aotain.com
 * @date 2018/06/20
 */
@Configuration
public class AuthorityInterceptConfig {

    private static final Logger LOG= LoggerFactory.getLogger(AuthorityInterceptConfig.class);
    /**
     * http类型
     */
    @Value("${passport.service.type}")
    private String httpType;
    /**
     * passport的IP地址
     */
    @Value("${passport.host.domain}")
    private String passportDomain;
    /**
     * 端口
     */
    @Value("${passport.host.port}")
    private String servicePort;
    /**
     * 当前系统在权限系统的部署id
     */
    @Value("${service.local.deployid}")
    private String deployid;
    /**
     * 登录的白名单
     */
    @Value("${service.sso.whiteuri}")
    private String whiteuri;

    @Value("${server.port}")
    private String deployPort;

    /**
     * 登陆页面地址
     */
    private String loginUrl;

    public String getAuthPath(){
        return httpType+"://"+passportDomain+":"+servicePort+"/";
    }

    @Value("${service.sso.loginurl}")
    private String serviceSsoLoginurl;

    @Bean
    public FilterRegistrationBean ssoFilter() {
        loginUrl = "http://"+ serviceSsoLoginurl+":"+deployPort+"/login";
        LOG.info("AuthorityInterceptConfig,loginUrl="+loginUrl);
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RestLoginFilter());
        registration.addInitParameter("SSOURL", httpType+"://"+passportDomain+":"+servicePort);
        registration.addInitParameter("DEPLOYID", deployid);
        registration.addInitParameter("WHITEURI", whiteuri);
        //登陆页面的url
        registration.addInitParameter("LOGINURL", loginUrl);
        registration.addUrlPatterns("*");
        registration.setName("RestLoginFilter");
        registration.setOrder(100);
        return registration;
    }

    /**
     * 获取权限拦截
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    @Bean
    public FilterRegistrationBean filterDemo3Registration() throws IOException, DocumentException {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new PermissionFilter());
        registration.addInitParameter("SSOURL", httpType+"://"+passportDomain+":"+servicePort);
        registration.addInitParameter("SSOPORT", servicePort);
        registration.addInitParameter("DEPLOYID", deployid);
        registration.addInitParameter("WHITEURI", whiteuri);
        //ACTIONPREVTAG 在本系统使用功能权限的时候有时需要加上一个前缀，这个就是这个前缀
        //比如，在spring security中权限都必须记上ROLE_
        //如果在本系统使用的权限的功能代码与权限系统的一样，此处就不用管
        registration.addInitParameter("ACTIONPREVTAG", "");
        //MENUPREVTAG 在本系统使用菜单权限的时候有时需要加上一个前缀，这个就是这个前缀
        registration.addInitParameter("MENUPREVTAG", "");
        registration.addUrlPatterns("*");
        registration.setName("AuthorityFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

}
