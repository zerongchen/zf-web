package com.aotain.zongfen.service.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.zongfen.dto.common.PolicyStatusDto;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class PolicyStatusServiceImpl extends BaseService implements PolicyStatusService {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	@Autowired
	private PolicyStatusMapper policyStatusMapper;
	
    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;
	
	@Override
	public PageResult<PolicyStatusDto> getPolicyDetail(PolicyStatusDto record) {
		List<PolicyStatusDto> query = new ArrayList<PolicyStatusDto>();
		if(record.getSearchType()==1) {
			query.add(record);
			PageResult<PolicyStatusDto> result = new PageResult<PolicyStatusDto>();
			PageHelper.startPage(record.getPageIndex(), record.getPageSize());
			List<PolicyStatusDto> info = policyStatusMapper.getPolicyDetailList(query);
			PageInfo<PolicyStatusDto> pageResult = new PageInfo<PolicyStatusDto>(info);
			result.setTotal(pageResult.getTotal());
			result.setRows(info);
			return result;
		}else if(record.getSearchType()==2){
			UserPolicyBindStrategy bind = new UserPolicyBindStrategy();
        	bind.setUserBindMessageNo(record.getMessageNo());
        	bind.setUserBindMessageType(record.getMessageType());
        	List<UserPolicyBindStrategy> bindMessages = userPolicyBindMapper.getByBindMessages(bind);
			for(UserPolicyBindStrategy temp:bindMessages) {
				PolicyStatusDto tem = new PolicyStatusDto();
				tem.setStatus(record.getStatus());
				tem.setUserType(record.getUserType());
				tem.setMessageNo(temp.getMessageNo());
				tem.setSearchType(record.getSearchType());
				tem.setMessageType(MessageType.USER_POLICY_BIND.getId());
				query.add(tem);
			}
			if(query.size()>0) {
				PageResult<PolicyStatusDto> result = new PageResult<PolicyStatusDto>();
				PageHelper.startPage(record.getPageIndex(), record.getPageSize());
				List<PolicyStatusDto> info = policyStatusMapper.getPolicyDetailList(query);
				for(PolicyStatusDto dto: info) {
					dto.setBindMessageNo(record.getMessageNo());
				}
				PageInfo<PolicyStatusDto> pageResult = new PageInfo<PolicyStatusDto>(info);
				result.setTotal(pageResult.getTotal());
				result.setRows(info);
				return result;
			}
		}
		return null;
	}

	@Override
	public String policyResend(List<Long> messageNo,Integer messageType, List<String> ips) {
		boolean result = false;
		Set<Long> messageNos = new HashSet<Long>(messageNo);
		if(messageNo.size()>1) {
			for(Long temp:messageNos) {
				List<String> tempIps = new ArrayList<String>();
				for(int i=0;i<messageNo.size();i++) {
					if(temp.equals(messageNo.get(i))) {
						tempIps.add(ips.get(i));
					}
				}
				result = manualRetryPolicy(ProbeType.DPI.getValue(),messageType,temp,tempIps);
			}
		}else {
			result = manualRetryPolicy(ProbeType.DPI.getValue(),messageType,messageNo.get(0),ips);
		}
		if(result) {
			return null;
		}else {
			return "策略重发失败";
		}
		
	}

	@Override
	protected boolean addDb(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
