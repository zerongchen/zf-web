package com.aotain.zongfen.controller.monitor;

import javax.servlet.http.HttpServletRequest;

import com.aotain.common.config.LocalConfig;
import com.aotain.zongfen.interceptor.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.MonitorTaskInfoDTO;
import com.aotain.zongfen.service.monitor.MonitorTaskInfoService;
import com.aotain.zongfen.utils.PageResult;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务监控管理
 * @author DongBoye
 *
 */
@Controller
@RequestMapping("/monitor/task")
public class MonitorTaskInfoController {
	
	@Autowired
	private MonitorTaskInfoService monitorTaskInfoService;
	
	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/monitor/task/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf401002_query")
	@ResponseBody
	public PageResult<MonitorTaskInfoDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, MonitorTaskInfoDTO dto){
		return monitorTaskInfoService.getList(pageIndex, pageSize, dto);
	}

	@RequestMapping(value="/getRefreshTime")
	@ResponseBody
	public String getRefreshTime(){
		System.out.println("get:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		return LocalConfig.getInstance().getHashValueByHashKey("monitor.alarm.refresh.time");
	}
}
