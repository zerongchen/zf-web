package com.aotain.zongfen.service.device;

import com.aotain.zongfen.model.device.DpiStatic;
import com.aotain.zongfen.utils.PageResult;

public interface DpiStaticService {
	/**
	 * 
	* @Title: getList 
	* @Description: 分页的获取(这里用一句话描述这个方法的作用) 
	* @param @param pageSize
	* @param @param pageIndex
	* @param @param obj
	* @param @return    设定文件 
	* @return PageResult<DpiStatic>    返回类型 
	* @throws
	 */
	public PageResult<DpiStatic> getList(Integer pageIndex,Integer pageSize, DpiStatic obj);
	/**
	 * 
	* @Title: getByPK 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param deploysitename
	* @param @return    设定文件 
	* @return DpiStatic    返回类型 
	* @throws
	 */
	public DpiStatic getByPK(String deploysitename);
}
