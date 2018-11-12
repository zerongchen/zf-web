package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.HttpGetMonitorDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

public interface HttpGetMonitorService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<HttpGetMonitorDTO> getList(Integer pageIndex,Integer pageSize,HttpGetMonitorDTO dto);
	
	public ECharts<Series> getChart(HttpGetMonitorDTO dto);
	
}
