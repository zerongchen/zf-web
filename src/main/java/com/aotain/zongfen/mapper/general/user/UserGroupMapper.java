package com.aotain.zongfen.mapper.general.user;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.user.UserGroup;

@MyBatisDao
public interface UserGroupMapper {

    int insertSelective(UserGroup record);

    UserGroup selectSingleUserGroup( UserGroup record);

    List<UserGroup> getUserGroupList(UserGroup recode);

    int deleteUserGroups(Long[] array);

    int updateUserGroupName(UserGroup recode);

    Integer isSampleUserGroup( UserGroup recode);

    Integer isSampleUserGroup2( UserGroup recode);
}