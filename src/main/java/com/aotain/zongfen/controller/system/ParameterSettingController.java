package com.aotain.zongfen.controller.system;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.system.SystemParameter;
import com.aotain.zongfen.service.system.IParameterSettingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * 系统参数设置Controller
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */

@Controller
@RequestMapping(value = "/system/parameter")
public class ParameterSettingController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ParameterSettingController.class);

    @Autowired
    private IParameterSettingService parameterSettingService;

    @RequestMapping(value = {"/index"})
    public String home(ModelMap modelMap) throws Exception{
        return "/system/parameterSettings/index";
    }

    @RequestMapping(value = {"/list"})
    @ResponseBody
    @RequiresPermission(value="zf501002_query")
    public com.aotain.common.config.pagehelper.PageResult listParameter(@Param("page") Page page, SystemParameter systemParameter) throws Exception{
        try{
            PageHelper.startPage(page.getPage(),page.getPageSize());
            List<SystemParameter> systemParameters = parameterSettingService.listSystemParameter(systemParameter);
            PageInfo pageInfo = new PageInfo(systemParameters);
            return new com.aotain.common.config.pagehelper.PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/add"})
    @ResponseBody
    @LogAnnotation(module = 501002,type = OperationConstants.OPERATION_SAVE)
    @RequiresPermission(value="zf501002_add")
    public ResponseResult addData(SystemParameter systemParameter) throws Exception{
        try{
            ResponseResult responseResult =new ResponseResult();
            boolean exist = parameterSettingService.existRecord(systemParameter);
            if (exist){
                responseResult.setResult(1);
                responseResult.setMessage("存在key值相同的记录,操作失败！");
                return responseResult;
            }

            boolean existSameNameRecord = parameterSettingService.existSameNameRecord(systemParameter);
            if (existSameNameRecord) {
                responseResult.setResult(1);
                responseResult.setMessage("存在模块和描述均相同的记录,操作失败！");
            } else {

                setCommonPropertiesBeforeInsertOrUpdate(systemParameter,false);
                parameterSettingService.addOrUpdate(systemParameter);
                responseResult.setResult(0);
                responseResult.setMessage("操作成功！");
            }

            try{
                String dataJson = "configKey="+systemParameter.getConfigKey();
                ProxyUtil.changeVariable(ParameterSettingController.class,"addData",dataJson,systemParameter.toString());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return responseResult;
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 501002,type = OperationConstants.OPERATION_UPDATE)
    @RequiresPermission(value="zf501002_modify")
    public ResponseResult updateData(SystemParameter systemParameter) throws Exception{
        try{
            ResponseResult responseResult =new ResponseResult();
            boolean existSameNameRecord = parameterSettingService.existSameNameRecord(systemParameter);
            if (existSameNameRecord) {
                responseResult.setResult(1);
                responseResult.setMessage("存在模块和描述均相同的记录,操作失败！");
            } else {

                setCommonPropertiesBeforeInsertOrUpdate(systemParameter,true);
                parameterSettingService.addOrUpdate(systemParameter);
                responseResult.setResult(0);
                responseResult.setMessage("操作成功！");
            }
            try{
                SystemParameter result = parameterSettingService.selectByConfigKey(systemParameter.getConfigKey());
//            long configId = systemParameter.getConfigId()==null?result.getConfigId():systemParameter.getConfigId();
                String dataJson = "configKey="+systemParameter.getConfigKey();
                ProxyUtil.changeVariable(ParameterSettingController.class,"updateData",dataJson,systemParameter.toString());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return responseResult;
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/set"})
    @ResponseBody
    @LogAnnotation(module = 501002,type = OperationConstants.OPERATION_SET)
    @RequiresPermission(value="zf501002_set")
    public ResponseResult setData(SystemParameter systemParameter) throws Exception{
        try{
            ResponseResult responseResult =new ResponseResult();
            boolean existSameNameRecord = parameterSettingService.existSameNameRecord(systemParameter);
            if (existSameNameRecord) {
                responseResult.setResult(1);
                responseResult.setMessage("存在模块和描述均相同的记录,操作失败！");
            } else {

                setCommonPropertiesBeforeInsertOrUpdate(systemParameter,true);
                parameterSettingService.addOrUpdate(systemParameter);
                responseResult.setResult(0);
                responseResult.setMessage("操作成功！");
            }
            try{
                String dataJson = "configKey="+systemParameter.getConfigKey();
                ProxyUtil.changeVariable(ParameterSettingController.class,"setData",dataJson,systemParameter.toString());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return responseResult;
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 501002,type = OperationConstants.OPERATION_DELETE)
    @RequiresPermission(value="zf501002_delete")
    public void deleteData(@RequestParam(value = "configIds[]") Integer[] configIds,
                           @RequestParam(value = "configKeys[]") String[] configKeys) throws Exception{
        try{
            List<Integer> configList = Arrays.asList(configIds);
            parameterSettingService.batchDelete(configList);
            try{
                Object data = null;
                if (configKeys.length==1){
                    data = configKeys[0];
                } else {
                    data = Arrays.asList(configKeys);
                }
                String dataJson = "configKey="+data;
                ProxyUtil.changeVariable(ParameterSettingController.class,"deleteData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }
}
