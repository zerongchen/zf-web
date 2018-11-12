package com.aotain.zongfen.service.common;

import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyType;

public interface PolicyService {

   /**
    * 添加策略到DB,生成messageNo
    * @param messageType
    * @param policy
    * @return
    */
   Long addPolicy( MessageType messageType, Policy policy) ;

   /**
    * 添加策略到DB，生成MessageSqNo
    * @param messageType
    * @param policyType
    * @return
    * @throws Exception
    */
   Long addPolicyType( MessageType messageType, PolicyType policyType) ;

   /**
    * 是否存在策略类型一致的策略名
    * @param record
    * @return
    */
   boolean isSamePolicyNameByType(Policy record);


}
