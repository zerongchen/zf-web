package com.aotain.zongfen.service.monitor.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.AppFlowMonitorDTO;
import com.aotain.zongfen.mapper.monitor.AppFlowMonitorMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.AppFlowMonitorService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AppFlowMonitorServiceImpl implements AppFlowMonitorService{
	
	@Autowired
	private AppFlowMonitorMapper appFlowMonitorMapper;
	
	@Override
	public PageResult<AppFlowMonitorDTO> getList(Integer pageIndex, Integer pageSize, AppFlowMonitorDTO dto) {
		dto.setOrderBy("DESC");
		Long maxSize = appFlowMonitorMapper.selectMaxSize(dto);
		PageResult<AppFlowMonitorDTO> result = new PageResult<AppFlowMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<AppFlowMonitorDTO> list = appFlowMonitorMapper.selectList(dto);
		transferUnitTable(list,getUnit(maxSize));
		PageInfo<AppFlowMonitorDTO> pageResult = new PageInfo<AppFlowMonitorDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public ECharts<Series> getChart(AppFlowMonitorDTO dto) {
		dto.setOrderBy("ASC");
		List<AppFlowMonitorDTO> list = appFlowMonitorMapper.selectList(dto);
		Long maxSize = appFlowMonitorMapper.selectMaxSize(dto);
		transferUnit(list,getUnit(maxSize));
		List<String> legend = new ArrayList<String>();
		
		legend.add("生成文件大小");
		legend.add("生成文件个数");
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();
		
		
		List<Long> createFileRecordList = new ArrayList<Long>();
		List<Double> createFileSizeList = new ArrayList<Double>();
		
		if(list != null && list.size() > 0) {
			for(AppFlowMonitorDTO obj : list) {
				timeSet.add(obj.getCreateTime());
				createFileSizeList.add(obj.getCreateFileSize());
				createFileRecordList.add(obj.getCreateFileRecord());
			}
		}
		xAxis.addAll(timeSet);
		seriesList.add(new Series(legend.get(0), "bar", createFileSizeList));
		seriesList.add(new Series(legend.get(1), "line", createFileRecordList,1));
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

	public void transferUnit(List<AppFlowMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			String realSize = "";
			for(AppFlowMonitorDTO dto : list) {
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
	
	public void transferUnitTable(List<AppFlowMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(AppFlowMonitorDTO dto : list) {
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
