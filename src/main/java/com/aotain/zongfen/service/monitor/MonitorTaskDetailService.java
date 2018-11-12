package com.aotain.zongfen.service.monitor;

import java.util.List;

import com.aotain.zongfen.model.monitor.MonitorTaskDetail;
import com.aotain.zongfen.model.monitor.MonitorTaskFile;
import com.aotain.zongfen.utils.PageResult;

public interface MonitorTaskDetailService {
	/**
	 * 根据monitorTaskId来获取告警详情
	 * @param pageIndex
	 * @param pageSize
	 * @param dto
	 * @return
	 */
	public List<MonitorTaskDetail> getList(MonitorTaskDetail record);
	
	public boolean resendPolicy(MonitorTaskDetail record);
	
	public long count(Long monitorTaskId);
	
	public PageResult<MonitorTaskFile> getFileList(Integer pageIndex,Integer pageSize, MonitorTaskFile dto);
	
	public MonitorTaskDetail getOne(Integer type,Long monitorTaskId);
}
