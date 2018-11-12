package com.aotain.zongfen.service.general.ipaddress;

import java.util.List;

import com.aotain.common.policyapi.model.IpAddressArea;

public interface IpAddressAreaService {
	
	 List<IpAddressArea> getByType(Byte areaType);
}
