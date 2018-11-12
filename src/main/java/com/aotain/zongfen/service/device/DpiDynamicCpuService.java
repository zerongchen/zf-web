package com.aotain.zongfen.service.device;

import java.util.List;

import com.aotain.zongfen.model.device.DpiDynamicCpu;
import com.aotain.zongfen.model.echarts.ECharts;

public interface DpiDynamicCpuService {
	
	public ECharts<Integer> getChartByCpu(Integer cpuNo);
	
	public List<DpiDynamicCpu> getCpuList();
	
}
