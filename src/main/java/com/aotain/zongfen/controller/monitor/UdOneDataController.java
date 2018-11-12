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

import com.aotain.zongfen.dto.monitor.UdOneDataDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.UdOneDataService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

@Controller
@RequestMapping("/monitor/ud1")
public class UdOneDataController {
	private static Logger LOG = LoggerFactory.getLogger(UdOneDataController.class);

	@Autowired
	private UdOneDataService udOneDataService;
	
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
		mav.setViewName("/monitor/ud1/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402006_query")
	@ResponseBody
	public PageResult<UdOneDataDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, UdOneDataDTO dto){
		try {
			return udOneDataService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402006_query")
    @ResponseBody
	public ECharts<Series> getChart(UdOneDataDTO dto){
		try {
			return udOneDataService.getChart(dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		return null;
	}
}
