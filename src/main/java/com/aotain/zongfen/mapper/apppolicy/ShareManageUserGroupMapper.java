package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.ShareManageUserGroup;

@MyBatisDao
public interface ShareManageUserGroupMapper {

	int insertList(List<ShareManageUserGroup> list);
	
	List<ShareManageUserGroup> getListById(Integer id);
	
	int deleteById(Integer id);
	
	int deleteEntity(List<ShareManageUserGroup> list);
}
