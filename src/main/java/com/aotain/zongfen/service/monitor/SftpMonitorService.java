package com.aotain.zongfen.service.monitor;

import java.util.List;

import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

public interface SftpMonitorService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<SftpMonitorDTO> getList(Integer pageIndex,Integer pageSize,SftpMonitorDTO dto);
	
	public ECharts<Series> getChart(SftpMonitorDTO dto);
	
	public List<SftpMonitorDTO> getFileType();
}
