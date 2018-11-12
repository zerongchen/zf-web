package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.AppFlowMonitorDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

public interface AppFlowMonitorService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<AppFlowMonitorDTO> getList(Integer pageIndex,Integer pageSize,AppFlowMonitorDTO dto);
	
	public ECharts<Series> getChart(AppFlowMonitorDTO dto);
}
