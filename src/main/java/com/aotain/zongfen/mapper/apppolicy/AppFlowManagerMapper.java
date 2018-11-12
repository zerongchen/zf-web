package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.apppolicy.FlowManagerDTO;
import com.aotain.zongfen.model.apppolicy.AppFlowManager;

import java.util.List;

@MyBatisDao
public interface AppFlowManagerMapper {
    int deleteByPrimaryKey(Long appflowId);

    int insertSelective(AppFlowManager record);

    AppFlowManager selectByPrimaryKey( Long appflowId);

    int updateByPrimaryKeySelective(AppFlowManager record);

    List<FlowManagerDTO> getList( FlowManagerDTO dto);

    int isSameName(AppFlowManager record);

}