package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDTO;
import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDetailDTO;
import com.aotain.zongfen.dto.monitor.AppFlowMonitorDTO;
import com.aotain.zongfen.dto.monitor.AppFlowMonitorDetailDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

/**
 * 
 * ClassName: 应用流量数据监控
 * Description: TODO
 * date: 2018年4月4日 下午1:49:39
 * 
 * @author tanzj
 * @version  
 * @since JDK 1.8
 */
public interface AppFlowDirecMonitorService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<AppFlowDirecMonitorDTO> getList(Integer pageIndex, Integer pageSize, AppFlowDirecMonitorDTO dto);
	
	public ECharts<Series> getChart(AppFlowDirecMonitorDTO dto);
	
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<AppFlowDirecMonitorDetailDTO> getList(Integer pageIndex,Integer pageSize,AppFlowDirecMonitorDetailDTO dto);
}
