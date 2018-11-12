package com.aotain.zongfen.controller.monitor;

import com.aotain.zongfen.dto.monitor.HttpGetMonitorDTO;
import com.aotain.zongfen.dto.monitor.HttpGetMonitorDetailDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.HttpGetMonitorDetailService;
import com.aotain.zongfen.service.monitor.HttpGetMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/monitor/httpget")
public class HttpGetMonitorController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HttpGetMonitorDetailService httpGetMonitorDetailService;

	@Autowired
	private HttpGetMonitorService httpGetMonitorService;

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		Date endDate = new Date(); 
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DATE, -1);
	    Date startDate = calendar.getTime(); // 结果 
		SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
		ModelAndView mav = new ModelAndView();
		mav.addObject("tableType", 2);
		mav.addObject("level", 0);
		mav.addObject("startTime", sdf.format(startDate));
		mav.addObject("endTime", sdf.format(endDate));
		mav.setViewName("/monitor/httpget/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402003_query")
	@ResponseBody
	public PageResult<HttpGetMonitorDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, HttpGetMonitorDTO dto){
		dto.setOrder("DESC");
		String endTime = dto.getEndTime();
		try {
			Date date = DateUtils.parseDate(endTime,DateFormatConstant.DATE_CHS_HYPHEN);
			Calendar calendar = DateUtils.toCalendar(date);
			calendar.add(Calendar.DATE, 1);
			SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
			dto.setEndTime(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			logger.error("query error ",e);
		}
		return httpGetMonitorService.getList(pageIndex, pageSize, dto);
	}
	
	@RequestMapping(value="/listDetail")
	@RequiresPermission(value = "zf402003_query")
	@ResponseBody
	public PageResult<HttpGetMonitorDetailDTO> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, HttpGetMonitorDetailDTO dto){
		if(dto.getStatTime() != null && dto.getTableType() != null) {
			Long startTime = dto.getStatTime();
			dto.setStartTime(startTime);
			Long endTime = 0L;
			if (dto.getTableType() == 1) {// 分钟，5分钟为颗粒度
				endTime = startTime + 5*60 ;
				dto.setEndTime(endTime);
			} else if (dto.getTableType() == 2) {// 小时，1小时为颗粒度
				endTime = startTime + 60*60 ;
				dto.setEndTime(endTime);
			} else if (dto.getTableType() == 3) {// 天，1天为颗粒度 
				endTime = startTime + 24*60*60 ;
				dto.setEndTime(endTime);
			}
		}
		return httpGetMonitorDetailService.getList(pageIndex, pageSize, dto);
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402003_query")
    @ResponseBody
	public ECharts<Series> getChart(HttpGetMonitorDTO dto){
		dto.setOrder("ASC");
		String endTime = dto.getEndTime();
		try {
			Date date = DateUtils.parseDate(endTime,DateFormatConstant.DATE_CHS_HYPHEN);
			Calendar calendar = DateUtils.toCalendar(date);
			calendar.add(Calendar.DATE, 1);
			SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
			dto.setEndTime(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return httpGetMonitorService.getChart(dto);
	}
}
