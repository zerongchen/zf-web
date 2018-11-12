package com.aotain.zongfen.service.monitor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.HttpGetMonitorDetailDTO;
import com.aotain.zongfen.mapper.monitor.HttpGetMonitorDetailMapper;
import com.aotain.zongfen.service.monitor.HttpGetMonitorDetailService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class HttpGetMonitorDetailServiceImp implements HttpGetMonitorDetailService {

	@Autowired
	private HttpGetMonitorDetailMapper httpGetMonitorDetailMapper;
	
	@Override
	public PageResult<HttpGetMonitorDetailDTO> getList(Integer pageIndex, Integer pageSize,
			HttpGetMonitorDetailDTO dto) {
		PageResult<HttpGetMonitorDetailDTO> result = new PageResult<HttpGetMonitorDetailDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<HttpGetMonitorDetailDTO> list = httpGetMonitorDetailMapper.selectList(dto);
		PageInfo<HttpGetMonitorDetailDTO> pageResult = new PageInfo<HttpGetMonitorDetailDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

}
