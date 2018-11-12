package com.aotain.zongfen.mapper.policy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.common.PolicyStatusDto;
import com.aotain.zongfen.model.policy.PolicyStatus;

@MyBatisDao
public interface PolicyStatusMapper {
	
	PolicyStatus getCountTotalAndFail(PolicyStatus querys);
	
	PolicyStatus getCountFailForMain(PolicyStatus querys);
	
	List<PolicyStatusDto> getPolicyDetailList(List<PolicyStatusDto> query);
}