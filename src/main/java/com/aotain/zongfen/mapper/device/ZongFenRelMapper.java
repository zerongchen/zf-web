package com.aotain.zongfen.mapper.device;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.ZongFenRel;

@MyBatisDao
public interface ZongFenRelMapper {

	int insertList(List<ZongFenRel> record);
	
	int insert(ZongFenRel record);
	
	List<String> selectListById(Integer zongFenId);
	
	//根据综分设备ID删除
	int deleteByZfId(Integer deviceId);
	
	int updateByZfId(ZongFenRel record);
	
	int selectCountByIp(String ip);
}
