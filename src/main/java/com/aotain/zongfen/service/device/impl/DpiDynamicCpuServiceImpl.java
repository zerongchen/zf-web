package com.aotain.zongfen.service.device.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.mapper.device.DpiDynamicCpuMapper;
import com.aotain.zongfen.model.device.DpiDynamicCpu;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.echarts.Title;
import com.aotain.zongfen.service.device.DpiDynamicCpuService;

@Service
public class DpiDynamicCpuServiceImpl implements DpiDynamicCpuService {
	
	@Autowired
	private DpiDynamicCpuMapper mapper;

	@Override
	public ECharts<Integer> getChartByCpu(Integer cpuNo) {
		List<DpiDynamicCpu> list = mapper.selectByCpu(cpuNo);
		List<String> axis = new ArrayList<String>();
		Set<String> timeSet = new HashSet<String>();
		List<Integer> ayis = new  ArrayList<Integer>();
		if(list != null) {
			for(DpiDynamicCpu one : list) {
				timeSet.add(one.getCreateTimeStr());
				ayis.add(one.getCpuUsage());
			}
		}
		axis.addAll(timeSet);
		List<Series<Integer>> seriesList = new ArrayList<Series<Integer>>();
		seriesList.add(new Series<Integer>(cpuNo+" 折线图", "line", ayis));
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
//		int start = axis.size() /20;
//		int end = axis.size() / 20 + 5;
		dataRoom.setStart(0);
		dataRoom.setEnd(100);
		Title title = new Title();
		title.setText("CPU编号:"+cpuNo);
		ECharts<Integer> echarts = new ECharts(title, axis, seriesList, dataRoom);
		return echarts;
	}

	@Override
	public List<DpiDynamicCpu> getCpuList() {
		return mapper.selectCPU();
	}

}
