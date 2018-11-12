package com.aotain.zongfen.service.device.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.mapper.device.DpiStaticMapper;
import com.aotain.zongfen.model.device.DpiStatic;
import com.aotain.zongfen.service.device.DpiStaticService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DpiStaticServiceImpl implements DpiStaticService {
	
	@Autowired
	private DpiStaticMapper dpiStaticMapper;

	@Override
	public PageResult<DpiStatic> getList(Integer pageIndex,Integer pageSize, DpiStatic obj) {
		PageResult<DpiStatic> result = new PageResult<DpiStatic>();
		PageHelper.startPage(pageIndex, pageSize);	
		List<DpiStatic> list = dpiStaticMapper.selectList(obj);
		PageInfo<DpiStatic> pageResult = new PageInfo<DpiStatic>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public DpiStatic getByPK(String deploysitename) {
		return dpiStaticMapper.selectByPrimaryKey(deploysitename);
	}

}
