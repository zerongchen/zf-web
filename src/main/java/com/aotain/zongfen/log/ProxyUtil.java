package com.aotain.zongfen.log;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.log.constant.OperationType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/12
 */
public class ProxyUtil {

    public static String methodName = "";

    public static void changeVariable(Class clz,String methodName,String dataJson) throws Exception {
        changeVariable(clz,methodName,dataJson,"");
    }
    public static void changeVariable(Class clz,String methodName,String dataJson,String inputParam) throws Exception{
        changeVariable(clz, methodName, dataJson, inputParam,null,null);
    }

    public static void changeVariable( Class clz, String methodName, String dataJson, String inputParam, Integer module, OperationType operationType) throws Exception {
        Method[] methods = clz.getMethods();
        Method targetMethod = null;
        for (Method method:methods){
            if (method.getName().equals(methodName)){
                targetMethod = method;
                ProxyUtil.methodName = methodName;
            }
        }

        LogAnnotation logAnnotation = targetMethod.getAnnotation(LogAnnotation.class);
        //获取 foo 这个代理实例所持有的 InvocationHandler
        InvocationHandler h = Proxy.getInvocationHandler(logAnnotation);
        // 获取 AnnotationInvocationHandler 的 memberValues 字段
        Field hField = h.getClass().getDeclaredField("memberValues");
        // 修改field访问权限
        hField.setAccessible(true);
        // 获取 memberValues
        Map memberValues = (Map) hField.get(h);
        // 修改需要改变属性值
        memberValues.put("dataJson", dataJson);
        memberValues.put("inputParam",inputParam);
        //可修改module的值
        if(module!=null){
            memberValues.put("module",module);
        }
        if (operationType!=null){
            memberValues.put("type",operationType.getType());
        }

    }

}
