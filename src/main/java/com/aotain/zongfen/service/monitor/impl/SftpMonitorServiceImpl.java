package com.aotain.zongfen.service.monitor.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.aotain.zongfen.dto.monitor.DataUploadMonitorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.common.DictFactoryMapper;
import com.aotain.zongfen.mapper.device.IdcHouseMapper;
import com.aotain.zongfen.mapper.monitor.SftpMonitorMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.echarts.Title;
import com.aotain.zongfen.service.monitor.SftpMonitorService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class SftpMonitorServiceImpl implements SftpMonitorService {
	
	@Autowired
	private ChinaAreaMapper chinaAreaMapper;
	
	@Autowired
	private IdcHouseMapper idcHouseMapper;

	@Autowired
	private SftpMonitorMapper sftpMonitorMapper;
	@Autowired
	private DictFactoryMapper dictFactoryMapper;
	
	@Override
	public PageResult<SftpMonitorDTO> getList(Integer pageIndex, Integer pageSize, SftpMonitorDTO dto) {
		PageResult<SftpMonitorDTO> result = new PageResult<SftpMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<SftpMonitorDTO> list = sftpMonitorMapper.selectList(dto);

		Double maxSize = 0.0;
		if(list != null && list.size() > 0) {
			for(SftpMonitorDTO obj : list) {
				if(obj.getReceivedFileSize()>maxSize){
					maxSize=obj.getReceivedFileSize();
				}
			}
		}
		transferUnitTable(list,getUnit(maxSize));
		PageInfo<SftpMonitorDTO> pageResult = new PageInfo<SftpMonitorDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public ECharts<Series> getChart(SftpMonitorDTO dto) {
	//	List<SftpMonitorDTO> list = sftpMonitorMapper.selectList(dto);
		List<SftpMonitorDTO> list = sftpMonitorMapper.selectChartList(dto);
		Double maxSize = 0.0;
		if(list != null && list.size() > 0) {
			for(SftpMonitorDTO obj : list) {
				if(obj.getReceivedFileSize()>maxSize){
					maxSize=obj.getReceivedFileSize();
				}
			}
		}
		transferUnit(list,getUnit(maxSize));
		List<String> legend = new ArrayList<String>();
		legend.add("接收文件总大小");
		legend.add("接收文件数");
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();
		
		List<Double> receivedFileSizeList = new ArrayList<Double>();
		List<Integer> receivedFileNumList = new ArrayList<Integer>();
		
		if(list != null && list.size() > 0) {
			for(SftpMonitorDTO obj : list) {
				timeSet.add(obj.getReceivedTime());
				receivedFileSizeList.add(obj.getReceivedFileSize());
				receivedFileNumList.add(obj.getReceivedFileNum());
			}
		}
		xAxis.addAll(timeSet);
		seriesList.add(new Series(legend.get(0), "bar", receivedFileSizeList));
		seriesList.add(new Series(legend.get(1), "line", receivedFileNumList, 1));
		DataZoom dataRoom = new DataZoom();
		dataRoom.setShow(true);
		dataRoom.setZoomLock(false);
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
		echarts.setUnit(getUnit(maxSize));
		return echarts;
	}

	public void transferUnit(List<SftpMonitorDTO> list, String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			String realSize = "";
			for(SftpMonitorDTO dto : list) {
				if(dto.getReceivedFileSize() != null) {
					Long ut = Long.parseLong("1024");
					if( "KB".equals(unit)) {
						realSize = decimalFormat.format(dto.getReceivedFileSize()/ut.longValue());
					}else if( "M".equals(unit)) {
						ut = Long.parseLong("1048576");
						realSize = decimalFormat.format((dto.getReceivedFileSize()/ut.longValue()));
					}else if( "G".equals(unit)) {
						ut=Long.parseLong("1073741824");
						realSize = decimalFormat.format((dto.getReceivedFileSize()/ut.longValue()));
					}else if( "T".equals(unit)) {
						ut=Long.parseLong("1099511627776");
						realSize = decimalFormat.format((dto.getReceivedFileSize()/ut.longValue()));
					}
				}
				if(realSize !="") {
					dto.setReceivedFileSize(Double.valueOf(realSize));
				}
			}
		}
	}

	public void transferUnitTable(List<SftpMonitorDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(SftpMonitorDTO dto : list) {
				if(dto.getReceivedFileSize() != null) {
					if( "T".equals(unit)) {
						dto.setReceivedFileSizeStr(decimalFormat.format((dto.getReceivedFileSize()/Long.parseLong("1099511627776")))+"T");
					}else if( "G".equals(unit)) {
						dto.setReceivedFileSizeStr(decimalFormat.format((dto.getReceivedFileSize()/Long.parseLong("1073741824")))+"G");
					}else if( "M".equals(unit)) {
						dto.setReceivedFileSizeStr(decimalFormat.format((dto.getReceivedFileSize()/Long.parseLong("1048576")))+"M");
					}else if( "KB".equals(unit)) {
						dto.setReceivedFileSizeStr(decimalFormat.format(dto.getReceivedFileSize()/Long.parseLong("1024"))+"KB");
					}
				}
			}
		}
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

	@Override
	public List<SftpMonitorDTO> getFileType() {
		return sftpMonitorMapper.selectFileType();
	}

}
