package com.aotain.zongfen.service.device.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.mapper.device.DpiDynamicPortMapper;
import com.aotain.zongfen.model.device.DpiDynamicPort;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.echarts.Title;
import com.aotain.zongfen.service.device.DpiDynamicPortService;

@Service
public class DpiDynamicPortServiceImpl implements DpiDynamicPortService {

	@Autowired
	private DpiDynamicPortMapper mapper;
	
	@Override
	public ECharts<Integer> getChartByPort(DpiDynamicPort dpiDynamicPort) {
		List<DpiDynamicPort> list = mapper.selectByPort(dpiDynamicPort.getPortno());
		List<String> axis = new ArrayList<String>();
	//	Set<String> timeSet = new HashSet<String>();
		List<Integer> ayis = new  ArrayList<Integer>();
		if(list != null) {
			for(DpiDynamicPort one : list) {
			//	timeSet.add(one.getCreateTimeStr());
				axis.add(one.getCreateTimeStr());
				ayis.add(one.getPortusage());
			}
		}
//		axis.addAll(timeSet);
		List<Series<Integer>> seriesList = new ArrayList<Series<Integer>>();
		seriesList.add(new Series<Integer>("端口"+dpiDynamicPort.getPortno()+" 折线图", "line", ayis));
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
//		int start = axis.size() /20;
//		int end = axis.size() / 20 + 5;
		dataRoom.setStart(0);
		dataRoom.setEnd(100);
		
		Title title = new Title();
		title.setText("端口编号:"+dpiDynamicPort.getPortno());
		title.setSubtext("端口描述:"+dpiDynamicPort.getPortinfo());
		ECharts<Integer> echarts = new ECharts(title,axis, seriesList, dataRoom);
		return echarts;
	}

	@Override
	public List<DpiDynamicPort> getPortList() {
		return mapper.selectPort();
	}

}
