package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.FlowMirrorStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.flowmirror.IFlowMirrorService;
import com.aotain.zongfen.service.common.PolicyService;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/08
 */
@Controller
@RequestMapping("/apppolicy/flowmirror")
public class AppFlowMirrorController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AppFlowMirrorController.class);

    @Autowired
    private IFlowMirrorService flowMirrorService;

    @Autowired
    private PolicyService policyService;

    @RequestMapping("/index")
    public String getIndex() {
        return "/apppolicy/flowmirror/index";
    }

    @RequiresPermission("zf102010_query")
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

            List<FlowMirrorStrategy> flowSignStrategies = flowMirrorService.listData(queryMap);
            PageInfo pageInfo = new PageInfo(flowSignStrategies);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }
    }

    @RequiresPermission("zf102010_add")
    @RequestMapping(value = {"/save"})
    @ResponseBody
    @LogAnnotation(module = 102010,type = OperationConstants.OPERATION_SAVE)
    public ResponseResult addPolicy(@RequestBody FlowMirrorStrategy flowMirrorStrategy) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            flowMirrorStrategy.setCreateOper(createOper);
            flowMirrorStrategy.setModifyOper(createOper);
            flowMirrorStrategy.setCreateTime(new Date());
            flowMirrorStrategy.setModifyTime(new Date());

            Policy policy = new Policy();
            policy.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
            policy.setMessageName(flowMirrorStrategy.getMessageName());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }

            List<Long> bindMessageNo = flowMirrorService.addFlowMirrorAndUserBindPolicy(flowMirrorStrategy);

            try{
                String dataJson = "messageNo="+flowMirrorStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppFlowMirrorController.class,"addPolicy",dataJson);
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

    @RequiresPermission("zf102010_modify")
    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 102010,type = OperationConstants.OPERATION_UPDATE)
    public ResponseResult modifyData(@RequestBody FlowMirrorStrategy flowMirrorStrategy) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            flowMirrorStrategy.setModifyOper(createOper);
            flowMirrorStrategy.setModifyTime(new Date());


            flowMirrorStrategy.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
            Policy policy = new Policy();
            policy.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
            if (flowMirrorStrategy.getMessageNo()!=null){
                policy.setMessageNo(flowMirrorStrategy.getMessageNo());
            }
            policy.setMessageName(flowMirrorStrategy.getMessageName());
            if (policyService.isSamePolicyNameByType(policy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }

            List<Long> bindMessageNo = flowMirrorService.modifyFlowMirrorAndUserBindPolicy(flowMirrorStrategy);

            try{
                String dataJson = "messageNo="+flowMirrorStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppFlowMirrorController.class,"modifyData",dataJson);
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

    @RequiresPermission("zf102010_delete")
    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 102010,type = OperationConstants.OPERATION_DELETE)
    public ResponseResult deleteData(@RequestParam(value = "policyIds[]") Integer[] policyIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();

            List<FlowMirrorStrategy> flowMirrorStrategies = new ArrayList<>();
            List<Long> messageNo = new ArrayList<Long>();
            for(int i=0;i<policyIds.length;i++){

                FlowMirrorStrategy result = flowMirrorService.selectByPrimaryKey(policyIds[i]);
                result.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
                result.setModifyOper(createOper);
                result.setModifyTime(new Date());
                flowMirrorStrategies.add(result);
                messageNo.add(result.getMessageNo());
            }

            List<Long> bindMessageNo = flowMirrorService.deletePolicys(flowMirrorStrategies);

            try{
                String dataJson = "messageNo="+JSONArray.toJSONString(messageNo)+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);;
                ProxyUtil.changeVariable(AppFlowMirrorController.class,"deleteData",dataJson);
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

    @RequiresPermission("zf102010_redo")
    @RequestMapping(value = {"/resend"})
    @ResponseBody
    @LogAnnotation(module = 102010,type = OperationConstants.OPERATION_RESEND)
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
                                       @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            List<Long> bindMessageNo = flowMirrorService.resendPolicy(0L,messageNo,true,ips);
            try{
                String dataJson = "messageNo="+messageNo+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(AppFlowMirrorController.class,"resendPolicy",dataJson);
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
