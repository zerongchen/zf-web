package com.aotain.zongfen.mapper.general.userbind;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.model.general.user.UserGroup;
import com.aotain.zongfen.model.general.userbind.UserBindModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface UserBindMapper {

  //  int insertSelective(UserGroup record);

 //   UserGroup selectSingleUserGroup(UserGroup record);

    List<UserBindModel> getUserBindList(UserBindModel recode);

    List<Multiselect> getbindMessageName(@Param(value="bindMessageType") Long bindMessageType);

    UserPolicyBindStrategy getByBindMessageNoAndType(Map<String, Object> queryCondition);

    //  int deleteUserGroups(Long[] array);

  //  int updateUserGroupName(UserGroup recode);

   // Integer isSampleUserGroup(UserGroup recode);
}