package com.aotain.zongfen.service.analysis;

import java.text.ParseException;
import java.util.List;

import com.aotain.zongfen.dto.analysis.IllegalRouteListDto;
import com.aotain.zongfen.dto.common.IllegalRouteParamVo;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

public interface IllegalRouteAnalysisService {

	public PageResult<IllegalRouteListDto> getIndexListData(IllegalRouteParamVo params);
	
	public List<IllegalRouteListDto> getExportData(IllegalRouteParamVo params)  throws ParseException;
	
	public ECharts<Series> getChart(IllegalRouteParamVo params);
}
