package com.aotain.zongfen.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.mapper.device.DpiStaticPortMapper;
import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.service.device.DpiStaticPortService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DpiStaticPortServiceImp implements DpiStaticPortService {
	
	@Autowired
	private DpiStaticPortMapper dpiStaticPortMapper;

	@Override
	public PageResult<DpiStaticPort> getList(Integer pageIndex, Integer pageSize, String deploysitename) {
		PageResult<DpiStaticPort> result = new PageResult<DpiStaticPort>(); 
		PageHelper.startPage(pageIndex, pageSize);
		List<DpiStaticPort> list = dpiStaticPortMapper.selectList(deploysitename);
		PageInfo<DpiStaticPort> pageResult = new PageInfo<DpiStaticPort>(list);//mLinkid=1000, mLinkdesc=bbbbb
		result.setRows(list);
		result.setTotal(pageResult.getTotal());
		return result;
	}

}
