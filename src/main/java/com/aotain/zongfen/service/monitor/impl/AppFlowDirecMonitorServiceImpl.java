package com.aotain.zongfen.service.monitor.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDTO;
import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDetailDTO;
import com.aotain.zongfen.mapper.monitor.AppFlowDirecMonitorDetailMapper;
import com.aotain.zongfen.mapper.monitor.AppFlowDirecMonitorMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.AppFlowDirecMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AppFlowDirecMonitorServiceImpl implements AppFlowDirecMonitorService{
	
	@Autowired
	private AppFlowDirecMonitorMapper appFlowDataMonitorMapper;
	
	@Autowired
	private AppFlowDirecMonitorDetailMapper appFlowDataMonitorDetailMapper;

	@Override
	public PageResult<AppFlowDirecMonitorDetailDTO> getList(Integer pageIndex, Integer pageSize,
			AppFlowDirecMonitorDetailDTO dto) {
		if(dto.getStatTime()!=null && !"".equals(dto.getStatTime())) {
			setTimeGap(dto);
		}
		PageResult<AppFlowDirecMonitorDetailDTO> result = new PageResult<AppFlowDirecMonitorDetailDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<AppFlowDirecMonitorDetailDTO> list = appFlowDataMonitorDetailMapper.selectList(dto);
		PageInfo<AppFlowDirecMonitorDetailDTO> pageResult = new PageInfo<AppFlowDirecMonitorDetailDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}
	
    /**
     * 根据时间粒度设置事件跨度，DateType:1 往前推5min长度 ，DateType：2 往前推1H ，DateType：3 往前推1D
     * @param detail
     */
    private void setTimeGap(AppFlowDirecMonitorDetailDTO detail){
        Calendar beginTime =null;
        Calendar endTime =null;
        Date end=null;
        if(detail.getDateType()!=null) {
        	 switch (detail.getDateType()){
             case 1:
                 end = DateUtils.parse(DateFormatConstant.DATETIME_WITHOUT_SECOND,detail.getStatTime());
                 endTime = DateUtils.toCalendar(end);
                 beginTime = DateUtils.toCalendar(end);
                 endTime.set(Calendar.MINUTE,beginTime.get(Calendar.MINUTE)+5);
                 detail.setStartTime(beginTime.getTimeInMillis()/1000);
                 detail.setEndTime(endTime.getTimeInMillis()/1000);
                 break;
             case 2:
                 end = DateUtils.parse(DateFormatConstant.DATETIME_WITHOUT_HOUR,detail.getStatTime());
                 endTime = DateUtils.toCalendar(end);
                 beginTime = DateUtils.toCalendar(end);
                 endTime.set(Calendar.HOUR,endTime.get(Calendar.HOUR)+1);
                 detail.setStartTime(beginTime.getTimeInMillis()/1000);
                 detail.setEndTime(endTime.getTimeInMillis()/1000);
                 break;
             case 3:
                 end = DateUtils.parse(DateFormatConstant.DATE_CHS_HYPHEN,detail.getStatTime());
                 endTime = DateUtils.toCalendar(end);
                 beginTime = DateUtils.toCalendar(end);
                 endTime.set(Calendar.DATE,endTime.get(Calendar.DATE)+1);
                 detail.setStartTime(beginTime.getTimeInMillis()/1000);
                 detail.setEndTime(endTime.getTimeInMillis()/1000);
                 break;
         }
        }
    }

	@Override
	public PageResult<AppFlowDirecMonitorDTO> getList(Integer pageIndex, Integer pageSize, AppFlowDirecMonitorDTO dto) {
		dto.setOrderBy("DESC");
		Long maxSize = appFlowDataMonitorMapper.selectMaxSize(dto);
		PageResult<AppFlowDirecMonitorDTO> result = new PageResult<AppFlowDirecMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<AppFlowDirecMonitorDTO> list = appFlowDataMonitorMapper.selectList(dto);
		transferUnitTable(list,getUnit(maxSize));
		List<String> legend = new ArrayList<String>();
		PageInfo<AppFlowDirecMonitorDTO> pageResult = new PageInfo<AppFlowDirecMonitorDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public ECharts<Series> getChart(AppFlowDirecMonitorDTO dto) {
		dto.setOrderBy("ASC");
		List<AppFlowDirecMonitorDTO> list = appFlowDataMonitorMapper.selectList(dto);
		Long maxSize = appFlowDataMonitorMapper.selectMaxSize(dto);
		transferUnit(list,getUnit(maxSize));
		List<String> legend = new ArrayList<String>();
		
		legend.add("生成文件大小");
		legend.add("生成文件个数");
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();
		
		List<Double> createFileSize = new ArrayList<Double>();
		List<Integer> createFileNumList = new ArrayList<Integer>();
		
		if(list != null && list.size() > 0) {
			for(AppFlowDirecMonitorDTO obj : list) {
				timeSet.add(obj.getCreateTime());
				createFileNumList.add(obj.getCreateFileNum());
				createFileSize.add(Double.valueOf(obj.getCreateFileSize()));
			}
		}
		xAxis.addAll(timeSet);
		seriesList.add(new Series(legend.get(0), "bar",createFileSize));
		seriesList.add(new Series(legend.get(1), "line",createFileNumList,1));
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
		dataRoom.setZoomLock(false);
		int start = 0;
		int end = 100;
		dataRoom.setStart(start);
		dataRoom.setEnd(end);
		
		ECharts<Series> echarts = new ECharts<Series>(legend,xAxis, seriesList, dataRoom);
		echarts.setUnit(getUnit(maxSize));
		return echarts;
		}

	public String getUnit(Long maxSize) {
		if(maxSize == null || maxSize< Long.parseLong("1048576")) {
			return "KB";
		}else if(Long.parseLong("1073741824") > maxSize && maxSize >= Long.parseLong("1048576")) {
			return "M";
		}else if( Long.parseLong("1099511627776") > maxSize && maxSize>= Long.parseLong("1073741824")) {
			return "G";
		}else if(maxSize>= Long.parseLong("1099511627776")) {
			return "T";
		}
		return "KB";
	}
	
	public void transferUnit(List<AppFlowDirecMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			String realSize = "";
			for(AppFlowDirecMonitorDTO dto : list) {
				if(dto.getCreateFileSize() != null) {
					if( "KB".equals(unit)) {
						realSize = decimalFormat.format(dto.getCreateFileSize()/Long.parseLong("1024"));
					}else if( "M".equals(unit)) {
						realSize = decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1048576")));
					}else if( "G".equals(unit)) {
						realSize = decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1073741824")));
					}else if( "T".equals(unit)) {
						realSize = decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1099511627776")));
					}
				}
				dto.setCreateFileSize(Double.valueOf(realSize));
			}
		}
	}
	
	public void transferUnitTable(List<AppFlowDirecMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(AppFlowDirecMonitorDTO dto : list) {
				if(dto.getCreateFileSize() != null) {
					if( "KB".equals(unit)) {
						dto.setCreatFileSizeUnit(decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1024")))+"KB");
					}else if( "M".equals(unit)) {
						dto.setCreatFileSizeUnit(decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1048576")))+"M");
					}else if( "G".equals(unit)) {
						dto.setCreatFileSizeUnit(decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1073741824")))+"G");
					}else if( "T".equals(unit)) {
						dto.setCreatFileSizeUnit(decimalFormat.format((dto.getCreateFileSize()/Long.parseLong("1099511627776")))+"T");
					}
				}
			}
		}
	}
}
