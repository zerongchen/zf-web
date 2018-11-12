package com.aotain.zongfen.mapper.policy;

import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.policy.PolicyType;

@MyBatisDao
public interface PolicyTypeMapper {
    int insert(PolicyType record);

    int insertSelective(PolicyType record);

    Long getMaxMessageSequencenoByType(PolicyType record);
    
    PolicyType getPolicyByNoAndType(Map<String,Object> query);
}