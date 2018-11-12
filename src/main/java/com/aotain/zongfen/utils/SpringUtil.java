package com.aotain.zongfen.utils;

import com.aotain.common.config.LocalConfig;
import com.aotain.login.support.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.aotain.zongfen.model.system.SystemUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class SpringUtil implements ApplicationContextAware {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);

    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    public static String getSysUserName(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return Authority.getUserDetailInfo(request).getUserName();
        } catch (Exception e){
            e.printStackTrace();
            logger.error("get sysUserName error...",e);
        }
        // 返回一个默认值
        return "admin";
    }
    /**
     * 获取系统用户的用户名和区域ID
     * @return
     */
    public static SystemUser getSysUser(){
    	SystemUser sysUser = new SystemUser("admin","123456",(long)440000);
        return sysUser;
    }

    /**
     * 获取系统用户获取当前部署区域ID(流量流向区域ID和areaCode不一致，重点在于对应关系)
     * @return
     */
    public static String getSysUserAreaId(){
        return LocalConfig.getInstance().getHashValueByHashKey("system.deploy.province.iparea");
    }
}
