package com.aotain.zongfen.service.device;

import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.utils.PageResult;

public interface DpiStaticPortService {
	
	public PageResult<DpiStaticPort> getList(Integer pageIndex,Integer pageSize, String deploysitename);
}
