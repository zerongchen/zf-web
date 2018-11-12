package com.aotain.zongfen.controller.monitor;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDTO;
import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDetailDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.AppFlowDirecMonitorService;
import com.aotain.zongfen.utils.PageResult;

/**
 * 
 * ClassName: AppFlowDataMonitorController
 * Description:应用流量数据监控
 * date: 2018年4月4日 上午11:29:13
 * 
 * @author tanzj
 * @version  
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/monitor/appflowdirec")
public class AppFlowDirecMonitorController {
	private static Logger logger = LoggerFactory.getLogger(AppFlowDirecMonitorController.class);

	@Autowired
	private AppFlowDirecMonitorService appFlowDirecMonitorService;

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/monitor/appflowdirec/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402004_query")
	@ResponseBody
	public PageResult<AppFlowDirecMonitorDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, AppFlowDirecMonitorDTO dto){
		try {
			return appFlowDirecMonitorService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
	
	@RequestMapping(value="/listDetail")
	@RequiresPermission(value = "zf402004_query")
	@ResponseBody
	public PageResult<AppFlowDirecMonitorDetailDTO> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, AppFlowDirecMonitorDetailDTO dto){
		try {
			return appFlowDirecMonitorService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402004_query")
    @ResponseBody
	public ECharts<Series> getChart(AppFlowDirecMonitorDTO dto){
		try {
			return appFlowDirecMonitorService.getChart(dto);
		} catch (Exception e) {
			logger.error("search fail",e);
		}
		return null;
	}
}
