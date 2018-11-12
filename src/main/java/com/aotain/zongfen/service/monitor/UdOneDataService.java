package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.UdOneDataDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

public interface UdOneDataService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<UdOneDataDTO> getList(Integer pageIndex,Integer pageSize,UdOneDataDTO dto);
	
	public ECharts<Series> getChart(UdOneDataDTO dto);
}
