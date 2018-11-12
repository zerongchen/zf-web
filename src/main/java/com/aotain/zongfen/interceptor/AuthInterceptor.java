package com.aotain.zongfen.interceptor;


import com.alibaba.fastjson.JSON;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;


/**
 * 用户权限拦截器
 *
 * @author
 * @Date 2017年9月5日
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)
            throws Exception {
        RequiresPermission requiresPermission = ((HandlerMethod) handler).getMethodAnnotation(RequiresPermission.class);

        if (requiresPermission != null) {

            HttpSession httpSession = request.getSession();
            String[] permissions = requiresPermission.value();
            boolean flag = false;
            for (String permission : permissions) {
                if(httpSession.getAttribute(permission)!=null && String.valueOf(httpSession.getAttribute(permission)).equalsIgnoreCase("1")){
                    flag = true;
                    break ;
                }
            }

            if(!flag){
                response.sendRedirect(request.getContextPath() + "/nopermission");
                return false;
            }
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView)
            throws Exception {
        HttpSession httpSession = request.getSession();
        Enumeration<String> permissionStrs = httpSession.getAttributeNames();
        while(permissionStrs.hasMoreElements()){
            String str = permissionStrs.nextElement();
            if ("通用管理策略".equals(str)){
                httpSession.setAttribute("generalManageStrategy",1);
            }
            else if ("信息推送触发库".equals(str)){
                httpSession.setAttribute("informationPushLibrary",1);
            }else if ("Web分类库".equals(str)){
                httpSession.setAttribute("webClassifyLibrary",1);
            } else if ("IP地址库".equals(str)){
                httpSession.setAttribute("ipAddressLibrary",1);
            } else if ("应用名称对应表".equals(str)){
                httpSession.setAttribute("appNameLibrary",1);
            } else if ("HTTPGET黑白名单".equals(str)){
                httpSession.setAttribute("httpGetLibrary",1);
            } else if ("BRAS信息管理".equals(str)){
                httpSession.setAttribute("brasInformationLibrary",1);
            }
            else if ("系统设置".equals(str)){
                httpSession.setAttribute("systemManage",1);
            }
            else if ("操作日志".equals(str)){
                httpSession.setAttribute("operationLog",1);
            } else if ("系统参数设置".equals(str)){
                httpSession.setAttribute("systemParameter",1);
            } else if ("地区管理".equals(str)){
                httpSession.setAttribute("areaManager",1);
            }
        }

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
