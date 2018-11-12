package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.apppolicy.VoipFlowDTO;
import com.aotain.zongfen.model.apppolicy.PolicyVoip;

@MyBatisDao
public interface PolicyVoipMapper {
    int deleteByPrimaryKey(Integer voipId);

    int insert(PolicyVoip record);

    int insertSelective(PolicyVoip record);

    PolicyVoip selectByPrimaryKey(Integer voipId);

    int updateByPrimaryKeySelective(PolicyVoip record);

    int updateByPrimaryKey(PolicyVoip record);
    
    List<VoipFlowDTO> getIndexList(Map<String,Object> query);
    
    List<PolicyVoip> getRecordsByMessageNo(String[] policyIds);
}