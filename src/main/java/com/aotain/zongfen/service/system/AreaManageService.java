package com.aotain.zongfen.service.system;

import java.util.List;
import java.util.Map;

import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.PageResult;

public interface AreaManageService {

	/**
	 * 
	* @Title: getAreaManageData
	* @Description: 获取区域列表数据
	* @param @param pageSize
	* @param @param pageIndex
	* @param @param searchName
	* @param @return
	* @return PageResult<ChinaArea>
	* @throws
	 */
	public PageResult<ChinaArea> getAreaManageData(Integer pageSize, Integer pageIndex, String searchName);
	
	public ResponseResult<BaseKeys> areaSave(List<ChinaArea>areaList);
	
	public ResponseResult<BaseKeys> areaUpdate(ChinaArea area);
	
	public String haveSetProvince();
	
	public String delete(List<Integer> ids);
	
	public Map<String,Object> getProvinceList();
	
	public ResponseResult<BaseKeys> setProvince(String province,String areaName,String areaShort);
}
