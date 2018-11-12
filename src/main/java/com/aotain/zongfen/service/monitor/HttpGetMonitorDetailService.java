package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.HttpGetMonitorDetailDTO;
import com.aotain.zongfen.utils.PageResult;

public interface HttpGetMonitorDetailService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<HttpGetMonitorDetailDTO> getList(Integer pageIndex,Integer pageSize,HttpGetMonitorDetailDTO dto);
}
