package com.aotain.zongfen.controller.apppolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.ApplicationFlowStrategy;
import com.aotain.common.policyapi.model.IpAddressArea;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.service.apppolicy.flowdirection.AppFlowDirectionStrategyService;
import com.aotain.zongfen.service.common.PolicyService;
import com.aotain.zongfen.service.general.ipaddress.IpAddressAreaService;
import com.aotain.zongfen.utils.PageResult;
/**
 * 
* @ClassName: AppFlowDirectionController 
* @Description: 应用流量流向策略(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年3月8日 下午6:17:37 
*
 */
@Controller
@RequestMapping("/flowDirection")
public class AppFlowDirectionController {
	
	private static Logger logger = LoggerFactory.getLogger(AppFlowDirectionController.class);
	
	@Autowired
	private AppFlowDirectionStrategyService appFlowDirectionStrategyService;
	
	@Autowired
	private IpAddressAreaService ipAddressAreaService;
	
	@Autowired
	private PolicyService policyService;
	
	@RequestMapping(value ="/index")
    public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/apppolicy/flowDirection/index");
		return mav;
    }
	
	@RequestMapping(value = "/obtainTree")
    @ResponseBody
	public List<IpAddressArea> obtainTree(Byte areaType) throws Exception{
		try{
			//areaType = 2 (城域网)，areaType = 3 (IDC)
			return ipAddressAreaService.getByType(areaType);
		} catch (Exception e){
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			throw new Exception("应用异常",e);
		}

	}
	
	@RequiresPermission("zf102012_query")
	@RequestMapping(value="/list")
	@ResponseBody
	public PageResult<ApplicationFlowStrategy> list(Policy policy,String messageName,
			@RequestParam(required = true) Integer pageIndex,
			@RequestParam(required = true) Integer pageSize) throws Exception{
		try{
			return appFlowDirectionStrategyService.getList(pageIndex, pageSize, messageName,policy);
		} catch (Exception e){
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			throw new Exception("应用异常",e);
		}

	}
	/**
	 * 更新和修改的方法拆开
	 * @param record
	 * @return
	 */
	@RequiresPermission("zf102012_add")
	@RequestMapping(value = "/save")
    @ResponseBody
	public ResponseResult<ApplicationFlowStrategy> save(ApplicationFlowStrategy record){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		try {
			if(record == null ) {
				result.setMessage("修改失败！");
				result.setResult(1);
				return result;
			}

			try{
				String messageName = record.getMessageName();
				Policy policy = new Policy();
				if (record.getMessageNo()!=null){
					policy.setMessageNo(record.getMessageNo());
				}
				policy.setMessageName(messageName);
				policy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				if (policyService.isSamePolicyNameByType(policy)) {
					result.setMessage("策略同名！");
					result.setResult(2);
					return result;
				}
			} catch (Exception e){
				MonitorStatisticsUtils.addEvent(e);
				logger.error("set messageNo failed...",e);
			}

			if (record.getMessageNo() == null) {				
				init(record);
				result = add(record);
			} else {
				init(record);
				result = modify(record);
			}

		}catch(Exception e) {
			result.setMessage("保存失败！");
			result.setResult(1);
			MonitorStatisticsUtils.addEvent(e);
			logger.error("save fail ! ", e);
		}
		return result;
	}
	
	@RequiresPermission("zf102012_modify")
	@RequestMapping(value = "/update")
    @ResponseBody
	public ResponseResult<ApplicationFlowStrategy> update(ApplicationFlowStrategy record){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		try {
			if(record == null ) {
				result.setMessage("修改失败！");
				result.setResult(1);
				return result;
			}

			try{
				String messageName = record.getMessageName();
				Policy policy = new Policy();
				if (record.getMessageNo()!=null){
					policy.setMessageNo(record.getMessageNo());
				}
				policy.setMessageName(messageName);
				policy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				if (policyService.isSamePolicyNameByType(policy)) {
					result.setMessage("策略同名！");
					result.setResult(2);
					return result;
				}
			} catch (Exception e){
				MonitorStatisticsUtils.addEvent(e);
				logger.error("set messageNo failed...",e);
			}

			if (record.getMessageNo() == null) {				
				init(record);
				result = add(record);
			} else {
				init(record);
				result = modify(record);
			}

		}catch(Exception e) {
			result.setMessage("保存失败！");
			result.setResult(1);
			MonitorStatisticsUtils.addEvent(e);
			logger.error("save fail ! " , e);
		}
		return result;
	}
	
	/**
	 * 初始化树形
	 * @param record
	 */
	private void init(ApplicationFlowStrategy record) {
		if(null != record.getInternalAreaGroupJson() && !"[]".equals(record.getInternalAreaGroupJson())) {
			record.setInternalAreaGroupList(JSONArray.parseArray(record.getInternalAreaGroupJson(), IpAddressArea.class));
		}else {
			record.setInternalAreaGroupList(null);
		}
		if(null != record.getInternalIdcGroupJson() && !"[]".equals(record.getInternalIdcGroupJson())) {
			record.setInternalIdcGroupList(JSONArray.parseArray(record.getInternalIdcGroupJson(), IpAddressArea.class));
		}else {
			record.setInternalIdcGroupList(null);
		}
		if(null != record.getExternalAreaGroupJson() && !"[]".equals(record.getExternalAreaGroupJson())) {
			record.setExternalAreaGroupList(JSONArray.parseArray(record.getExternalAreaGroupJson(), IpAddressArea.class));
		}else {
			record.setExternalAreaGroupList(null);
		}
		if(null != record.getExternalIdcGroupJson() && !"[]".equals(record.getExternalIdcGroupJson())) {
			record.setExternalIdcGroupList(JSONArray.parseArray(record.getExternalIdcGroupJson(), IpAddressArea.class));
		}else {
			record.setExternalIdcGroupList(null);
		}
	}
	
	private ResponseResult<ApplicationFlowStrategy> add(ApplicationFlowStrategy record){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(appFlowDirectionStrategyService.addPolicy(record)) {
				result.setMessage("新增成功");
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				bk.setOperType(OperationType.CREATE.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setResult(0);
			}else {
				result.setMessage("保存失败！");
				result.setResult(1);
			}
		}catch(Exception e) {
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			result.setMessage("保存失败！");
			result.setResult(1);

		}
		return result;
	}
	
	private ResponseResult<ApplicationFlowStrategy> modify(ApplicationFlowStrategy record){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(appFlowDirectionStrategyService.modifyPolicy(record)) {
				result.setMessage("修改成功");
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				bk.setOperType(OperationType.MODIFY.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
				result.setResult(0);
			}else {
				result.setMessage("修改失败！");
				result.setResult(1);
			}
		}catch(Exception e) {
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
			result.setMessage("保存失败！");
			result.setResult(1);
			
		}
		return result;
	}
	
	@RequiresPermission("zf102012_delete")
	@RequestMapping(value = "/delete")
    @ResponseBody
	public ResponseResult<ApplicationFlowStrategy> delete(Long messageNo){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(messageNo == null || messageNo < 0 ) {
				result.setMessage("删除失败，未获取到策略号！");
				result.setResult(1);
			}else {
				ApplicationFlowStrategy record = new ApplicationFlowStrategy();
				record.setMessageNo(messageNo);
				if(appFlowDirectionStrategyService.deletePolicy(record)) {
					result.setMessage("删除成功！");
					BaseKeys bk = new BaseKeys();
					bk.setMessageNo(record.getMessageNo());
					bk.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
					bk.setOperType(OperationType.DELETE.getType());
					bk.setDataType(DataType.POLICY.getType());
					keys.add(bk);
					result.setKeys(keys);
					result.setResult(0);
				}else {
					result.setMessage("删除失败！");
					result.setResult(1);
				}
				
			}
		}catch(Exception e) {
			result.setMessage("删除失败！");
			result.setResult(1);
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
		}
		return result;
	}
	
	@RequiresPermission("zf102012_delete")
	@RequestMapping(value = "/deleteList")
    @ResponseBody
    @LogAnnotation(module = 102012,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult<ApplicationFlowStrategy> deleteList(String messageNos){
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		List<Integer> idList = new ArrayList<Integer>();
		try {
			if(messageNos == null ) {
				result.setMessage("删除失败，未获取到策略号！");
				result.setResult(2);
			}else {
				String[] ids = messageNos.split(",");
				for(String id : ids) {
					ApplicationFlowStrategy record = new ApplicationFlowStrategy();
					record.setMessageNo(Long.parseLong(id));
					appFlowDirectionStrategyService.deletePolicy(record);
					idList.add(Integer.valueOf(id));
				}
				result.setMessage("删除成功！");
				result.setResult(0);
			}
			String dataJson = "messageNo="+JSONArray.toJSONString(idList);
            ProxyUtil.changeVariable(AppFlowDirectionController.class,"deleteList",dataJson,dataJson);
		}catch(Exception e) {
			result.setMessage("删除失败！");
			result.setResult(1);
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
		}
		return result;
	}
	
	@RequiresPermission("zf102012_redo")
	@RequestMapping("resend")
    @ResponseBody
    public ResponseResult<ApplicationFlowStrategy> resendPolicy( HttpServletRequest request,ApplicationFlowStrategy record) {
		ResponseResult<ApplicationFlowStrategy> result = new ResponseResult<ApplicationFlowStrategy>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
        try {
        	if(appFlowDirectionStrategyService.resendPolicy(record)) {
        		BaseKeys bk = new BaseKeys();
				bk.setMessageNo(record.getMessageNo());
				bk.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				bk.setOperType(OperationType.RESEND.getType());
				bk.setDataType(DataType.POLICY.getType());
				keys.add(bk);
				result.setKeys(keys);
        		result.setMessage("重发成功！");
    			result.setResult(0);
        	}else {
        		result.setMessage("重发失败！");
    			result.setResult(1);
        	}
         
        }catch (Exception e){
        	result.setMessage("重发失败！");
			result.setResult(1);
			MonitorStatisticsUtils.addEvent(e);
			logger.error("ClassName="+this.getClass().getName()+",methodName="+Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            
        }
        return result;
    }
}
