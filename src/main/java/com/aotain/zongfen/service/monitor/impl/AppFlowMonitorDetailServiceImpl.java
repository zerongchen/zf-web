package com.aotain.zongfen.service.monitor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.AppFlowMonitorDetailDTO;
import com.aotain.zongfen.mapper.monitor.AppFlowMonitorDetailMapper;
import com.aotain.zongfen.service.monitor.AppFlowMonitorDetailService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AppFlowMonitorDetailServiceImpl implements AppFlowMonitorDetailService {
	
	@Autowired
	private AppFlowMonitorDetailMapper appFlowMonitorDetailMapper;

	@Override
	public PageResult<AppFlowMonitorDetailDTO> getList(Integer pageIndex, Integer pageSize,
			AppFlowMonitorDetailDTO dto) {
		PageResult<AppFlowMonitorDetailDTO> result = new PageResult<AppFlowMonitorDetailDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<AppFlowMonitorDetailDTO> list = appFlowMonitorDetailMapper.selectList(dto);
		PageInfo<AppFlowMonitorDetailDTO> pageResult = new PageInfo<AppFlowMonitorDetailDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

}
