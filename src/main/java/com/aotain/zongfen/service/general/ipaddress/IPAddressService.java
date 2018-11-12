package com.aotain.zongfen.service.general.ipaddress;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aotain.zongfen.model.general.GenIPAddress;
import com.aotain.zongfen.utils.PageResult;

public interface IPAddressService {

	/**
	 * 
	* @Title: uploadFile 
	* @Description: 导入IP地址库 (这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param type:增量导入=add，全量导入=all
	* @param @return
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
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
	public PageResult<GenIPAddress> getIndexList(Integer pageSize,Integer pageIndex,String ipType,String ipaddress);

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
	* @Title: getAll 
	* @Description: 获取所有的IPV4地址库数据，用来导出数据(这里用一句话描述这个方法的作用) 
	* @param @return    设定文件 
	* @return List<GenIPAddress>    返回类型 
	* @throws
	 */
	public List<GenIPAddress> getIpV4();
	/**
	 * 
	* @Title: getAll 
	* @Description: 获取所有的IPV4地址库数据，用来导出数据(这里用一句话描述这个方法的作用) 
	* @param @return    设定文件 
	* @return List<GenIPAddress>    返回类型 
	* @throws
	 */
	public List<GenIPAddress> getIpV6();

	Map<String,String> queryProgress(HttpServletRequest request);

	void queryResultFile(HttpServletRequest request,HttpServletResponse response);
}
