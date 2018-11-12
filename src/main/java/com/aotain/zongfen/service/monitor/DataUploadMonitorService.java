package com.aotain.zongfen.service.monitor;
import com.aotain.zongfen.dto.monitor.DataUploadMonitorDTO;

import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.PageResult;

import java.util.Map;

public interface DataUploadMonitorService {
		/**
		 * 分页列表
		 * @param pageIndex
		 * @param pageSize
		 * @param dto
		 * @return
		 */
		public PageResult<DataUploadMonitorDTO> getList(Integer pageIndex, Integer pageSize, DataUploadMonitorDTO dto);
		
		public Map<String,Object> getChart( DataUploadMonitorDTO dto);
		
	}


