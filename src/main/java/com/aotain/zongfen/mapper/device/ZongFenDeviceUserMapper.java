package com.aotain.zongfen.mapper.device;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;

@MyBatisDao
public interface ZongFenDeviceUserMapper {

	int insert(ZongFenDeviceUser record);
	
	int insertList(List<ZongFenDeviceUser> list);
	
	int deleteByZfId(Integer deviceId);
}
