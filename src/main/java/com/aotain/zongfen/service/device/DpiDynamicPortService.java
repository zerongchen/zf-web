package com.aotain.zongfen.service.device;

import java.util.List;

import com.aotain.zongfen.model.device.DpiDynamicPort;
import com.aotain.zongfen.model.echarts.ECharts;

public interface DpiDynamicPortService {

	public ECharts<Integer> getChartByPort(DpiDynamicPort dpiDynamicPort);

	public List<DpiDynamicPort> getPortList();
}
