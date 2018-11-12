package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.config.pagehelper.TimeScope;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.VoipFlowStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.voipflowmanage.IVoipFlowManageService;
import com.aotain.zongfen.utils.SpringUtil;


/**
 * 
 * ClassName: Voip流量管理
 * date: 2018年3月15日 上午10:26:56
 * 
 * @author daiyh
 * @version  
 * @since JDK 1.8
 */
@RequestMapping("/apppolicy/voipflow")
@Controller
public class VoipFlowManageController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(VoipFlowManageController.class);

	@Autowired
	private IVoipFlowManageService voipFlowManageService;

	/**
	 * 
	* @Title: getIndex
	* @Description: 首页跳转
	* @param 
	* @return String
	* @throws
	 */
	@RequestMapping("index")
	public String getIndex() {
		return "/apppolicy/voipflowmanage/index";
	}

	@RequiresPermission("zf102004_query")
    @RequestMapping(value = {"/list"})
    @ResponseBody
    public PageResult listData(Page<VoipFlowStrategy> page,
                               String messageName,
                               TimeScope timeScope) throws Exception{
		try{
			if ( !StringUtils.isEmpty(messageName) ){
				page.getFuzzyParams().put("a.message_name",messageName);
			}
			if ( !StringUtils.isEmpty(timeScope.getStartTime()) || !StringUtils.isEmpty(timeScope.getEndTime())){
				page.getTimeScopeParams().put("a.create_time",timeScope);
			}
			page.getNotEqualOperateParams().put("a.OPERATE_TYPE",3);
			page.getOrderByParams().put("a.MESSAGE_NO","DESC");
			List<VoipFlowStrategy> voipFlowStrategyList =  voipFlowManageService.listData(page);
			return wrapResultData(page,voipFlowStrategyList);
		} catch (Exception e){
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			throw new Exception("应用异常",e);
		}

    }


	@RequiresPermission("zf102004_add")
    @RequestMapping(value = {"/add"})
	@ResponseBody
	@LogAnnotation(module = 102004,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult addData(VoipFlowStrategy voipFlowStrategy) throws Exception{
		try{
			String createOper = SpringUtil.getSysUserName();
			voipFlowStrategy.setCreateOper(createOper);
			voipFlowStrategy.setModifyOper(createOper);
			voipFlowStrategy.setCreateTime(new Date());
			voipFlowStrategy.setModifyTime(new Date());
			if (voipFlowManageService.existSameNameRecord(voipFlowStrategy)){
				return wrapReturnDate("存在策略名称相同的记录",1);
			}
			voipFlowManageService.addVoipFlowManageAndUserBindPolicy(voipFlowStrategy);

			try{
				String dataJson = "voipflowId="+voipFlowStrategy.getVoipFlowId();
				ProxyUtil.changeVariable(VoipFlowManageController.class,"addData",dataJson,voipFlowStrategy.objectToJson());
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

	@RequiresPermission("zf102004_modify")
	@RequestMapping(value = {"/update"})
	@ResponseBody
	@LogAnnotation(module = 102004,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult modifyData(VoipFlowStrategy voipFlowStrategy) throws Exception{
		try{
			String createOper = SpringUtil.getSysUserName();
			voipFlowStrategy.setModifyOper(createOper);
			voipFlowStrategy.setModifyTime(new Date());
			if (voipFlowManageService.existSameNameRecord(voipFlowStrategy)){
				return wrapReturnDate("存在策略名称相同的记录",1);
			}
			voipFlowManageService.modifyVoipFlowManageAndUserBindPolicy(voipFlowStrategy);

			try{
				String dataJson = "voipflowId="+voipFlowStrategy.getVoipFlowId();
				ProxyUtil.changeVariable(VoipFlowManageController.class,"modifyData",dataJson,voipFlowStrategy.objectToJson());
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

	@RequiresPermission("zf102004_delete")
	@RequestMapping(value = {"/delete"})
	@ResponseBody
	@LogAnnotation(module = 102004,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult deleteData(@RequestParam(value = "voipFlowIds[]") Integer[] voipFlowIds) throws Exception{
		try{
			String createOper = SpringUtil.getSysUserName();
			List<VoipFlowStrategy> voipFlowStrategies = new ArrayList<>();

			for(int i=0;i<voipFlowIds.length;i++){
				VoipFlowStrategy voipFlowStrategy = new VoipFlowStrategy();
				voipFlowStrategy.setVoipFlowId(voipFlowIds[i]);
				VoipFlowStrategy result = voipFlowManageService.selectByPrimaryKey(voipFlowStrategy);

				result.setModifyOper(createOper);
				result.setModifyTime(new Date());

				voipFlowStrategies.add(result);
			}

			voipFlowManageService.deletePolicys(voipFlowStrategies);
			try{
				String dataJson = "voipflowId="+ Arrays.asList(voipFlowIds).toString();
				ProxyUtil.changeVariable(VoipFlowManageController.class,"deleteData",dataJson);
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

	@RequiresPermission("zf102004_redo")
	@RequestMapping(value = {"/resend"})
	@ResponseBody
	@LogAnnotation(module = 102004,type = OperationConstants.OPERATION_RESEND)
	public ResponseResult resendPolicy(@RequestParam(value = "messageNo") Long messageNo,
									   @RequestParam(value = "ips[]",required=false) List<String> ips) throws Exception{
		try{
			if (ips==null||ips.size()==0){
				ips = new ArrayList<>();
			}
			List<Long> bindMessageNo = voipFlowManageService.resendPolicy(0L,messageNo,true,ips);

			try{
				String dataJson = "messageNo="+ messageNo+";bindMessageNo="+JSONArray.toJSONString(bindMessageNo);
				ProxyUtil.changeVariable(VoipFlowManageController.class,"resendPolicy",dataJson);
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
			voipFlowManageService.resendPolicy(0L,messageNo,false,ips);
			return wrapReturnDate();
		} catch (Exception e){
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			throw new Exception("应用异常",e);
		}

	}

}
