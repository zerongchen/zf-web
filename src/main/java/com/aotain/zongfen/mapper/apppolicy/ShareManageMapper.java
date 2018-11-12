package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.ShareManageStrategy;

@MyBatisDao
public interface ShareManageMapper {
    int deleteByPrimaryKey(Integer webflowId);

    int insert(ShareManageStrategy record);
    
    int isSamePolicyName(ShareManageStrategy record);
    
    int updateByPrimaryKeySelective(ShareManageStrategy record);

    List<ShareManageStrategy> getIndexList(Map<String,Object> query);
    
    List<ShareManageStrategy> getRecordsByIds(Integer[] ids);
}