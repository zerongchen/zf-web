package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.AppFlowManagerUserGroup;

import java.util.List;

@MyBatisDao
public interface AppFlowManagerUserGroupMapper {
    int insert(AppFlowManagerUserGroup record);

    int insertSelective(AppFlowManagerUserGroup record);

    int insertSelectiveList(List<AppFlowManagerUserGroup> records);

    List<AppFlowManagerUserGroup> getAppGroup(AppFlowManagerUserGroup record);

    int delete(AppFlowManagerUserGroup record);

    int deleteList(List<AppFlowManagerUserGroup> records);

}