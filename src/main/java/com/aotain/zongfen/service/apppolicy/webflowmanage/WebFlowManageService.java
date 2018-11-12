package com.aotain.zongfen.service.apppolicy.webflowmanage;

import com.aotain.zongfen.model.apppolicy.WebFlowManage;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.PageResult;

public interface WebFlowManageService {

	/**
	 * 
	* @Title: getWebFlowData
	* @Description: 查询列表数据
	* @param pageSize
	* @param pageIndex
	* @param policyName
	* @param status
	* @param 
	* @return PageResult<WebFlowManage>
	* @throws
	 */
	public PageResult<WebFlowManage> getWebFlowData(Integer pageSize,Integer pageIndex,String policyName,String startTime,String endTime);
	
	/**
	 * 
	* @Title: webFlowSave
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param webFlowId
	* @param policyName
	* @param controlType
	* @param advUrl
	* @param start
	* @param end
	* @param timeBar
	* @param common
	* @param webTypes
	* @param 
	* @return ResponseResult
	* @throws
	 */
	public ResponseResult<BaseKeys> webFlowSave(WebFlowManage webFlow);
	
	/**
	 * 
	* @Title: deleteWebFlow
	* @Description:删除
	* @param @param policys
	* @param @return
	* @return ResponseResult 
	* @throws
	 */
	public ResponseResult<BaseKeys> deleteWebFlow(Integer[] policys);
	
	/**
	 * 
	* @Title: policyResend
	* @Description: 策略重发 
	* @param @param webFlowId
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> policyResend(Integer[] webFlowId);
	
}
