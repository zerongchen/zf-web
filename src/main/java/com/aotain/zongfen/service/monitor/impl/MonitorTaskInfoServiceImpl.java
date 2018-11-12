package com.aotain.zongfen.service.monitor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.zongfen.dto.monitor.MonitorTaskInfoDTO;
import com.aotain.zongfen.mapper.monitor.MonitorTaskInfoMapper;
import com.aotain.zongfen.service.monitor.MonitorTaskInfoService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class MonitorTaskInfoServiceImpl implements MonitorTaskInfoService {

	@Autowired
	private MonitorTaskInfoMapper monitorTaskInfoMapper;
	
	@Override
	public PageResult<MonitorTaskInfoDTO> getList(Integer pageIndex, Integer pageSize, MonitorTaskInfoDTO dto) {
		PageResult<MonitorTaskInfoDTO> result = new PageResult<MonitorTaskInfoDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<MonitorTaskInfoDTO> list = monitorTaskInfoMapper.selectList(dto);
		for(MonitorTaskInfoDTO task : list) {
			 /**
		     * 2=主动上报,3=导出任务,4=DPI策略,5=文件上报,6=Azkaban任务,99=系统消息
		     */
			 /**
		     * 主动上报:2000=全业务流量分析上报,2001=应用流量流向信息上报
		     * 导出任务:3000
		     * DPI策略:MessageType
		     * 文件上报:6000
		     * Azkaban任务:7000
		     * 系统消息:
		     */
			if(task.getTaskType() == 2 && task.getTaskSubtype() == 2000) {
				task.setTaskTitle("全业务流量分析上报");
			}else if(task.getTaskType() == 2 && task.getTaskSubtype() == 2001) {
				task.setTaskTitle("应用流量流向信息上报");
			}else if(task.getTaskType() == 3 && task.getTaskSubtype() == 3000) {
				task.setTaskTitle("导出任务");
			}else if(task.getTaskType() == 4) {
				task.setTaskTitle(MessageType.getTypeById(task.getTaskSubtype()));
			}else if(task.getTaskType() == 5 && task.getTaskSubtype() == 6000) {
				task.setTaskTitle("文件上报");
			}else if(task.getTaskType() == 6 && task.getTaskSubtype() == 7000) {
				task.setTaskTitle("Azkaban任务");
			}else if(task.getTaskType() == 99) {
				task.setTaskTitle("系统消息");
			}
		}
		PageInfo<MonitorTaskInfoDTO> pageResult = new PageInfo<MonitorTaskInfoDTO>(list);
		result.setRows(list);
		result.setTotal(pageResult.getTotal());
		return result;
	}

}
