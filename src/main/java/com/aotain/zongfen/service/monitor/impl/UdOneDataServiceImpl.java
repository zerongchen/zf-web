package com.aotain.zongfen.service.monitor.impl;

import com.aotain.zongfen.dto.monitor.UdOneDataDTO;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.common.DictFactoryMapper;
import com.aotain.zongfen.mapper.device.IdcHouseMapper;
import com.aotain.zongfen.mapper.monitor.UdOneDataMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.echarts.Title;
import com.aotain.zongfen.service.monitor.UdOneDataService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UdOneDataServiceImpl implements UdOneDataService {
	
	@Autowired
	private UdOneDataMapper udOneDataMapper;
	
	@Autowired
	private IdcHouseMapper idcHouseMapper;
	
	@Autowired
	private ChinaAreaMapper chinaAreaMapper;
	
	@Autowired
	private DictFactoryMapper dictFactoryMapper;

	@Override
	public PageResult<UdOneDataDTO> getList(Integer pageIndex, Integer pageSize, UdOneDataDTO dto) {
		PageResult<UdOneDataDTO> result = new PageResult<UdOneDataDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<UdOneDataDTO> list = udOneDataMapper.selectList(dto);
		for(UdOneDataDTO m:list){
			if(m.getSavenum() == null){
				m.setSavenum(0l);
			}
		}
		PageInfo<UdOneDataDTO> pageResult = new PageInfo<UdOneDataDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public ECharts<Series> getChart(UdOneDataDTO dto) {
		List<UdOneDataDTO> list = null;
		// 接收记录数
		if(dto.getChartType() == 1){
			list = udOneDataMapper.selectChartListByReceived(dto);
		// 保存数
		}else if(dto.getChartType() == 2){
			list = udOneDataMapper.selectChartListBySave(dto);
		}

		List<String> legend = new ArrayList<String>();
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		// 数据集合
		List<Series> seriesList = new ArrayList<Series>();
		Map<String,Map<String,Long>>  uMap = new HashMap<String,Map<String,Long>>();


		if(list != null && list.size() > 0) {
			for(UdOneDataDTO obj : list) {
				if(!legend.contains(obj.getPacketsubname())){
					legend.add(obj.getPacketsubname());
				}
				if(!xAxis.contains(obj.getReceivedTime())){
					xAxis.add(obj.getReceivedTime());
				}
			}
			for(String legends:legend){
				Map<String,Long> hMap = new LinkedHashMap<String,Long>();
				for(String dateStr:xAxis){
					hMap.put(dateStr,0l);
				}
				uMap.put(legends,hMap);
			}


			if(dto.getChartType() == 1) {//1=receved(接收记录图)、
				for(UdOneDataDTO obj : list) {
					if(uMap.containsKey(obj.getPacketsubname())){
							uMap.get(obj.getPacketsubname()).put(obj.getReceivedTime(),obj.getReceivednum());
					}
				}
				for(Map.Entry<String,Map<String,Long>> entry:uMap.entrySet()){
					List value = Arrays.asList(entry.getValue().values().toArray());
					seriesList.add(new Series(entry.getKey(), "line", entry.getKey(), value, ""));
				}
			}else if(dto.getChartType() == 2) {//2=save(保存记录图)
				for(UdOneDataDTO obj : list) {
					if(uMap.containsKey(obj.getPacketsubname())){
						uMap.get(obj.getPacketsubname()).put(obj.getReceivedTime(),obj.getSavenum());
					}
				}
				for(Map.Entry<String,Map<String,Long>> entry:uMap.entrySet()){
					List value = Arrays.asList(entry.getValue().values().toArray());
					seriesList.add(new Series(entry.getKey(), "line", entry.getKey(), value, ""));
				}
			}
		}
		
		DataZoom dataRoom = new DataZoom();
		dataRoom.setZoomLock(false);
		dataRoom.setShow(true);
		int start = 0;
		int end = 100;
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
		
		return echarts;
	}
}
