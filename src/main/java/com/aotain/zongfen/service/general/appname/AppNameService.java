package com.aotain.zongfen.service.general.appname;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.model.general.GeneralApp;
import com.aotain.zongfen.utils.PageResult;

public interface AppNameService {

	/**
	 * 
	* @Title: uploadFile
	* @Description: 导入应用名称
	* @param @param request
	* @param @return
	* @param @throws ImportException
	* @param @throws IOException
	* @return String
	* @throws
	 */
	public Map<String,String> uploadFile(HttpServletRequest request) throws Exception;
	
	/**
	 * 
	* @Title: getIndexList
	* @Description: 查询首页列表 
	* @param @param pageSize
	* @param @param pageIndex
	* @param @param ipType
	* @param @param ipaddress
	* @param @return
	* @return PageResult<GenIPAddress>
	* @throws
	 */
	public PageResult<GeneralApp> getIndexList(Integer pageSize,Integer pageIndex,String appType,String appName);

	/**
	 * 
	* @Title: createAndSend
	* @Description: 生成并下发
	* @param @return
	* @return Map<String,String>
	* @throws
	 */
	public Map<String,String> createAndSend(Integer serverId);
	
	public List<GeneralApp> getAllAppName();
}
