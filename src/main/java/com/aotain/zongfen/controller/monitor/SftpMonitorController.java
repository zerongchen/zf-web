package com.aotain.zongfen.controller.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.interceptor.RequiresPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.dto.monitor.SftpMonitorDetailDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.SftpMonitorDetailService;
import com.aotain.zongfen.service.monitor.SftpMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

@Controller
@RequestMapping("/monitor/sftp")
public class SftpMonitorController {

	private static final Logger LOG = LoggerFactory.getLogger(SftpMonitorController.class);

	@Autowired
	private SftpMonitorDetailService sftpMonitorDetailService;

	@Autowired
	private SftpMonitorService sftpMonitorService;

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
		mav.setViewName("/monitor/sftp/index");
		return mav;
	}
	
	@RequestMapping(value="/list")
	@RequiresPermission(value = "zf402007_query")
	@ResponseBody
	public PageResult<SftpMonitorDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, SftpMonitorDTO dto){
		try {
			return sftpMonitorService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		return null;
	}
	
	@RequestMapping(value="/listDetail")
	@RequiresPermission(value = "zf402007_query")
	@ResponseBody
	public PageResult<SftpMonitorDetailDTO> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, SftpMonitorDetailDTO dto){
		try {
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
			return sftpMonitorDetailService.getList(pageIndex, pageSize, dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getChart")
	@RequiresPermission(value = "zf402007_query")
    @ResponseBody
	public ECharts<Series> getChart(SftpMonitorDTO dto){
		try {
			return sftpMonitorService.getChart(dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		return null;
	}
	
	@RequestMapping(value = "/getType")
	@RequiresPermission(value = "zf402007_query")
    @ResponseBody
	public List<SftpMonitorDTO> getType(){
		return sftpMonitorService.getFileType();
	}
}
