package com.aotain.zongfen.service.monitor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.mapper.monitor.MonitorTaskAlarmMapper;
import com.aotain.zongfen.model.monitor.MonitorTaskAlarm;
import com.aotain.zongfen.service.monitor.MonitorTaskAlarmService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class MonitorTaskAlarmServiceImpl implements MonitorTaskAlarmService {
	
	@Autowired
	private MonitorTaskAlarmMapper monitorTaskAlarmMapper;

	@Override
	public PageResult<MonitorTaskAlarmDTO> getList(Integer pageIndex, Integer pageSize, MonitorTaskAlarmDTO dto) {
		PageResult<MonitorTaskAlarmDTO> result = new PageResult<MonitorTaskAlarmDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<MonitorTaskAlarmDTO> list = monitorTaskAlarmMapper.selectList(dto);
		for(MonitorTaskAlarmDTO alarm : list) {
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
			if(alarm.getTaskType() == 2 && alarm.getTaskSubtype() == 2000) {
				alarm.setAlarmTitle("全业务流量分析上报");
			}else if(alarm.getTaskType() == 2 && alarm.getTaskSubtype() == 2001) {
				alarm.setAlarmTitle("应用流量流向信息上报");
			}else if(alarm.getTaskType() == 3 && alarm.getTaskSubtype() == 3000) {
				alarm.setAlarmTitle("导出任务");
			}else if(alarm.getTaskType() == 4) {
				alarm.setAlarmTitle(MessageType.getTypeById(alarm.getTaskSubtype()));
			}else if(alarm.getTaskType() == 5 && alarm.getTaskSubtype() == 6000) {
				alarm.setAlarmTitle("文件上报");
			}else if(alarm.getTaskType() == 6 && alarm.getTaskSubtype() == 7000) {
				alarm.setAlarmTitle("Azkaban任务");
			}else if(alarm.getTaskType() == 99) {
				alarm.setAlarmTitle("系统消息");
			}
		}
		PageInfo<MonitorTaskAlarmDTO> pageResult = new PageInfo<MonitorTaskAlarmDTO>(list);
		result.setRows(list);
		result.setTotal(pageResult.getTotal());
		return result;
	}

	@Override
	public PageResult<MonitorTaskAlarmDTO> getDealSolutions(Integer pageIndex, Integer pageSize,
			MonitorTaskAlarmDTO dto) {
		PageResult<MonitorTaskAlarmDTO> result = new PageResult<MonitorTaskAlarmDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<MonitorTaskAlarmDTO> list = monitorTaskAlarmMapper.selectDealSolution(dto.getTaskType(), dto.getTaskSubtype());
		PageInfo<MonitorTaskAlarmDTO> pageResult = new PageInfo<MonitorTaskAlarmDTO>(list);
		result.setRows(list);
		result.setTotal(pageResult.getTotal());
		return result;
	}

	@Override
	public int addSolution(MonitorTaskAlarm alarm) {
		return monitorTaskAlarmMapper.update(alarm);
	}


	@Override
	public PageResult<Map<String,String>> getFileList(Integer pageIndex, Integer pageSize, Map<String,String> params) {
		PageResult<Map<String,String>> result = new PageResult<>();

		String fileType=params!=null&&params.containsKey("fileType")?params.get("fileType"):null;
		// 0 表示接收异常，1 表示上报异常
		String reportType=params!=null&&params.containsKey("reportType")?params.get("reportType"):null;

		if(fileType!=null){
			List<Map<String,String>> list=null;
			if("768".equals(fileType)){
				if("0".equals(reportType)){
					PageHelper.startPage(pageIndex, pageSize);
					list = monitorTaskAlarmMapper.selectReceivedException(params);
				}else if("1".equals(reportType)){
					PageHelper.startPage(pageIndex, pageSize);
					list = monitorTaskAlarmMapper.selectUploadException(params);
				}
			}else{
				PageHelper.startPage(pageIndex, pageSize);
				list = monitorTaskAlarmMapper.selectCreateException(params);
			}
			PageInfo<Map<String,String>> pageResult = new PageInfo<>(list);
			result.setRows(list);
			result.setTotal(pageResult.getTotal());
			return result;
		}else{
			return null;
		}

	}
}
