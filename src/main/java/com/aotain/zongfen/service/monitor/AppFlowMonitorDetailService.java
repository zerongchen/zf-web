package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.AppFlowMonitorDetailDTO;
import com.aotain.zongfen.utils.PageResult;

public interface AppFlowMonitorDetailService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<AppFlowMonitorDetailDTO> getList(Integer pageIndex,Integer pageSize,AppFlowMonitorDetailDTO dto);
}
