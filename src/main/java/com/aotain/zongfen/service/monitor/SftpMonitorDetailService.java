package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.SftpMonitorDetailDTO;
import com.aotain.zongfen.utils.PageResult;

public interface SftpMonitorDetailService {
	/**
	 * 分页列表
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public PageResult<SftpMonitorDetailDTO> getList(Integer pageIndex,Integer pageSize,SftpMonitorDetailDTO dto);
}
