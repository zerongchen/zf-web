package com.aotain.zongfen.controller.apppolicy;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.AppDefinedStrategy;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.appdefined.IAppDefinedService;
import com.aotain.zongfen.service.common.PolicyService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/04
 */
@Controller
@RequestMapping("/apppolicy/customfeatures")
public class AppDefinedController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AppDefinedController.class);

    @Autowired
    private IAppDefinedService appDefinedService;

    @Autowired
    private PolicyService policyService;

    @RequestMapping("index")
    public String index() {
        return "/apppolicy/customfeatures/index";
    }

    @RequiresPermission("zf102011_query")
    @RequestMapping(value = {"/list"})
    @ResponseBody
    public PageResult listData(Page page,
                               @RequestParam(value="messageName",required=false)String messageName) throws Exception{

        try{
            PageHelper.startPage(page.getPage(),page.getPageSize());

            Map<String,Object> queryMap = new HashMap<>();
            queryMap.put("messageName",messageName);
            if (page.getSearchStartTime()!=null){
                queryMap.put("startTime",page.getSearchStartTime());
            }
            if (page.getSearchEndTime()!=null){
                page.setSearchEndTime(DateUtils.addDateOfMonth(page.getSearchEndTime(),1));
                queryMap.put("endTime",page.getSearchEndTime());
            }

            List<AppDefinedStrategy> appDefinedStrategyList = appDefinedService.listData(queryMap);
            PageInfo pageInfo = new PageInfo(appDefinedStrategyList);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }


    }

    @RequiresPermission("zf102011_add")
    @RequestMapping(value = {"/save"})
    @ResponseBody
    @LogAnnotation(module = 102011,type = OperationConstants.OPERATION_SAVE)
    public ResponseResult addPolicy(@RequestBody AppDefinedStrategy appDefinedStrategy) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            appDefinedStrategy.setCreateOper(createOper);
            appDefinedStrategy.setModifyOper(createOper);
            appDefinedStrategy.setCreateTime(new Date());
            appDefinedStrategy.setModifyTime(new Date());

            Policy policy = new Policy();
            policy.setMessageType(MessageType.APP_DEFINED.getId());
            policy.setMessageName(appDefinedStrategy.getMessageName());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }

            List<Long> bindMessageNo = appDefinedService.addAppDefinedAndUserBindPolicy(appDefinedStrategy);

            try{
                String dataJson = "messageNo="+appDefinedStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppDefinedController.class,"addPolicy",dataJson,appDefinedStrategy.objectToJson());
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

    @RequiresPermission("zf102011_modify")
    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 102011,type = OperationConstants.OPERATION_UPDATE)
    public ResponseResult modifyData(@RequestBody AppDefinedStrategy appDefinedStrategy) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            appDefinedStrategy.setModifyOper(createOper);
            appDefinedStrategy.setModifyTime(new Date());


            appDefinedStrategy.setMessageType(MessageType.APP_DEFINED.getId());
            Policy policy = new Policy();
            policy.setMessageType(MessageType.APP_DEFINED.getId());
            if (appDefinedStrategy.getMessageNo()!=null){
                policy.setMessageNo(appDefinedStrategy.getMessageNo());
            }
            policy.setMessageName(appDefinedStrategy.getMessageName());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }

            List<Long> bindMessageNo = appDefinedService.modifyAppDefinedAndUserBindPolicy(appDefinedStrategy);

            try{
                String dataJson = "messageNo="+appDefinedStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppDefinedController.class,"modifyData",dataJson,appDefinedStrategy.objectToJson());
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

    @RequiresPermission("zf102011_delete")
    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 102011,type = OperationConstants.OPERATION_DELETE)
    public ResponseResult deleteData(@RequestParam(value = "definedIds[]") Integer[] definedIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();

            List<AppDefinedStrategy> flowMirrorStrategies = new ArrayList<>();
            List<Long> messageNos = new ArrayList<Long>();
            for(int i=0;i<definedIds.length;i++){

                AppDefinedStrategy result = appDefinedService.selectByPrimaryKey(definedIds[i]);
                result.setMessageType(MessageType.APP_DEFINED.getId());
                result.setModifyOper(createOper);
                result.setModifyTime(new Date());
                flowMirrorStrategies.add(result);
                messageNos.add(result.getMessageNo());
            }

            List<Long> bindMessageNo = appDefinedService.deletePolicys(flowMirrorStrategies);

            try{
                String dataJson = "messageNo="+JSONArray.toJSONString(messageNos)+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppDefinedController.class,"deleteData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate("删除成功",0);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102011_redo")
    @RequestMapping(value = {"/resend"})
    @ResponseBody
    @LogAnnotation(module = 102011,type = OperationConstants.OPERATION_RESEND)
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
                                       @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            List<Long> bindMessageNo = appDefinedService.resendPolicy(0L,messageNo,true,ips);

            try{
                String dataJson = "messageNo="+messageNo+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppDefinedController.class,"resendPolicy",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }

            return wrapReturnDate("重发策略成功",0);
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

}
