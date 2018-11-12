package com.aotain.zongfen.controller.monitor;

import java.util.ArrayList;
import java.util.List;

import com.aotain.zongfen.interceptor.RequiresPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.monitor.MonitorTaskDetail;
import com.aotain.zongfen.model.monitor.MonitorTaskFile;
import com.aotain.zongfen.model.monitor.ResultMonitor;
import com.aotain.zongfen.service.monitor.MonitorTaskDetailService;
import com.aotain.zongfen.utils.PageResult;

/**
 * 告警监控详情页面
 * @author DongBoye ResultMonitor
 *
 */
@Controller
@RequestMapping("/monitor/detail")
public class MonitorTaskDetailController {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorTaskDetailController.class);
	
	@Autowired
	private MonitorTaskDetailService monitorTaskDetailService;
	
	
	@RequestMapping(value="/list")
	@ResponseBody
	public ResultMonitor<MonitorTaskDetail> list(MonitorTaskDetail dto){
		List<MonitorTaskDetail> list = monitorTaskDetailService.getList(dto);
		long count = monitorTaskDetailService.count(dto.getMonitorTaskId());
		ResultMonitor<MonitorTaskDetail> result = new ResultMonitor<MonitorTaskDetail>(count,list);
		return result;
	}
	
	@RequestMapping(value="/resend")
	@RequiresPermission(value = "zf401001_redo")
	@ResponseBody
	public ResponseResult<MonitorTaskDetail> resend(MonitorTaskDetail dto){
		ResponseResult<MonitorTaskDetail> result = new ResponseResult<MonitorTaskDetail>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		 try {
	        	if(monitorTaskDetailService.resendPolicy(dto)) {
	        		BaseKeys bk = new BaseKeys();
					bk.setMessageNo(dto.getDataId());
					bk.setMessageType(dto.getTaskSubtype());
					bk.setOperType(OperationType.RESEND.getType());
				//	bk.setDataType(DataType.POLICY.getType());
					bk.setMessage("taskId="+dto.getTaskId());
					keys.add(bk);
					result.setKeys(keys);
	        		result.setMessage("重发成功！");
	    			result.setResult(0);
	        	}else {
	        		result.setMessage("重发失败！");
	    			result.setResult(1);
	        	}
	         
	        }catch (Exception e){
	        	result.setMessage("重发失败！");
				result.setResult(1);
	            logger.error("resend policy fail!"+e);
	        }
	        return result;
	}

	@RequestMapping(value="/resend2")
	@RequiresPermission(value = "zf401002_redo")
	@ResponseBody
	public ResponseResult<MonitorTaskDetail> resendTask(MonitorTaskDetail dto){
		ResponseResult<MonitorTaskDetail> result = new ResponseResult<MonitorTaskDetail>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		try {
			if(monitorTaskDetailService.resendPolicy(dto)) {
				BaseKeys bk = new BaseKeys();
				bk.setMessageNo(dto.getDataId());
				bk.setMessageType(dto.getTaskSubtype());
				bk.setOperType(OperationType.RESEND.getType());
				//	bk.setDataType(DataType.POLICY.getType());
				bk.setMessage("taskId="+dto.getTaskId());
				keys.add(bk);
				result.setKeys(keys);
				result.setMessage("重发成功！");
				result.setResult(0);
			}else {
				result.setMessage("重发失败！");
				result.setResult(1);
			}

		}catch (Exception e){
			result.setMessage("重发失败！");
			result.setResult(1);
			logger.error("resend policy fail!"+e);
		}
		return result;
	}

	@RequestMapping(value="/listFile")
	@ResponseBody
	public PageResult<MonitorTaskFile> listFile(@RequestParam(required = true, defaultValue = "1")Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, MonitorTaskFile dto){
		return monitorTaskDetailService.getFileList(pageIndex, pageSize, dto);
	}
}
