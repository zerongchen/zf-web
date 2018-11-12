package com.aotain.zongfen.service.general.weblibrary;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.GeneralURL;
import com.aotain.zongfen.utils.PageResult;

public interface WEBCategoryService {

	/**
	 * 
	* @Title: uploadFile
	* @Description: 导入IP地址库 
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
	public PageResult<GeneralURL> getIndexList(Integer pageSize,Integer pageIndex,String webType,String hostName);

	/**
	 * 
	* @Title: createAndSend
	* @Description: 生成并下发
	* @param @return
	* @return Map<String,String>
	* @throws
	 */
	public Map<String,String> createAndSend(Integer serverId);
	
	/**
	 * 
	* @Title: getAllUrl
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return
	* @return List<GeneralURL>
	* @throws
	 */
	public List<GeneralURL> getAllUrl();
	
}
