package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.WebFlowManageWebType;

@MyBatisDao
public interface WebFlowManageWebTypeMapper {
	int insertList(List<WebFlowManageWebType> list);
	
	List<WebFlowManageWebType> getListById(Integer id);
	
	int deleteById(Integer id);
}
