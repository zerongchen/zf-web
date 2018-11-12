package com.aotain.zongfen.controller.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.interceptor.RequiresPermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.AppFlowMonitorDTO;
import com.aotain.zongfen.dto.monitor.AppFlowMonitorDetailDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.general.weblibrary.WEBCategoryServiceImpl;
import com.aotain.zongfen.service.monitor.AppFlowMonitorDetailService;
import com.aotain.zongfen.service.monitor.AppFlowMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

@Controller
@RequestMapping("/monitor/appflow")
public class AppFlowMonitorController {
	
	private static Logger logger = LoggerFactory.getLogger(AppFlowMonitorController.class);
	
	@Autowired
	private AppFlowMonitorDetailService appFlowMonitorDetailService;

	@Autowired
	private AppFlowMonitorService appFlowMonitorService;

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		Date endDate = new Date(); 
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DATE, -1);
	    Date startDate = calendar.getTime(); // 结果 
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		ModelAndView mav = new ModelAndView();
		mav.addObject("startTime", sdf.format(startDate));
		mav.addObject("endTime", sdf.format(endDate));
		mav.setViewName("/monitor/appflow/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402005_query")
	@ResponseBody
	public PageResult<AppFlowMonitorDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, AppFlowMonitorDTO dto){
		String endTime = dto.getEndTime();
		try {
			Date date = DateUtils.parseDate(endTime,DateFormatConstant.DATE_CHS_HYPHEN);
			Calendar calendar = DateUtils.toCalendar(date);
			calendar.add(Calendar.DATE, 1);
			SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
			dto.setEndTime(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			logger.error("search fail",e);
		}
		return appFlowMonitorService.getList(pageIndex, pageSize, dto);
	}
	
	@RequestMapping(value="/listDetail")
	@RequiresPermission(value = "zf402005_query")
	@ResponseBody
	public PageResult<AppFlowMonitorDetailDTO> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, AppFlowMonitorDetailDTO dto){
		Long startTime = dto.getStatTime();
		if(startTime!=null) {
			dto.setStartTime(startTime);
			Long endTime = startTime + 24*60*60 ;
			dto.setEndTime(endTime);
		}
		try {
			return appFlowMonitorDetailService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402005_query")
    @ResponseBody
	public ECharts<Series> getChart(AppFlowMonitorDTO dto){
		String endTime = dto.getEndTime();
		try {
			Date date = DateUtils.parseDate(endTime,DateFormatConstant.DATE_CHS_HYPHEN);
			Calendar calendar = DateUtils.toCalendar(date);
			calendar.add(Calendar.DATE, 1);
			SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
			dto.setEndTime(sdf.format(calendar.getTime()));
			return appFlowMonitorService.getChart(dto);
		} catch (ParseException e) {
			logger.error("get chart data fail",e);
		}
		return null;
	}
}
