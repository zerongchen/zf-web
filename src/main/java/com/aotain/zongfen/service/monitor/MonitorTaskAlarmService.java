package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.model.monitor.MonitorTaskAlarm;
import com.aotain.zongfen.utils.PageResult;

import java.util.Map;

public interface MonitorTaskAlarmService {
	
	public PageResult<MonitorTaskAlarmDTO> getList(Integer pageIndex,Integer pageSize,MonitorTaskAlarmDTO dto); 
	
	public PageResult<MonitorTaskAlarmDTO> getDealSolutions(Integer pageIndex,Integer pageSize,MonitorTaskAlarmDTO dto); 
	
	public int addSolution(MonitorTaskAlarm alarm);

    PageResult<Map<String,String>> getFileList(Integer pageIndex, Integer pageSize, Map<String,String> param);
}
