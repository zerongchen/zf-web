package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.WebFlowManage;

@MyBatisDao
public interface WebFlowManageMapper {
    int deleteByPrimaryKey(Integer webflowId);

    int insert(WebFlowManage record);
    
    int isSamePolicyName(WebFlowManage record);

    int updateByPrimaryKeySelective(WebFlowManage record);

    List<WebFlowManage> getIndexList(Map<String,Object> query);
    
    List<WebFlowManage> getRecordsByIds(Integer[] ids);
}