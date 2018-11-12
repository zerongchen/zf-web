package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.utils.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.AppUserStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.appuser.impl.AppUserServiceImpl;
import com.aotain.zongfen.service.common.PolicyServiceImpl;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/05
 */
@Controller
@RequestMapping("/apppolicy/appuser")
public class AppUserController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    @Autowired
    private AppUserServiceImpl appUserService;

    @Autowired
    private PolicyServiceImpl policyService;

    @RequestMapping("index")
    public String index() {
        return "/apppolicy/appuser/index";
    }

    @RequiresPermission("zf102006_query")
    @RequestMapping("list")
    @ResponseBody
    public PageResult listData(Page<AppUserStrategy> page,
                               @Param("messageName")String messageName)  throws Exception{
        try{
            Map<String,Object> queryMap = Maps.newHashMap();
            queryMap.put("messageName",messageName);
            if (page.getSearchStartTime()!=null){
                queryMap.put("searchStartTime",page.getSearchStartTime());
            }
            if (page.getSearchEndTime()!=null){
                page.setSearchEndTime(DateUtils.addDateOfMonth(page.getSearchEndTime(),1));
                queryMap.put("searchEndTime",page.getSearchEndTime());
            }
            PageHelper.startPage(page.getPage(),page.getPageSize());
            List<AppUserStrategy> appUserStrategyList =  appUserService.listData(queryMap);
            PageInfo pageInfo = new PageInfo(appUserStrategyList);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102006_delete")
    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 102006,type = OperationConstants.OPERATION_DELETE)
    public ResponseResult deleteData(@RequestParam(value = "messageNos[]") Long[] messageNos) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            List<AppUserStrategy> appUserStrategies = new ArrayList<>();

            for(int i=0;i<messageNos.length;i++){
                AppUserStrategy appUserStrategy = new AppUserStrategy();
                appUserStrategy.setMessageNo(messageNos[i]);
                AppUserStrategy result = appUserService.selectByPrimaryKey(appUserStrategy);

                result.setModifyOper(createOper);
                result.setModifyTime(new Date());

                if (StringUtils.isEmpty(result.getAppName())){
                    result.setAppName(" ");
                }

                appUserStrategies.add(result);
            }

            appUserService.deletePolicys(appUserStrategies);

            try{
                String dataJson = "messageNos="+Arrays.asList(messageNos);
                ProxyUtil.changeVariable(AppUserController.class,"deleteData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate();
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102006_add")
    @RequestMapping(value = {"/save"})
    @ResponseBody
    @LogAnnotation(module = 102006,type = OperationConstants.OPERATION_SAVE)
    public ResponseResult addPolicy(AppUserStrategy appUserStrategy,
                                    @RequestParam(value="names[]",required=false)String[] userNames,
                                    @RequestParam(value="groupIds[]",required=false)Long[] userGroupIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            appUserStrategy.setCreateOper(createOper);
            appUserStrategy.setModifyOper(createOper);
            appUserStrategy.setCreateTime(new Date());
            appUserStrategy.setModifyTime(new Date());

            if (StringUtils.isEmpty(appUserStrategy.getAppName())){
                appUserStrategy.setAppName(" ");
            }


            Policy policy = new Policy();
            policy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER);
            policy.setMessageName(appUserStrategy.getMessageName());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate(1);
            }

            appUserService.addAppUserPolicyAndUserBindPolicy(appUserStrategy);

            try{
                String dataJson = "messageNo="+appUserStrategy.getMessageNo();
                ProxyUtil.changeVariable(AppUserController.class,"addPolicy",dataJson,appUserStrategy.objectToJson());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate("新增策略成功",0);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102006_modify")
    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 102006,type = OperationConstants.OPERATION_UPDATE)
    public ResponseResult modifyPolicy(AppUserStrategy appUserStrategy,
                                       @RequestParam(value="names[]",required=false)String[] names,
                                       @RequestParam(value="groupIds[]",required=false)Long[] groupIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            appUserStrategy.setModifyOper(createOper);
            appUserStrategy.setModifyTime(new Date());

            if (StringUtils.isEmpty(appUserStrategy.getAppName())){
                appUserStrategy.setAppName(" ");
            }

            Policy policy = new Policy();
            policy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER);
            policy.setMessageName(appUserStrategy.getMessageName());
            policy.setMessageNo(appUserStrategy.getMessageNo());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate(1);
            }

            appUserService.modifyAppUserPolicyAndUserBindPolicy(appUserStrategy);

            try{
                String dataJson = "messageNo="+appUserStrategy.getMessageNo();
                ProxyUtil.changeVariable(AppUserController.class,"modifyPolicy",dataJson,appUserStrategy.objectToJson());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate("修改策略成功",0);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102006_redo")
    @RequestMapping(value = {"/resend"})
    @ResponseBody
    @LogAnnotation(module = 102006,type = OperationConstants.OPERATION_RESEND)
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
                                       @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            appUserService.resendPolicy(0L,messageNo,true,ips);

            try{
                String dataJson = "messageNo="+messageNo;
                ProxyUtil.changeVariable(AppUserController.class,"resendPolicy",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate();
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequestMapping(value = {"/resendSpecificDpi"})
    @ResponseBody
    public ResponseResult resendSpecificDpi(@RequestParam(value = "messageNo") Long messageNo,
                                            @RequestParam(value = "ips[]") List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            appUserService.resendPolicy(0L,messageNo,false,ips);
            return wrapReturnDate();
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

}
