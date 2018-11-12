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
import com.aotain.common.policyapi.model.ShareManageStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.apppolicy.sharemanage.ShareManageService;
import com.aotain.zongfen.utils.PageResult;

/**
 * 
 * ClassName: 1拖N用户管理
 * Description: TODO
 * date: 2018年1月17日 上午10:38:03
 * 
 * @author TZJ
 * @version  
 * @since JDK 1.8
 */
@RequestMapping("/apppolicy/sharemanage")
@Controller
public class ShareManageController {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ShareManageController.class);
	
	@Autowired
	private ShareManageService shareManageservice;
	
	@RequestMapping("index")
	public String getIndex() {
		return "/apppolicy/sharemanage/index";
	}
	
	@RequiresPermission("zf102007_query")
	@RequestMapping("getList")
	@ResponseBody
	public PageResult<ShareManageStrategy> getList(@RequestParam(required = true, name = "pageSize") Integer pageSize,
												 @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
												 @RequestParam(required = true, name = "policyName") String policyName,
												 String startTime,String endTime){
		return shareManageservice.getList(pageSize, pageIndex, policyName,startTime,endTime);
		
  	}
	
	@RequiresPermission("zf102007_add")
	@RequestMapping("save")
	@ResponseBody
	@LogAnnotation(module = 102007,type = OperationConstants.OPERATION_SAVE)
	public ResponseResult<BaseKeys> save(@RequestBody ShareManageStrategy share) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = shareManageservice.save(share);
			if(result.getResult().equals(1)) {
				String dataJson = "shareId="+result.getKeys().get(0).getId();
	            ProxyUtil.changeVariable(ShareManageController.class,"save",dataJson);
			}
		} catch (Exception e) {
			logger.error("save fail",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102007_modify")
	@RequestMapping("update")
	@ResponseBody
	@LogAnnotation(module = 102007,type = OperationConstants.OPERATION_UPDATE)
	public ResponseResult<BaseKeys> update(@RequestBody ShareManageStrategy share) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = shareManageservice.update(share);
			if(result.getResult().equals(1)) {
				String dataJson = "shareId="+share.getShareId();
	            ProxyUtil.changeVariable(ShareManageController.class,"update",dataJson);
			}
		} catch (Exception e) {
			logger.error("update fail",e);
			result.setResult(0);
			result.setMessage("保存失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102007_delete")
	@RequestMapping("delete")
	@ResponseBody
	@LogAnnotation(module = 102007,type = OperationConstants.OPERATION_DELETE)
	public ResponseResult<BaseKeys> deleteShare(@RequestParam("policyId[]")Integer[] policys) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = shareManageservice.delete(policys);
			String dataJson = "shareId="+JSONArray.toJSONString(policys);
            ProxyUtil.changeVariable(ShareManageController.class,"deleteShare",dataJson);
		} catch (Exception e) {
			logger.error("delete fail",e);
			result.setResult(0);
			result.setMessage("删除失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
	@RequiresPermission("zf102007_redo")
	@RequestMapping("policyResend")
	@ResponseBody
	@LogAnnotation(module = 102007,type = OperationConstants.OPERATION_RESEND)
	public ResponseResult<BaseKeys> policyResend(@RequestParam("shareId[]") Integer[] shareId) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		try {
			result = shareManageservice.policyResend(shareId);
			String dataJson = "messageNo="+result.getKeys().get(0).getMessageNo()+";bindMessageNo="+JSONArray.toJSONString(result.getBindMessageNo());
            ProxyUtil.changeVariable(ShareManageController.class,"policyResend",dataJson);
		} catch (Exception e) {
			logger.error("resend fail",e);
			result.setResult(0);
			result.setMessage("重发失败,请刷新页面！");
		}
		result.setKeys(null);
		return result;
	}
	
}
