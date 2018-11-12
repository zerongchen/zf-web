package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.FlowSignStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.flowmark.IFlowMarkService;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * 流量标记策略
 *
 * @author daiyh@aotain.com
 * @date 2018/04/03
 */
@RequestMapping("/apppolicy/flowmark")
@Controller
public class FlowMarkController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FlowMarkController.class);

    @Autowired
    private IFlowMarkService flowMarkService;

    @RequestMapping("/index")
    public String getIndex() {
        return "/apppolicy/flowmark/index";
    }

    @RequiresPermission("zf102005_query")
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

            List<FlowSignStrategy> flowSignStrategies = flowMarkService.listData(queryMap);
            PageInfo pageInfo = new PageInfo(flowSignStrategies);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e){
            MonitorStatisticsUtils.addEvent(e);
            logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            throw new Exception("应用异常",e);
        }

    }

    @RequiresPermission("zf102005_add")
    @RequestMapping(value = {"/save"})
    @ResponseBody
    @LogAnnotation(module = 102005,type = OperationConstants.OPERATION_SAVE)
    public ResponseResult addData(FlowSignStrategy flowSignStrategy,
                                  @RequestParam(value="names[]",required=false)String[] userNames,
                                  @RequestParam(value="groupIds[]",required=false)Long[] userGroupIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            flowSignStrategy.setCreateOper(createOper);
            flowSignStrategy.setModifyOper(createOper);
            flowSignStrategy.setCreateTime(new Date());
            flowSignStrategy.setModifyTime(new Date());

            if (flowSignStrategy.getUserType()==1||flowSignStrategy.getUserType()==2){
                flowSignStrategy.setUserName(Arrays.asList(userNames));
            } else if (flowSignStrategy.getUserType()==3){
                flowSignStrategy.setUserGroupId(Arrays.asList(userGroupIds));
            }

            flowSignStrategy.setMessageType(MessageType.APP_FLOW_MARK.getId());
            if (flowMarkService.existSameNameRecord(flowSignStrategy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }

            List<Long> bindMessageNo = flowMarkService.addFlowSignAndUserBindPolicy(flowSignStrategy);

            try{
                String dataJson = "messageNo="+flowSignStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(FlowMarkController.class,"addData",dataJson,flowSignStrategy.objectToJson());
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

    @RequiresPermission("zf102005_modify")
    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 102005,type = OperationConstants.OPERATION_UPDATE)
    public ResponseResult modifyData(FlowSignStrategy flowSignStrategy,
                                     @RequestParam(value="names[]",required=false)String[] userNames,
                                     @RequestParam(value="groupIds[]",required=false)Long[] userGroupIds) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();
            flowSignStrategy.setModifyOper(createOper);
            flowSignStrategy.setModifyTime(new Date());

            if (flowSignStrategy.getUserType()==1||flowSignStrategy.getUserType()==2){
                flowSignStrategy.setUserName(Arrays.asList(userNames));
            } else if (flowSignStrategy.getUserType()==3){
                flowSignStrategy.setUserGroupId(Arrays.asList(userGroupIds));
            }


            flowSignStrategy.setMessageType(MessageType.APP_FLOW_MARK.getId());
            if (flowMarkService.existSameNameRecord(flowSignStrategy)){
                return wrapReturnDate("存在策略名称相同的记录",1);
            }
            List<Long> bindMessageNo = flowMarkService.modifyFlowSignAndUserBindPolicy(flowSignStrategy);
            try{
                String dataJson = "messageNo="+flowSignStrategy.getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(FlowMarkController.class,"modifyData",dataJson,flowSignStrategy.objectToJson());
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

    @RequiresPermission("zf102005_delete")
    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 102005,type = OperationConstants.OPERATION_DELETE)
    public ResponseResult deleteData(@RequestParam(value = "messageNos[]") Integer[] messageNos) throws Exception{
        try{
            String createOper = SpringUtil.getSysUserName();

            List<FlowSignStrategy> flowSignStrategies = new ArrayList<>();
            for(int i=0;i<messageNos.length;i++){

                FlowSignStrategy result = flowMarkService.selectByPrimaryKey(messageNos[i]);
                result.setMessageType(MessageType.APP_FLOW_MARK.getId());
                result.setModifyOper(createOper);
                result.setModifyTime(new Date());
                flowSignStrategies.add(result);
            }

            List<Long> bindMessageNo = flowMarkService.deletePolicys(flowSignStrategies);
            try{
                String dataJson = "messageNos="+Arrays.asList(messageNos)+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(FlowMarkController.class,"deleteData",dataJson);
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

    @RequiresPermission("zf102005_redo")
    @RequestMapping(value = {"/resend"})
    @ResponseBody
    @LogAnnotation(module = 102005,type = OperationConstants.OPERATION_RESEND)
    public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
                                       @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
        try{
            if (ips==null||ips.size()==0){
                ips = new ArrayList<>();
            }
            List<Long> bindMessageNo = flowMarkService.resendPolicy(0L,messageNo,true,ips);
            try{
                String dataJson = "messageNo="+messageNo+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
                ProxyUtil.changeVariable(FlowMarkController.class,"resendPolicy",dataJson);
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

}
