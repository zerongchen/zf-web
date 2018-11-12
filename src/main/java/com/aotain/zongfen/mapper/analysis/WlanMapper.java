package com.aotain.zongfen.mapper.analysis;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;

@MyBatisDao
public interface WlanMapper {

	public Integer getCountUser(Map<String,String> query);
	
	public List<String> getAcountList(Integer userGroup);
  
}