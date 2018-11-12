package com.aotain.zongfen.service.analysis;

import java.sql.SQLException;
import java.util.List;

import com.aotain.zongfen.dto.analysis.WlanDto;
import com.aotain.zongfen.dto.common.WlanParamVo;
import com.aotain.zongfen.utils.PageResult;

public interface WlanAnalysisService {

	public PageResult<WlanDto> getIndexListData(WlanParamVo params);
	
	public List<WlanDto> getDetailListData(WlanParamVo params) throws SQLException;
	
	public List<WlanDto> getExportData(WlanParamVo params);
}
