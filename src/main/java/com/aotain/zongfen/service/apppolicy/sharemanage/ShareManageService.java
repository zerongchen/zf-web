package com.aotain.zongfen.service.apppolicy.sharemanage;

import com.aotain.common.policyapi.model.ShareManageStrategy;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.PageResult;

public interface ShareManageService {

	public PageResult<ShareManageStrategy> getList(Integer pageSize,Integer pageIndex,String policyName,String startTime,String endTime);
	
	public ResponseResult<BaseKeys> save(ShareManageStrategy share);
	
	public ResponseResult<BaseKeys> update(ShareManageStrategy share);
	
	/**
	 * 
	* @Title: delete
	* @Description:删除
	* @param @param policys
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> delete(Integer[] policys);
	
	/**
	 * 
	* @Title: policyResend
	* @Description: 策略重发 
	* @param @param shareId
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> policyResend(Integer[] shareId);
	
}
