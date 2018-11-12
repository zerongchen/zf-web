package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.MonitorTaskInfoDTO;
import com.aotain.zongfen.utils.PageResult;

public interface MonitorTaskInfoService {
	
	public PageResult<MonitorTaskInfoDTO> getList(Integer pageIndex,Integer pageSize,MonitorTaskInfoDTO dto); 
	
}
