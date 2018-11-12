package com.aotain.zongfen.service.general.ipaddress.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.model.IpAddressArea;
import com.aotain.zongfen.mapper.general.IpAddressAreaMapper;
import com.aotain.zongfen.service.general.ipaddress.IpAddressAreaService;

@Service
public class IpAddressAreaServiceImpl implements IpAddressAreaService {
	
	@Autowired
	private IpAddressAreaMapper mapper;

	@Override
	public List<IpAddressArea> getByType(Byte areaType) {
		return mapper.selectByType(areaType);
	}

}
