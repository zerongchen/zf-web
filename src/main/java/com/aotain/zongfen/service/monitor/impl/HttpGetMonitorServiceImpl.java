package com.aotain.zongfen.service.monitor.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.HttpGetMonitorDTO;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.common.DictFactoryMapper;
import com.aotain.zongfen.mapper.device.IdcHouseMapper;
import com.aotain.zongfen.mapper.monitor.HttpGetMonitorMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.echarts.Title;
import com.aotain.zongfen.service.monitor.HttpGetMonitorService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class HttpGetMonitorServiceImpl implements HttpGetMonitorService {
	
	@Autowired
	private HttpGetMonitorMapper httpGetMonitorMapper;
	@Autowired
	private IdcHouseMapper idcHouseMapper;
	@Autowired
	private ChinaAreaMapper chinaAreaMapper;
	
	@Autowired
	private DictFactoryMapper dictFactoryMapper;

	@Override
	public PageResult<HttpGetMonitorDTO> getList(Integer pageIndex, Integer pageSize, HttpGetMonitorDTO dto) {
		PageResult<HttpGetMonitorDTO> result = new PageResult<HttpGetMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<HttpGetMonitorDTO> list = httpGetMonitorMapper.selectList(dto);
		if(dto.getTableType()==3) {//天
			for(HttpGetMonitorDTO http : list) {
				http.setUploadFileSizeStr(http.getUploadFileSize()==null?"-":(http.getUploadFileSize()+" G"));
				http.setReceivedFileSizeStr(http.getReceivedFileSize()==null?"-":(http.getReceivedFileSize()+ " G") );
			}
		}else {
			for(HttpGetMonitorDTO http : list) {
				http.setUploadFileSizeStr(http.getUploadFileSize()==null?"-":(http.getUploadFileSize()+" M"));
				http.setReceivedFileSizeStr(http.getReceivedFileSize()==null?"-":(http.getReceivedFileSize()+ " M") );
			}
		}
		PageInfo<HttpGetMonitorDTO> pageResult = new PageInfo<HttpGetMonitorDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public ECharts<Series> getChart(HttpGetMonitorDTO dto) {
		
		List<HttpGetMonitorDTO> list = httpGetMonitorMapper.selectList(dto);
		List<String> legend = new ArrayList<String>();
		legend.add("接收文件数");
		legend.add("上报文件数");
		legend.add("接收文件总大小");
		legend.add("上报文件总大小");
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();
		
		List<Double> receivedFileSizeList = new ArrayList<Double>();
		List<Double> uploadFileSizeList = new ArrayList<Double>();
		List<Integer> receivedFileNumList = new ArrayList<Integer>();
		List<Integer> uploadFileNumList = new ArrayList<Integer>();
		
		if(list != null && list.size() > 0) {
			for(HttpGetMonitorDTO obj : list) {
				timeSet.add(obj.getReceivedTime());
				receivedFileSizeList.add(obj.getReceivedFileSize());
				uploadFileSizeList.add(obj.getUploadFileSize());
				receivedFileNumList.add(obj.getReceivedFileNum());
				uploadFileNumList.add(obj.getUploadFileNum());
			}
		}
		xAxis.addAll(timeSet);
		seriesList.add(new Series(legend.get(0), "bar", receivedFileNumList));
		seriesList.add(new Series(legend.get(1), "bar", uploadFileNumList));
		seriesList.add(new Series(legend.get(2), "line", receivedFileSizeList, 1));
		seriesList.add(new Series(legend.get(3), "line", uploadFileSizeList, 1));
		
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
		dataRoom.setZoomLock(false);
		int start = 0;
		int end = 30;
		dataRoom.setStart(start);
		dataRoom.setEnd(end);
		Title title = new Title();
		if(dto.getLevel() == 0 ) {
			title.setText("全省");
		}else if(dto.getLevel() == 1 ) {
			title.setText(dto.getProbeType()==0?"DPI":"EU");
		}else if(dto.getLevel() == 2 ) {
			String probeType = dto.getProbeType()==0?"DPI":"EU";
			if(dto.getProbeType() == 0) {//DPI
				title.setText(probeType + ":" +chinaAreaMapper.getAreaByCode(Long.parseLong(dto.getAreaId())).getAreaName());
			}else {
				title.setText(probeType + ":" +idcHouseMapper.selectByPrimaryKey(dto.getAreaId().toString()).getHouseName());
			}
			
		}else if(dto.getLevel() == 3 ) {
			String probeType = dto.getProbeType()==0?"DPI":"EU";
			if(dto.getProbeType() == 0) {//DPI
				title.setText(probeType + ":" +chinaAreaMapper.getAreaByCode(Long.parseLong(dto.getAreaId())).getAreaName());
			}else {
				title.setText(probeType + ":" +idcHouseMapper.selectByPrimaryKey(dto.getAreaId().toString()).getHouseName());
			}
			title.setSubtext(dictFactoryMapper.select(dto.getSoftwareProvider()).getFacotryName());
		}
		ECharts<Series> echarts = new ECharts<Series>(title, legend,xAxis, seriesList, dataRoom);
		//表格类型，1=分钟表格，2=小时表格，3=天表格（默认为2）
		if(dto.getTableType() == 1) {
			echarts.setUnit("M");
		}else if(dto.getTableType() == 2) {
			echarts.setUnit("M");
		}else if(dto.getTableType() == 4) {
			echarts.setUnit("G");
		}
		
		return echarts;
	}

}
