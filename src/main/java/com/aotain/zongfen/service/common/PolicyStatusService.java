package com.aotain.zongfen.service.common;

import java.util.List;

import com.aotain.zongfen.dto.common.PolicyStatusDto;
import com.aotain.zongfen.utils.PageResult;

public interface PolicyStatusService {
	
	public PageResult<PolicyStatusDto> getPolicyDetail(PolicyStatusDto record);
	
	public String policyResend(List<Long> messageNo,Integer messageType,List<String> ips);
	
}
