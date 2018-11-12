package com.aotain.zongfen.mapper.analysis;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.analysis.IllegalRouteListDto;
import com.aotain.zongfen.dto.common.IllegalRouteParamVo;

@MyBatisDao
public interface IllegalRouteMapper {

	public List<IllegalRouteListDto> getDataList(IllegalRouteParamVo params);
  
	public long getMaxSize(IllegalRouteParamVo params);
	
	public List<String> getCarrieroperators();
	
	public List<IllegalRouteListDto> getAllDataList(IllegalRouteParamVo params);
	
	public long getAllDataMaxSize(IllegalRouteParamVo params);
}