package com.aotain.zongfen.service.monitor.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.DataUploadMonitorDTO;
import com.aotain.zongfen.mapper.monitor.DataUploadMonitorMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.monitor.DataUploadMonitorService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


@Service
public class DataUploadMonitorServiceImpl implements DataUploadMonitorService{
	@Autowired
	private DataUploadMonitorMapper dataUploadMonitorMapper;
	
	@Override
	public PageResult<DataUploadMonitorDTO> getList(Integer pageIndex, Integer pageSize, DataUploadMonitorDTO dto) {
		Double maxSize = dataUploadMonitorMapper.selectMaxSize(dto);
		dto.setOrder("DESC");
		PageResult<DataUploadMonitorDTO> result = new PageResult<DataUploadMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<DataUploadMonitorDTO> list = dataUploadMonitorMapper.selectList(dto);
		transferUnitTable(list,getUnit(maxSize));
		PageInfo<DataUploadMonitorDTO> pageResult = new PageInfo<DataUploadMonitorDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public Map<String,Object> getChart( DataUploadMonitorDTO dto) {
		dto.setOrder("ASC");
		List<DataUploadMonitorDTO> list = dataUploadMonitorMapper.selectList(dto);
		Double maxSize = dataUploadMonitorMapper.selectMaxSize(dto);
		transferUnit(list,getUnit(maxSize));
		List<String> legend = new ArrayList<String>();
		legend.add("上报文件个数");
		legend.add("上报文件大小");
		
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();			
		List<Integer> createFileRecordList = new ArrayList<Integer>();
		List<Double> createFileSizeList = new ArrayList<Double>();
		Long count = 0l;
		if(list != null && list.size() > 0) {
			for(DataUploadMonitorDTO obj : list) {
				count = count+obj.getTotalAbnormalFileNum();
				timeSet.add(obj.getUploadTime());
				createFileSizeList.add(obj.getUploadFileSize());
				createFileRecordList.add(obj.getUploadFileNum());
			}
		}
		xAxis.addAll(timeSet);
		seriesList.add(new Series(legend.get(0), "bar", createFileRecordList,1));
		seriesList.add(new Series(legend.get(1), "line", createFileSizeList));
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
		dataRoom.setZoomLock(false);
		int start = 0;
		int end = 100;
		dataRoom.setStart(start);
		dataRoom.setEnd(end);
		
		ECharts<Series> echarts = new ECharts<Series>(legend,xAxis, seriesList, dataRoom);
		echarts.setUnit(getUnit(maxSize));
		Map<String ,Object> map = new HashMap<>();
		map.put("echart",echarts);
		map.put("warnnum",count);
		return map;
	}
	
	public String getUnit(Double maxSize) {
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

	public void transferUnit(List<DataUploadMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			String realSize = "";
			for(DataUploadMonitorDTO dto : list) {
				if(dto.getUploadFileSize() != null) {
					Long ut = Long.parseLong("1024");
					if( "KB".equals(unit)) {
						realSize = decimalFormat.format(dto.getUploadFileSize()/ut.longValue());
					}else if( "M".equals(unit)) {
						ut = Long.parseLong("1048576");
						realSize = decimalFormat.format((dto.getUploadFileSize()/ut.longValue()));
					}else if( "G".equals(unit)) {
						ut=Long.parseLong("1073741824");
						realSize = decimalFormat.format((dto.getUploadFileSize()/ut.longValue()));
					}else if( "T".equals(unit)) {
						ut=Long.parseLong("1099511627776");
						realSize = decimalFormat.format((dto.getUploadFileSize()/ut.longValue()));
					}
				}
				if(realSize !="") {
				    dto.setUploadFileSize(Double.valueOf(realSize));
				}
			}
		}
	}
	
	public void transferUnitTable(List<DataUploadMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(DataUploadMonitorDTO dto : list) {
				if(dto.getUploadFileSize() != null) {
					if( "KB".equals(unit)) {
						dto.setUploadFileSizeStr(decimalFormat.format(dto.getUploadFileSize()/Long.parseLong("1024"))+"KB");
					}else if( "M".equals(unit)) {
						dto.setUploadFileSizeStr(decimalFormat.format((dto.getUploadFileSize()/Long.parseLong("1048576")))+"M");
					}else if( "G".equals(unit)) {
						dto.setUploadFileSizeStr(decimalFormat.format((dto.getUploadFileSize()/Long.parseLong("1073741824")))+"G");
					}else if( "T".equals(unit)) {
						dto.setUploadFileSizeStr(decimalFormat.format((dto.getUploadFileSize()/Long.parseLong("1099511627776")))+"T");
					}
				}
			}
		}
	}
}
	
	
	
	

