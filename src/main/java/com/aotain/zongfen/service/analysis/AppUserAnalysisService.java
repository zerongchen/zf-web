package com.aotain.zongfen.service.analysis;

import java.util.List;

import com.aotain.zongfen.dto.analysis.AppUserDto;
import com.aotain.zongfen.dto.common.AppUserParamVo;
import com.aotain.zongfen.utils.PageResult;

public interface AppUserAnalysisService {

	public PageResult<AppUserDto> getIndexListData(AppUserParamVo params);
	
	public PageResult<AppUserDto> getDetailListData(AppUserParamVo params);
	
	public List<AppUserDto> getExportData(AppUserParamVo params);
}
