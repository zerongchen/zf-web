package com.aotain.zongfen.controller.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import com.aotain.zongfen.dto.monitor.DataUploadMonitorDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.DataUploadDetailMonitorService;
import com.aotain.zongfen.service.monitor.DataUploadMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;


@Controller
@RequestMapping("/monitor/dataupload")
public class DataUploadMonitorController {
	private static Logger logger = LoggerFactory.getLogger(DataUploadMonitorController.class);
	@Autowired
	private DataUploadMonitorService dataUploadMonitorService;
	@Autowired
	private DataUploadDetailMonitorService dataUploadDetailMonitorService;
	

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/monitor/dataupload/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402001_query")
	@ResponseBody
	public PageResult<DataUploadMonitorDTO> list(
												@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
												@RequestParam(required = false, defaultValue = "10") Integer pageSize, DataUploadMonitorDTO dto){
		try {
			return dataUploadMonitorService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
	
	@RequestMapping(value="/listDetail")
	@RequiresPermission(value = "zf402001_query")
	@ResponseBody
	public PageResult<DataUploadDetailMonitorDTO> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, DataUploadDetailMonitorDTO dto){
		try {
			return dataUploadDetailMonitorService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402001_query")
    @ResponseBody
	public ResponseResult<Map> getChart( DataUploadMonitorDTO dto){
		String endTime = dto.getEndTime();
		try {
			Date date = DateUtils.parseDate(endTime,DateFormatConstant.DATE_CHS_HYPHEN);
			Calendar calendar = DateUtils.toCalendar(date);
			calendar.add(Calendar.DATE, 0);
			SimpleDateFormat sdf =new SimpleDateFormat(DateFormatConstant.DATE_CHS_HYPHEN);
			dto.setEndTime(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			logger.error("search fail",e);
		}
		Map dataMap = dataUploadMonitorService.getChart(dto);
		ECharts<Series> eCharts = (ECharts<Series>) dataMap.get("echart");
		String message="";
		message=dataMap.get("warnnum").toString();
		Map<String,Object> map = new HashMap<>();
		map.put("type",dto.getFileType());
		map.put("data",eCharts);
		ResponseResult responseResult =new ResponseResult();
		responseResult.setData(map);
		responseResult.setMessage(message);
		return responseResult;
	}
}
