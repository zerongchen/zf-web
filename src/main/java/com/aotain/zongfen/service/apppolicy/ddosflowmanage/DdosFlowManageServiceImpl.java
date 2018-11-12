package com.aotain.zongfen.service.apppolicy.ddosflowmanage;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.DdosExceptFlowStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.policyapi.model.msg.DdosFlowAppAttachNormal;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;
import com.aotain.zongfen.mapper.apppolicy.DdosFlowManageMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author chenym@aotain.com
 * @date 2018/04/04
 */
@Service
public class DdosFlowManageServiceImpl extends BaseService implements DdosFlowManageService {

    private static Logger LOG = LoggerFactory.getLogger(DdosFlowManageServiceImpl.class);

    @Autowired
    private DdosFlowManageMapper ddosFlowManageMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;
    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    /**
     * 添加ddos流量管理策略和用户绑定策略
     * @return
     */

    @Override
    protected boolean addDb(BaseVO policy) {
        try {
            //设置probeType和operateType
            policy.setOperationType(OperationConstants.OPERATION_SAVE);
            policy.setProbeType(ProbeType.DPI.getValue());
            // 设置messageType messageNo messageSeqNo
            int messageType = MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW;
            policy.setMessageType(messageType);
            policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
            policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));
            DdosExceptFlowStrategy ddosExceptFlowStrategy = (DdosExceptFlowStrategy)policy;
            for(int i=0;i<ddosExceptFlowStrategy.getAppAttachNormal().size();i++){
                Map<String,Integer> ddosMap = new HashMap<>();
                ddosMap.put("ddosId",null);
                ddosMap.put("messageNo",ddosExceptFlowStrategy.getMessageNo().intValue());
                ddosMap.put("appAttackType",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAppAttackType());
                ddosMap.put("attackThreshold",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAttackThreshold());
                ddosMap.put("attackControl",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAttackControl());
                ddosFlowManageMapper.insertSelective(ddosMap);
            }
            Policy policyNo = createPolicyBeanByBaseVo(ddosExceptFlowStrategy);
            policyMapper.insert(policyNo);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {

        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW;
        policy.setMessageType(messageType);
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        DdosExceptFlowStrategy ddosFlowStrategy = (DdosExceptFlowStrategy)policy;

     //   ddosFlowManageMapper.deleteData(ddosFlowStrategy);
        Policy policyNo = createPolicyBeanByBaseVo2(ddosFlowStrategy);
        policyMapper.updatePolicyByMessageNoAndType(policyNo);
        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        try {
            //设置probeType和operateType
            policy.setOperationType(OperationConstants.OPERATION_UPDATE);
            policy.setProbeType(ProbeType.DPI.getValue());
            // 设置messageType messageSeqNo
            int messageType = MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW;
            policy.setMessageType(messageType);
            policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

            DdosExceptFlowStrategy ddosExceptFlowStrategy = (DdosExceptFlowStrategy)policy;
            ddosFlowManageMapper.deleteData(ddosExceptFlowStrategy);

            for(int i=0;i<ddosExceptFlowStrategy.getAppAttachNormal().size();i++){
                Map<String,Integer> ddosMap = new HashMap<>();
                ddosMap.put("ddosId",null);
                ddosMap.put("messageNo",ddosExceptFlowStrategy.getMessageNo().intValue());
                ddosMap.put("appAttackType",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAppAttackType());
                ddosMap.put("attackThreshold",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAttackThreshold());
                ddosMap.put("attackControl",ddosExceptFlowStrategy.getAppAttachNormal().get(i).getAttackControl());
                ddosFlowManageMapper.insertSelective(ddosMap);
            }
            Policy policyNo = createPolicyBeanByBaseVo(ddosExceptFlowStrategy);
            policyMapper.updatePolicyByMessageNoAndType(policyNo);
        } catch (Exception e) {
            LOG.error("ddosflow service update error,",e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {return sendRedisMessage(policy); }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    @Override
    public boolean modifyDdosFlowManageAndUserBindPolicy(DdosExceptFlowStrategy ddosExceptFlowStrategy) {
        ddosExceptFlowStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW);

        Map<String,Object> queryCondition = generateQueryConditionMap(ddosExceptFlowStrategy);
        // 删除原有的绑定策略
        List<UserPolicyBindStrategy> existUserPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);

        modifyPolicy(ddosExceptFlowStrategy);
        /*deleteExistBindPolicy(existUserPolicyBindStrategyList,ddosExceptFlowStrategy,true);
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(ddosExceptFlowStrategy);
        if(userPolicyBindStrategyList!=null&&userPolicyBindStrategyList.size()>0){
            for(int i=0;i<userPolicyBindStrategyList.size();i++){
                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
            }
        }*/
        return true;
    }

    /**
     * 添加ddos流量管理策略和用户绑定策略
     * @return
     */
    public long addddosFlowPolicyAndUserBindPolicy(DdosExceptFlowStrategy ddosExceptFlowStrategy){
        addPolicy(ddosExceptFlowStrategy);
        /*List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(ddosExceptFlowStrategy);
        for(int i=0;i<userPolicyBindStrategyList.size();i++){
            userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
        }*/
        return ddosExceptFlowStrategy.getMessageNo();
    }
    /**
     * 根据DdosExceptFlowStrategy相关值确定用户应用绑定策略list
     * @param ddosExceptFlowStrategy
     * @return
     */
    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByBaseVo(DdosExceptFlowStrategy ddosExceptFlowStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (ddosExceptFlowStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(ddosExceptFlowStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        }

        else if (ddosExceptFlowStrategy.getUserType()==1 || ddosExceptFlowStrategy.getUserType()==2) {
            // 账号或者IP地址
            for (int i=0;i< ddosExceptFlowStrategy.getUserName().size();i++){
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserName(ddosExceptFlowStrategy.getUserName().get(i));
                copyProperties(ddosExceptFlowStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        } else if (ddosExceptFlowStrategy.getUserType()==3){
            // 用户组
            for (int i=0;i<ddosExceptFlowStrategy.getUserGroupId().size();i++){
                long userGroupId = Long.valueOf(ddosExceptFlowStrategy.getUserGroupId().get(i));
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserGroupId(userGroupId);
                userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                copyProperties(ddosExceptFlowStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        }
        return userPolicyBindStrategyList;
    }

    /**
     * 赋值相关属性
     * @param ddosExceptFlowStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(DdosExceptFlowStrategy ddosExceptFlowStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(ddosExceptFlowStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(ddosExceptFlowStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(ddosExceptFlowStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(ddosExceptFlowStrategy.getMessageNo());
        bindMessage.setBindMessageType(ddosExceptFlowStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !StringUtils.isEmpty(ddosExceptFlowStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(ddosExceptFlowStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(ddosExceptFlowStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(ddosExceptFlowStrategy.getModifyOper());
        }
        if ( ddosExceptFlowStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(ddosExceptFlowStrategy.getCreateTime());
        }
        if ( ddosExceptFlowStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(ddosExceptFlowStrategy.getModifyTime());
        }
    }

    /**
     * 根据DdosExceptFlowStrategy实体构造Policy实体
     * @param ddosExceptFlowStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo(DdosExceptFlowStrategy ddosExceptFlowStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(ddosExceptFlowStrategy.getMessageNo());
        policyNo.setMessageSequenceno(ddosExceptFlowStrategy.getMessageSequenceNo());
        policyNo.setMessageType(ddosExceptFlowStrategy.getMessageType());
        policyNo.setMessageName(ddosExceptFlowStrategy.getMessageName());
        policyNo.setOperateType(ddosExceptFlowStrategy.getOperationType());
        if ( !StringUtils.isEmpty(ddosExceptFlowStrategy.getCreateOper()) ){
            policyNo.setCreateOper(ddosExceptFlowStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(ddosExceptFlowStrategy.getModifyOper()) ){
            policyNo.setModifyOper(ddosExceptFlowStrategy.getModifyOper());
        }
        if ( ddosExceptFlowStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(ddosExceptFlowStrategy.getCreateTime());
        }
        if ( ddosExceptFlowStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(ddosExceptFlowStrategy.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 根据DdosExceptFlowStrategy实体构造Policy实体
     * @param ddosFlowStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo2(DdosExceptFlowStrategy ddosFlowStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(ddosFlowStrategy.getMessageNo());
        policyNo.setMessageSequenceno(ddosFlowStrategy.getMessageSequenceNo());
        policyNo.setMessageType(ddosFlowStrategy.getMessageType());
        policyNo.setMessageName(ddosFlowStrategy.getMessageName());
        policyNo.setOperateType(ddosFlowStrategy.getOperationType());
        if ( !StringUtils.isEmpty(ddosFlowStrategy.getCreateOper()) ){
            policyNo.setCreateOper(ddosFlowStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(ddosFlowStrategy.getModifyOper()) ){
            policyNo.setModifyOper(ddosFlowStrategy.getModifyOper());
        }
        if ( ddosFlowStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(ddosFlowStrategy.getCreateTime());
        }
        if ( ddosFlowStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(ddosFlowStrategy.getModifyTime());
        }
        return policyNo;
    }


    @Override
    public List<DdosExceptFlowStrategyPo> listData(DdosExceptFlowStrategyPo page) {
        List<DdosExceptFlowStrategyPo> result = ddosFlowManageMapper.listData(page);

        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            DdosExceptFlowStrategyPo po = result.get(i);
            PolicyStatus ddosPolicy = new PolicyStatus();
            if(po.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(po.getMessageNo());
                query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW);
                ddosPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(ddosPolicy!=null) {
                po.setDdosPolicy(ddosPolicy.getMainCount() == null ? "0/0" : ddosPolicy.getMainCount());
                po.setBindPolicy(ddosPolicy.getBindCount() == null ? "0/0" : ddosPolicy.getBindCount());
            }else {
                po.setDdosPolicy("0/0");
                po.setBindPolicy("0/0");
            }
        }
        return result;
    }

    @Override
    public DdosExceptFlowStrategy selectByPrimaryKey(DdosExceptFlowStrategy ddosFlowStrategy) {
        return ddosFlowManageMapper.selectByPrimaryKey(ddosFlowStrategy);
    }

    @Override
    public boolean deletePolicys(List<DdosExceptFlowStrategy> ddosFlowStrategies) {

        for (int i=0;i<ddosFlowStrategies.size();i++) {
            Map<String,Object> queryCondition = generateQueryConditionMap(ddosFlowStrategies.get(i));
            // 删除原有的绑定策略
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
            deleteExistBindPolicy(userPolicyBindStrategyList,ddosFlowStrategies.get(i),true);
            // 删除本策略
            DdosExceptFlowStrategy policyA = ddosFlowStrategies.get(i);
            List<DdosFlowAppAttachNormal> list = ddosFlowManageMapper.selectByMessage(policyA);
            policyA.setAppAttachNormal(list);
            deletePolicy(policyA);
        }
        return true;
    }
    private Map<String,Object> generateQueryConditionMap(DdosExceptFlowStrategy ddosFlowStrategy){
        Map<String,Object> queryCondition = Maps.newHashMap();
        queryCondition.put("messageNo",ddosFlowStrategy.getMessageNo());
        if (ddosFlowStrategy.getMessageType()==null){
            queryCondition.put("userBindMessageType", MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId());
        } else {
            queryCondition.put("userBindMessageType",ddosFlowStrategy.getMessageType());
        }
        queryCondition.put("operateType",OperationConstants.OPERATION_DELETE);
        return queryCondition;
    }

    /**
     *
     * @param userPolicyBindStrategyList
     * @param ddosFlowStrategy
     * @param sendStrategyChannel 是否发送通道
     */
    private void deleteExistBindPolicy(List<UserPolicyBindStrategy> userPolicyBindStrategyList,DdosExceptFlowStrategy ddosFlowStrategy,boolean sendStrategyChannel){

        ddosFlowStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_DDOS_EXCEPT_FLOW);

        for (UserPolicyBindStrategy userPolicyBindStrategy : userPolicyBindStrategyList) {
            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(ddosFlowStrategy.getMessageNo());
            bindMessage.setBindMessageType(ddosFlowStrategy.getMessageType());
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            if (sendStrategyChannel) {
                userPolicyBindService.deletePolicy(userPolicyBindStrategy);
            } else {
                userPolicyBindService.deleteDb(userPolicyBindStrategy);
            }
        }
    }

    @Override
    public boolean resendPolicy(long topTaskId, long messageNo, boolean needSendBindPolicy, List<String> dpiIp) {
        // 重发主策略
        manualRetryPolicy(topTaskId,0,MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId(),messageNo,dpiIp);
        // 重发关联的绑定策略
        /*if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId());
            queryMap.put("operateType",OperationConstants.OPERATION_DELETE);
            List<UserPolicyBindStrategy> userPolicyBindStrategies = userPolicyBindMapper.getByBindMessageNoAndType(queryMap);
            for(int i=0;i<userPolicyBindStrategies.size();i++){
                manualRetryPolicy(topTaskId,0,MessageType.USER_POLICY_BIND.getId(),
                        userPolicyBindStrategies.get(i).getMessageNo(),dpiIp);
            }
        }*/
        return true;
    }


}
