package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.WebFlowUserGroup;

@MyBatisDao
public interface WebFlowUserGroupMapper {

	int insertList(List<WebFlowUserGroup> list);
	
	List<WebFlowUserGroup> getListById(Integer id);
	
	int deleteById(Integer id);
	
	int deleteEntity(List<WebFlowUserGroup> list);
}
