package com.aotain.zongfen.service.apppolicy.flowmark.impl;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.FlowSignStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.FlowSignStrategyMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.apppolicy.flowmark.IFlowMarkService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流量标记服务类
 *
 * @author daiyh@aotain.com
 * @date 2018/04/03
 */
@Service
public class FlowMarkServiceImpl extends BaseService implements IFlowMarkService{

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private FlowSignStrategyMapper flowSignStrategyMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private CommonService commonService;

    @Override
    public FlowSignStrategy selectByPrimaryKey(long messageNo) {
        return flowSignStrategyMapper.selectByPrimaryKey(messageNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> addFlowSignAndUserBindPolicy(FlowSignStrategy flowSignStrategy) {
    	List<Long> bindMessageNo = new ArrayList<Long>();
        addPolicy(flowSignStrategy);
        // 新增用户绑定策略
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByFlowSign(flowSignStrategy);
        for(int i=0;i<userPolicyBindStrategyList.size();i++){
            userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
            bindMessageNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
        }
        return bindMessageNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> modifyFlowSignAndUserBindPolicy(FlowSignStrategy flowSignStrategy) {
    	List<Long> bindMessageNo = new ArrayList<Long>();
        flowSignStrategy.setOperationType(OperationConstants.OPERATION_UPDATE);
        setPropertiesBeforeSaveOrUpdate(flowSignStrategy);

        modifyPolicy(flowSignStrategy);

        Map<String,Object> queryCondition = generateQueryConditionMap(flowSignStrategy);
        // 删除原有的绑定策略
        List<UserPolicyBindStrategy> existUserPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
        // 新增用户绑定策略
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByFlowSign(flowSignStrategy);

        if ( !CollectionUtils.isEqualCollection(existUserPolicyBindStrategyList,userPolicyBindStrategyList ) ){
            deleteExistBindPolicy(existUserPolicyBindStrategyList,flowSignStrategy);
            for(int i=0;i<userPolicyBindStrategyList.size();i++){
                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
                bindMessageNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
            }
        }
        return bindMessageNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> deletePolicys(List<FlowSignStrategy> flowSignStrategies) {
    	List<Long> bindMessageNo = new ArrayList<Long>();
        for (int i=0;i<flowSignStrategies.size();i++) {
            Map<String,Object> queryCondition = generateQueryConditionMap(flowSignStrategies.get(i));
            // 删除原有的绑定策略
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
            deleteExistBindPolicy(userPolicyBindStrategyList,flowSignStrategies.get(i));

            deletePolicy(flowSignStrategies.get(i));
            for(UserPolicyBindStrategy temp:userPolicyBindStrategyList) {
            	 bindMessageNo.add(temp.getMessageNo());
            }
        }
        return bindMessageNo;
    }

    @Override
    public boolean existSameNameRecord(FlowSignStrategy flowSignStrategy) {
        Policy record = clonePropertyFromFlowSign(flowSignStrategy);
        return policyMapper.isSamePolicyNameByType(record)>0;
    }

    @Override
    public List<FlowSignStrategy> listData(Map<String,Object> queryMap){
        List<FlowSignStrategy> result = flowSignStrategyMapper.listData(queryMap);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            FlowSignStrategy flowSignStrategy = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(flowSignStrategy.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(flowSignStrategy.getMessageNo());
                query2.setMessageType(MessageType.APP_FLOW_MARK.getId());
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                flowSignStrategy.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
                flowSignStrategy.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else {
                flowSignStrategy.setAppPolicy("0/0");
                flowSignStrategy.setBindPolicy("0/0");
            }
        }
        return result;
    }

    @Override
    protected boolean addDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_SAVE);
        setPropertiesBeforeSaveOrUpdate(policy);

        FlowSignStrategy flowSignStrategy = (FlowSignStrategy)policy;
        flowSignStrategyMapper.insertSelective(flowSignStrategy);
        // messageNo表
        Policy record = clonePropertyFromFlowSign(flowSignStrategy);
        policyMapper.insertSelective(record);

        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        setPropertiesBeforeSaveOrUpdate(policy);

        FlowSignStrategy flowSignStrategy = (FlowSignStrategy)policy;
        // messageNo表
        Policy record = clonePropertyFromFlowSign(flowSignStrategy);
        policyMapper.deletePolicyByMesNoAndType(record);

//        flowSignStrategyMapper.deleteData(flowSignStrategy);
        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_UPDATE);
        setPropertiesBeforeSaveOrUpdate(policy);

        FlowSignStrategy flowSignStrategy = (FlowSignStrategy)policy;
        flowSignStrategyMapper.updateSelective(flowSignStrategy);
        // messageNo表
        Policy record = clonePropertyFromFlowSign(flowSignStrategy);
        policyMapper.updatePolicyByMessageNoAndType(record);
        return true;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {
        return sendRedisMessage(policy);
    }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    @Override
    public List<Long> resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp){
    	List<Long> bindMessageNo = new ArrayList<Long>();
    	// 重发主策略
        manualRetryPolicy(topTaskId,0, MessageType.APP_FLOW_MARK.getId(),messageNo,dpiIp);
        // 重发关联的绑定策略
        if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageType.APP_FLOW_MARK.getId());
            queryMap.put("operateType", OperationConstants.OPERATION_DELETE);
            List<UserPolicyBindStrategy> userPolicyBindStrategies = userPolicyBindMapper.getByBindMessageNoAndType(queryMap);
            for(int i=0;i<userPolicyBindStrategies.size();i++){
                manualRetryPolicy(topTaskId,0, MessageType.USER_POLICY_BIND.getId(),
                        userPolicyBindStrategies.get(i).getMessageNo(),dpiIp);
                bindMessageNo.add(userPolicyBindStrategies.get(i).getMessageNo());
            }
        }
        return bindMessageNo;
    }

    private Policy clonePropertyFromFlowSign(FlowSignStrategy flowSignStrategy){
        Policy policy = new Policy();
        if (!StringUtils.isEmpty(flowSignStrategy.getMessageNo())){
            policy.setMessageNo(flowSignStrategy.getMessageNo());
        }
        if (!StringUtils.isEmpty(flowSignStrategy.getMessageSequenceNo())){
            policy.setMessageSequenceno(flowSignStrategy.getMessageSequenceNo());
        }
        if (!StringUtils.isEmpty(flowSignStrategy.getMessageType())){
            policy.setMessageType(flowSignStrategy.getMessageType());
        }
        if (!StringUtils.isEmpty(flowSignStrategy.getMessageName())){
            policy.setMessageName(flowSignStrategy.getMessageName());
        }
        if (!StringUtils.isEmpty(flowSignStrategy.getOperationType())){
            policy.setOperateType(flowSignStrategy.getOperationType());
        }
        if ( !StringUtils.isEmpty(flowSignStrategy.getCreateOper()) ){
            policy.setCreateOper(flowSignStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(flowSignStrategy.getModifyOper()) ){
            policy.setModifyOper(flowSignStrategy.getModifyOper());
        }
        if ( flowSignStrategy.getCreateTime() != null ){
            policy.setCreateTime(flowSignStrategy.getCreateTime());
        }
        if ( flowSignStrategy.getModifyTime() != null ){
            policy.setModifyTime(flowSignStrategy.getModifyTime());
        }
        return policy;
    }

    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByFlowSign(FlowSignStrategy flowSignStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (flowSignStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(flowSignStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (flowSignStrategy.getUserType()==1 || flowSignStrategy.getUserType()==2) {
            // 账号或者IP地址
            for (int i=0;i< flowSignStrategy.getUserName().size();i++){
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserName(flowSignStrategy.getUserName().get(i));
                copyProperties(flowSignStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        } else if (flowSignStrategy.getUserType()==3){
            // 用户组
            for (int i=0;i<flowSignStrategy.getUserGroupId().size();i++){
                long userGroupId = flowSignStrategy.getUserGroupId().get(i);
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserGroupId(userGroupId);
                userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                copyProperties(flowSignStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }

        }

        return userPolicyBindStrategyList;
    }

    /**
     * 赋值相关属性
     * @param flowSignStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(FlowSignStrategy flowSignStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(flowSignStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(flowSignStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(flowSignStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(flowSignStrategy.getMessageNo());
        bindMessage.setBindMessageType(flowSignStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !StringUtils.isEmpty(flowSignStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(flowSignStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(flowSignStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(flowSignStrategy.getModifyOper());
        }
        if ( flowSignStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(flowSignStrategy.getCreateTime());
        }
        if ( flowSignStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(flowSignStrategy.getModifyTime());
        }
    }

    /**
     * 生成对应绑定策略查询条件
     * @param flowSignStrategy
     * @return
     */
    private Map<String,Object> generateQueryConditionMap(FlowSignStrategy flowSignStrategy){
        Map<String,Object> queryCondition = Maps.newHashMap();
        queryCondition.put("messageNo",flowSignStrategy.getMessageNo());
        queryCondition.put("userBindMessageType",flowSignStrategy.getMessageType());
        queryCondition.put("operateType",OperationConstants.OPERATION_DELETE);
        return queryCondition;
    }

    /**
     * 删除原有的用户组绑定策略
     * @param userPolicyBindStrategyList
     * @param flowSignStrategy
     */
    private void deleteExistBindPolicy(List<UserPolicyBindStrategy> userPolicyBindStrategyList,FlowSignStrategy flowSignStrategy){
        flowSignStrategy.setMessageType(MessageType.APP_FLOW_MARK.getId());
        for (UserPolicyBindStrategy userPolicyBindStrategy : userPolicyBindStrategyList) {
            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(flowSignStrategy.getMessageNo());
            bindMessage.setBindMessageType(flowSignStrategy.getMessageType());
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            userPolicyBindService.deletePolicy(userPolicyBindStrategy);
        }
    }

    private void setPropertiesBeforeSaveOrUpdate(BaseVO baseVO){
        //设置一些公共属性
        baseVO.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageType.APP_FLOW_MARK.getId();
        baseVO.setMessageType(messageType);
        if (StringUtils.isEmpty(baseVO.getMessageNo())){
            baseVO.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        }
        baseVO.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));
    }
}
