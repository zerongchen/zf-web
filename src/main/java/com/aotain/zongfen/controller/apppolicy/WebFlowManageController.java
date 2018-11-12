package com.aotain.zongfen.controller.apppolicy;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.apppolicy.WebFlowManage;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.webflowmanage.WebFlowManageService;
import com.aotain.zongfen.utils.PageResult;

/**
 * 
 * ClassName: web流量管理
 * Description: TODO
 * date: 2018年1月17日 上午10:38:03
 * 
 * @author TZJ
 * @version  
 * @since JDK 1.8
 */
@RequestMapping("/webflow")
@Controller
public class WebFlowManageController {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ShareManageController.class);
	
	@Autowired
	private WebFlowManageService webFlowManageservice;
	
	@RequestMapping("index")
	public String getIndex() {
		return "/apppolicy/webflowmanage/index";
	}
	
	@RequiresPermission("zf102003_query")
	@RequestMapping("getWebFlowData")
	@ResponseBody
	public PageResult<WebFlowManage> getWebFlowData(@RequestParam(required = true, name = "pageSize") Integer pageSize,
												 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
												 @RequestParam(required = true, name = "policyName") String policyName,
												 String startTime,String endTime){
		return webFlowManageservice.getWebFlowData(pageSize, pageIndex, policyName,startTime,endTime);
		
  	}
	
	@RequiresPermission("zf102003_add")
	@RequestMapping("webflowsave")
	@ResponseBody
	@LogAnnotation(module = 102003,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<BaseKeys> webflowSave(@RequestBody WebFlowManage webFlow) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = webFlowManageservice.webFlowSave(webFlow);
			if(result.getResult().equals(1)) {
				String dataJson = "webflowId="+result.getKeys().get(0).getId();
	            ProxyUtil.changeVariable(WebFlowManageController.class,"webflowSave",dataJson);
			}
		} catch (Exception e) {
			logger.error("save fail:",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102003_modify")
	@RequestMapping("webflowupdate")
	@ResponseBody
	@LogAnnotation(module = 102003,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<BaseKeys> webflowUpdate(@RequestBody WebFlowManage webFlow) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = webFlowManageservice.webFlowSave(webFlow);
			if(result.getResult().equals(1)) {
				String dataJson = "webflowId="+result.getKeys().get(0).getId();
	            ProxyUtil.changeVariable(WebFlowManageController.class,"webflowUpdate",dataJson);
			}
		} catch (Exception e) {
			logger.error("save fail",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102003_delete")
	@RequestMapping("deleteWebFlow")
	@ResponseBody
	@LogAnnotation(module = 102003,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult<BaseKeys> deleteWebFlow(@RequestParam("policyId[]")Integer[] policys) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = webFlowManageservice.deleteWebFlow(policys);
			String dataJson = "webflowId="+JSONArray.toJSONString(policys);
            ProxyUtil.changeVariable(WebFlowManageController.class,"deleteWebFlow",dataJson);
		} catch (Exception e) {
			logger.error("delete fail",e);
			result.setResult(0);
			result.setMessage("删除失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}

	@RequiresPermission("zf102003_redo")
	@RequestMapping("policyResend")
	@ResponseBody
	@LogAnnotation(module = 102003,type = OperationConstants.OPERATION_RESEND)
	public ResponseResult<BaseKeys> policyResend(@RequestParam("webFlowId[]") Integer[] webFlowId) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = webFlowManageservice.policyResend(webFlowId);
			String dataJson = "messageNo="+result.getKeys().get(0).getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(result.getBindMessageNo());
            ProxyUtil.changeVariable(WebFlowManageController.class,"policyResend",dataJson);
		} catch (Exception e) {
			logger.error("resend fail",e);
			result.setResult(0);
			result.setMessage("重发失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
}
