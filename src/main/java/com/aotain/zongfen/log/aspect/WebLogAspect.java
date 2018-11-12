package com.aotain.zongfen.log.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.mapper.system.OperationLogMapper;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.utils.LogUtil;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.system.OperationLog;
import com.aotain.zongfen.service.system.IOperationLogService;
import com.aotain.zongfen.utils.SpringUtil;


@Aspect
@Component
public class WebLogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(WebLogAspect.class);
    
    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Pointcut("execution(* com.aotain.zongfen.controller..*.*(..))")//两个..代表所有子目录，最后括号里的两个..代表所有参数
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();
        		
        // 记录下请求内容
       /* LOG.info("请求地址 : " + request.getRequestURL().toString());
        LOG.info("HTTP METHOD : " + request.getMethod());
        LOG.info("请求IP : 端口 = " + LogUtil.getCliectIp(request) + " : " + request.getLocalPort());
        LOG.info("类方法 : " + signature.getDeclaringTypeName() + "." + signature.getName());
        LOG.info("参数 : " + Arrays.toString(joinPoint.getArgs()));*/
    }


//    @AfterReturning(returning = "ret", pointcut = "logPointCut()")// returning的值和doAfterReturning的参数名一致
//    public void doAfterReturning(Object ret) throws Throwable {
//        // 处理完请求，返回内容 JSON.toJSONString(ret)
//        LOG.info("返回值 : " + ret );
//    }


    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    	
    	ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        long startTime = System.currentTimeMillis();
        Object ob = pjp.proceed();// ob 为方法的返回值
        ResponseResult result = null;
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        if( ob instanceof ResponseResult) {
        	result =  (ResponseResult)ob;
        	keys = result.getKeys();
        }
        if(keys != null && keys.size() > 0) {
        	for(BaseKeys bk: keys) {
        		OperationLog opLog = new OperationLog();
        		opLog.setClientIp(LogUtil.getCliectIp(request));
                opLog.setClientPort(request.getLocalPort());
                opLog.setInputParam(Arrays.toString(pjp.getArgs()));
                opLog.setOperUser(SpringUtil.getSysUserName());
                opLog.setServerName(request.getServerName());
                opLog.setOperTime(new Date());
                if(bk.getDataType()!=null && bk.getDataType().intValue() == DataType.POLICY.getType().intValue()) {
                	if(bk.getMessageNo() != null) {
                		opLog.setDataJson("messageNo="+bk.getMessageNo());
                	}else if(bk.getId() != null){
                		if(bk.getIdName()!=null) {
                			opLog.setDataJson(bk.getIdName()+"="+bk.getId());
                		}else {
                			opLog.setDataJson("ID="+bk.getId());
                		}
                	}
                }else if(bk.getDataType()!=null && bk.getDataType().intValue() == DataType.FILE.getType().intValue()) {
                	if(bk.getId() != null){
                		if(bk.getIdName()!=null) {
                			opLog.setDataJson(bk.getIdName()+"="+bk.getId()+",fileName="+bk.getFileName());
                		}else {
                			opLog.setDataJson("ID="+bk.getId()+",fileName="+bk.getFileName());
                		}
                	}else {
                		opLog.setDataJson("fileName="+bk.getFileName());
                	}
                	
                }else if(bk.getDataType()!=null && bk.getDataType().intValue() == DataType.UPLOAD.getType().intValue()){
                	if(bk.getMessageNos() != null) {
                		opLog.setDataJson("messageNo="+JSON.toJSONString(bk.getMessageNos())+", bindMessageNo="+ JSON.toJSONString( bk.getBindMessageNo()));
                	}else if(bk.getId() != null){
                		if(bk.getIdName()!=null) {
                			opLog.setDataJson(bk.getIdName()+"="+bk.getId()+", packetType="+bk.getPacketType()+", packetSubtype="+bk.getPacketSubtype());
                		}else {
                			opLog.setDataJson("ID="+bk.getId()+", packetType="+bk.getPacketType()+", packetSubtype="+bk.getPacketSubtype());
                		}
                		
                	}
                }else {//other
                	if(bk.getMessage() != null){
                		opLog.setDataJson(bk.getMessage());
                	}
                }
                if(bk.getMessageType()!=null && bk.getOperType()!=null) {
                	opLog.setOperModel(ModelType.getModelType(bk.getMessageType()));
                    opLog.setOperType(bk.getOperType());
                    operationLogService.addLog(opLog);
                }
        	}
        }
        
        LOG.info("耗时 : " + (System.currentTimeMillis() - startTime) +"ms");
        return ob;
    }

    @After("@annotation(com.aotain.zongfen.annotation.LogAnnotation)")
    public void addOperationLog(JoinPoint joinPoint) throws Exception {
        try{
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            OperationLog operationLog = new OperationLog();
            operationLog.setOperUser(SpringUtil.getSysUserName());
            operationLog.setOperTime(new Date());
            operationLog.setClientIp(LogUtil.getCliectIp(request));
            operationLog.setClientPort(request.getLocalPort());
            operationLog.setServerName(request.getServerName());

            LogAnnotation logAnnotation = getServiceMthodDescription(joinPoint);
            operationLog.setOperModel(logAnnotation.module());
            operationLog.setOperType(logAnnotation.type());
            operationLog.setDataJson(logAnnotation.dataJson());
            operationLog.setInputParam(logAnnotation.inputParam());
            if(operationLog.getOperModel()!=0 && StringUtils.isNotEmpty(logAnnotation.dataJson())){
                operationLogMapper.insertSelective(operationLog);
            }
        } catch (Exception e){
            LOG.error("add operation log failed...",e);
        }

    }

    /**
     * 获取注解中的属性
     * @param joinPoint
     * @return
     * @throws Exception
     */
    private static LogAnnotation getServiceMthodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    LogAnnotation operationLog = method.getAnnotation(LogAnnotation.class);
                    return operationLog;
                }
            }
        }
        return null;

    }
    
}