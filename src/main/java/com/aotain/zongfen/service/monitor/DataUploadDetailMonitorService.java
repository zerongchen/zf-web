package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import com.aotain.zongfen.utils.PageResult;

public interface DataUploadDetailMonitorService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<DataUploadDetailMonitorDTO> getList(Integer pageIndex,Integer pageSize,DataUploadDetailMonitorDTO dto);

}
