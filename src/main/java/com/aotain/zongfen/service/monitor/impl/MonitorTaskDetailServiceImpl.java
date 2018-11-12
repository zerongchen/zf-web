package com.aotain.zongfen.service.monitor.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.dto.monitor.MonitorTaskInfoDTO;
import com.aotain.zongfen.mapper.monitor.MonitorTaskAlarmMapper;
import com.aotain.zongfen.mapper.monitor.MonitorTaskDetailMapper;
import com.aotain.zongfen.mapper.monitor.MonitorTaskFileMapper;
import com.aotain.zongfen.mapper.monitor.MonitorTaskInfoMapper;
import com.aotain.zongfen.model.monitor.MonitorTaskDetail;
import com.aotain.zongfen.model.monitor.MonitorTaskFile;
import com.aotain.zongfen.model.monitor.MonitorTaskInfo;
import com.aotain.zongfen.service.monitor.MonitorTaskDetailService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class MonitorTaskDetailServiceImpl extends BaseService implements MonitorTaskDetailService {
	
	@Autowired
	private MonitorTaskDetailMapper monitorTaskDetailMapper;
	
	@Autowired
	private MonitorTaskFileMapper monitorTaskFileMapper;
	
	@Autowired
	private MonitorTaskAlarmMapper monitorTaskAlarmMapper;
	
	@Autowired
	private MonitorTaskInfoMapper monitorTaskInfoMapper;
	
	@Override
	public MonitorTaskDetail getOne(Integer type, Long monitorTaskId) {
		MonitorTaskDetail detail = new MonitorTaskDetail();
		
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
		if(type == 1) {//alarm
			MonitorTaskAlarmDTO alarm = monitorTaskAlarmMapper.select(monitorTaskId);
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
			detail.setTitle(alarm.getAlarmTitle());
			detail.setContent(alarm.getAlarmContent());
			detail.setParam(alarm.getAlarmParams());
		}else {//2,task
			MonitorTaskInfoDTO task = monitorTaskInfoMapper.select(monitorTaskId);
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
			detail.setTitle(task.getTaskTitle());
			detail.setContent(task.getMonitorName());
			detail.setParam(task.getMonitorParams());
		}
		return detail;
	}
	
	@Override
	public List<MonitorTaskDetail> getList(MonitorTaskDetail record) {
		List<MonitorTaskDetail> detailList = monitorTaskDetailMapper.selectList(record.getMonitorTaskId(),record.getCount());
		for(MonitorTaskDetail detail : detailList) {
			if(detail.getTaskType() == 2) {//主动上报
				MonitorTaskFile param = new MonitorTaskFile();
				param.setMonitorTaskId(record.getMonitorTaskId());
				param.setTaskId(detail.getTaskId());
				param.setStatus((short)2);//成功
				int successNum = monitorTaskFileMapper.countNum(param);
				param.setStatus((short)1);//失败
				int failNum = monitorTaskFileMapper.countNum(param);
				
				detail.setSuccessNum(successNum);
				detail.setFailNum(failNum);
			}else if(detail.getTaskType() == 5) {//文件上报
				MonitorTaskFile param = new MonitorTaskFile();
				param.setMonitorTaskId(record.getMonitorTaskId());
				param.setTaskId(detail.getTaskId());
				param.setStatus((short)2);//成功
				int successNum = monitorTaskFileMapper.countNum(param);
				param.setStatus((short)1);//失败
				int failNum = monitorTaskFileMapper.countNum(param);
				param.setStatus((short)0);//生成
				int createNum = monitorTaskFileMapper.countNum(param);
				
				detail.setSuccessNum(successNum);
				detail.setFailNum(failNum);
				detail.setCreateNum(createNum);
			}
		}
		return detailList;
	}
	@Override
	public long count(Long monitorTaskId) {
		return monitorTaskDetailMapper.count(monitorTaskId);
	}
	
	/**
	 * 策略的重发
	 * @param record
	 * @return
	 */
	public boolean resendPolicy(MonitorTaskDetail record) {
		if(record.getDataId() != null && record.getTaskSubtype() != null) {
			// 重发主策略
			manualRetryPolicyTask(record.getMonitorTaskId(),ProbeType.DPI.getValue(), record.getTaskSubtype(),record.getDataId(), new ArrayList<>());
			record.setStatus((short)4);
			MonitorTaskInfo dto = new MonitorTaskInfo();
			dto.setMonitorTaskId(record.getMonitorTaskId());
			dto.setStatus((short)0);
			monitorTaskInfoMapper.update(dto); 
			return true;
		}
		return false;
	}
	
	@Override
	public PageResult<MonitorTaskFile> getFileList(Integer pageIndex, Integer pageSize, MonitorTaskFile dto) {
		PageResult<MonitorTaskFile> result = new PageResult<MonitorTaskFile>();
		PageHelper.startPage(pageIndex, pageSize);
		List<MonitorTaskFile> list = monitorTaskFileMapper.selectList(dto);
		PageInfo<MonitorTaskFile> pageResult = new PageInfo<MonitorTaskFile>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}
	
	
	@Override
	protected boolean addDb(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return false;
	}

}
