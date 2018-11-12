package com.aotain.zongfen.service.apppolicy.paramset;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aotain.common.policyapi.model.CommonCookie;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.apppolicy.CommonSearch;
import com.aotain.zongfen.model.apppolicy.CommonThreshold;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.PageResult;

public interface ParamSetService {

	public ResponseResult<BaseKeys> saveAndSync(Integer webHitThreshold,Integer kwThreholds,Integer commonId);
	
	/**
	 * 
	* @Title: getThreshold
	* @Description: 获取阈值信息
	* @param @return
	* @return CommonThreshold
	* @throws
	 */
	public CommonThreshold getThreshold();
	
	/**
	 * 
	* @Title: searchSave
	* @Description: 保存搜索引擎 
	* @param @param searchEngine
	* @param @param seId
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> searchSave(String[] searchEngine,Long seId);
	
	/**
	 * 
	* @Title: getAllSearch
	* @Description: 获取搜索引擎
	* @param @return
	* @return List<CommonSearch>
	* @throws
	 */
	public List<CommonSearch> getAllSearch();
	
	/**
	 * 
	* @Title: deleteSearch
	* @Description: 搜索引擎删除
	* @param @param ids
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> deleteSearch(String[] ids);
	
	/**
	 * @throws ImportException 
	 * 
	* @Title: StringcookieSave
	* @Description: 保存cookie
	* @param @param request
	* @param @param cookies
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> cookieSave(HttpServletRequest request,CommonCookie cookies,Integer existFile) throws ImportException;
	
	/**
	 * 
	* @Title: getCookieData
	* @Description: 获取cookie数据
	* @param @param pageSize
	* @param @param pageIndex
	* @param @return
	* @return PageResult<CommonCookie>
	* @throws
	 */
	public PageResult<CommonCookie> getCookieData(Integer pageSize, Integer pageIndex);
	
	/**
	 * 
	* @Title: deleteCookie
	* @Description: cookie删除
	* @param @param ids
	* @param @return
	* @return ResponseResult<BaseKeys>
	* @throws
	 */
	public ResponseResult<BaseKeys> deleteCookie(String[] ids);
	
	public String getAlarmType();
	
	public ResponseResult<BaseKeys> sendPolicy();
	
	public List<CommonThreshold> getPolicyDetail();
	
	public ResponseResult<BaseKeys> policyResend(long messageNo);
		
}
