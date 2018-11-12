package com.aotain.zongfen.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyTypeMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyType;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyTypeMapper policyTypeMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;


    @Override
    public synchronized Long addPolicy( MessageType messageType, Policy policy ) {

            policy.setMessageType(messageType.getId());
            Long currentMessageNo = policyMapper.getMaxMessageNoByType(policy);
            currentMessageNo = currentMessageNo==null?1:currentMessageNo+1;
            policy.setMessageNo(currentMessageNo);
            policy.setOperateType(0);
            policyMapper.insertSelective(policy);
            return currentMessageNo;
            //MESSAGE_SEQUENCENO operate .....

    }

    @Override
    public Long addPolicyType( MessageType messageType, PolicyType policyType ) {
            policyType.setMessageType(messageType.getId());
            Long currentMessageSqNo = policyTypeMapper.getMaxMessageSequencenoByType(policyType);
            currentMessageSqNo = currentMessageSqNo==null?1:currentMessageSqNo+1;
            policyType.setMessageSequenceno(currentMessageSqNo);
            policyTypeMapper.insertSelective(policyType);
            return currentMessageSqNo;

    }

    @Override
    public boolean isSamePolicyNameByType(Policy record){
        return policyMapper.isSamePolicyNameByType(record)>0?true:false;
    }


}
