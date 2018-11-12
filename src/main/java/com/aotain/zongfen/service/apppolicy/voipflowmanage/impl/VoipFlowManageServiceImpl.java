package com.aotain.zongfen.service.apppolicy.voipflowmanage.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.VoipFlowManageUserGroup;
import com.aotain.common.policyapi.model.VoipFlowStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.policyapi.model.msg.VoipFlowManageIp;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.VoipFlowManageIpMapper;
import com.aotain.zongfen.mapper.apppolicy.VoipFlowManageUserGroupMapper;
import com.aotain.zongfen.mapper.apppolicy.VoipIpManageMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.apppolicy.voipflowmanage.IVoipFlowManageService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.google.common.collect.Maps;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/15
 */
@Service
public class VoipFlowManageServiceImpl extends BaseService implements IVoipFlowManageService{

    private static Logger logger = LoggerFactory.getLogger(VoipFlowManageServiceImpl.class);

    /** 网关 */
    private static Integer voipGwType = 0;
    /** 网守 */
    private static Integer voipGwKeeperType = 1;

    @Autowired
    private VoipIpManageMapper voipIpManageMapper;

    @Autowired
    private VoipFlowManageIpMapper voipFlowManageIpMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private VoipFlowManageUserGroupMapper voipFlowManageUserGroupMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addDb(BaseVO policy){

        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_SAVE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_VOIP_FLOW_POLICY;
        policy.setMessageType(messageType);
//        policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
//        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        VoipFlowStrategy voipFlowStrategy = (VoipFlowStrategy)policy;

        boolean inValidityPeriod = inValidityPeriod(voipFlowStrategy);
        if (inValidityPeriod){
            policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
            policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));
            Policy policyNo = createPolicyBeanByBaseVo(voipFlowStrategy);
            policyMapper.insert(policyNo);

        }

        voipIpManageMapper.insertSelective(voipFlowStrategy);
        setVoipFlowManageIp(voipFlowStrategy);

        for (int i=0;i<voipFlowStrategy.getVoipGwIpList().size();i++){
            voipFlowManageIpMapper.insertSelective(voipFlowStrategy.getVoipGwIpList().get(i));
        }

        for (int i=0;i<voipFlowStrategy.getVoipGwKeeperIpList().size();i++){
            voipFlowManageIpMapper.insertSelective(voipFlowStrategy.getVoipGwKeeperIpList().get(i));
        }

        List<VoipFlowManageUserGroup> voipFlowManageUserGroups = createVoipFlowManageUserGroup(voipFlowStrategy);
        for (int i=0;i<voipFlowManageUserGroups.size();i++){
            voipFlowManageUserGroupMapper.insertSelective(voipFlowManageUserGroups.get(i));
        }

//        Policy policyNo = createPolicyBeanByBaseVo(voipFlowStrategy);
//        policyMapper.insert(policyNo);

        return true;

    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_UPDATE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_VOIP_FLOW_POLICY;
        policy.setMessageType(messageType);
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        VoipFlowStrategy voipFlowStrategy = (VoipFlowStrategy)policy;

        setVoipFlowManageIp(voipFlowStrategy);

        // 删除然后新增zf_v2_voipflow_manage_ip
        voipFlowManageIpMapper.deleteByVoipFlowId(voipFlowStrategy.getVoipFlowId());
        for (int i=0;i<voipFlowStrategy.getVoipGwIpList().size();i++){
            voipFlowManageIpMapper.insertSelective(voipFlowStrategy.getVoipGwIpList().get(i));
        }

        for (int i=0;i<voipFlowStrategy.getVoipGwKeeperIpList().size();i++){
            voipFlowManageIpMapper.insertSelective(voipFlowStrategy.getVoipGwKeeperIpList().get(i));
        }

        // 删除zf_v2_voipflow_manage_usergroup
        voipFlowManageUserGroupMapper.deleteByVoipFlowId(voipFlowStrategy.getVoipFlowId());
        List<VoipFlowManageUserGroup> voipFlowManageUserGroups = createVoipFlowManageUserGroup(voipFlowStrategy);
        for (int i=0;i<voipFlowManageUserGroups.size();i++){
            voipFlowManageUserGroupMapper.insertSelective(voipFlowManageUserGroups.get(i));
        }

        // 修改zf_v2_voipflow_manage
        voipIpManageMapper.updateByPrimaryKey(voipFlowStrategy);

        if (inValidityPeriod(voipFlowStrategy)){
            Policy policyNo = createPolicyBeanByBaseVo(voipFlowStrategy);
            policyMapper.updatePolicyByMessageNoAndType(policyNo);
        }
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_VOIP_FLOW_POLICY;
        policy.setMessageType(messageType);
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        VoipFlowStrategy voipFlowStrategy = (VoipFlowStrategy)policy;

        // 删除zf_v2_voipflow_manage_ip
//        voipFlowManageIpMapper.deleteByVoipFlowId(voipFlowStrategy.getVoipFlowId());
        // 删除zf_v2_voipflow_manage_usergroup
//        voipFlowManageUserGroupMapper.deleteByVoipFlowId(voipFlowStrategy.getVoipFlowId());
        // 删除zf_v2_voipflow_manage
        voipIpManageMapper.deleteData(voipFlowStrategy);

        if (inValidityPeriod(voipFlowStrategy)){
            Policy policyNo = createPolicyBeanByBaseVo(voipFlowStrategy);
            policyMapper.updatePolicyByMessageNoAndType(policyNo);
        }

        return true;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {
        VoipFlowStrategy voipFlowStrategy = (VoipFlowStrategy)policy;
        // 不是当前日期发送
        if (voipFlowStrategy.getRStartTime() > getTodayTimeStamp()){
            // 是否成功标志
            boolean success = setPolicyOperateSequenceToRedis(policy);
            if (!success) {
                logger.error("setPolicyOperateSequenceToRedis failed..."+policy.objectToJson());
                return false;
            }
            // 是否成功标志
            success = setPolicyToRedisTask(policy);
            if(!success) {
                logger.error("setPolicyToRedisTask failed..."+policy.objectToJson());
                return false;
            }
            return true;
        } else {
            return sendRedisMessage(policy);
        }

    }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return setPolicyOperateSequenceToRedis(policy);
    }

    /**
     * 根据参数生成对应的FlowManageIp实体
     * @param voipFlowStrategy
     */
    private void setVoipFlowManageIp(VoipFlowStrategy voipFlowStrategy){
        List<VoipFlowManageIp> voipGwList = new ArrayList<>();
        List<VoipFlowManageIp> voipGwKeeperList = new ArrayList<>();
        String gwIp = voipFlowStrategy.getGwIp();
        String gwKeeperIp = voipFlowStrategy.getGwKeeperIp();
        //网关集合
        List<String> gwIps = Arrays.asList(gwIp.split(","));
        for (int i=0;i<gwIps.size();i++){
            if ( !StringUtils.isEmpty(gwIps.get(i)) ){
                VoipFlowManageIp voipFlowManageIp = new VoipFlowManageIp();
                voipFlowManageIp.setVoipflowId(voipFlowStrategy.getVoipFlowId());
                voipFlowManageIp.setVoipIp(gwIps.get(i));
                voipFlowManageIp.setVoipType(voipGwType);
                voipGwList.add(voipFlowManageIp);
            }
        }

        //网守集合
        List<String> gwKeeperIps = Arrays.asList(gwKeeperIp.split(","));
        for (int i=0;i<gwKeeperIps.size();i++){
            if ( !StringUtils.isEmpty(gwKeeperIps.get(i)) ){
                VoipFlowManageIp voipFlowManageIp = new VoipFlowManageIp();
                voipFlowManageIp.setVoipflowId(voipFlowStrategy.getVoipFlowId());
                voipFlowManageIp.setVoipIp(gwKeeperIps.get(i));
                voipFlowManageIp.setVoipType(voipGwKeeperType);
                voipGwKeeperList.add(voipFlowManageIp);
            }
        }
        voipFlowStrategy.setVoipGwIpList(voipGwList);
        voipFlowStrategy.setVoipGwKeeperIpList(voipGwKeeperList);
    }

    /**
     * 根据voipFlowStrategy实体构造Policy实体
     * @param voipFlowStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo(VoipFlowStrategy voipFlowStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(voipFlowStrategy.getMessageNo());
        policyNo.setMessageSequenceno(voipFlowStrategy.getMessageSequenceNo());
        policyNo.setMessageType(voipFlowStrategy.getMessageType());
        policyNo.setMessageName(voipFlowStrategy.getMessageName());
        policyNo.setOperateType(voipFlowStrategy.getOperationType());
        if ( !StringUtils.isEmpty(voipFlowStrategy.getCreateOper()) ){
            policyNo.setCreateOper(voipFlowStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(voipFlowStrategy.getModifyOper()) ){
            policyNo.setModifyOper(voipFlowStrategy.getModifyOper());
        }
        if ( voipFlowStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(voipFlowStrategy.getCreateTime());
        }
        if ( voipFlowStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(voipFlowStrategy.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 创建关联的实体bean
     * @param voipFlowStrategy
     * @return
     */
    private List<VoipFlowManageUserGroup> createVoipFlowManageUserGroup(VoipFlowStrategy voipFlowStrategy){
        List<VoipFlowManageUserGroup> voipFlowManageUserGroups = new ArrayList<>();
        if (voipFlowStrategy.getUserType()==0){
            VoipFlowManageUserGroup voipFlowManageUserGroup = new VoipFlowManageUserGroup();
            voipFlowManageUserGroup.setUserType(voipFlowStrategy.getUserType());
            voipFlowManageUserGroup.setVoipflowId(voipFlowStrategy.getVoipFlowId());
            voipFlowManageUserGroups.add(voipFlowManageUserGroup);

        } else if (voipFlowStrategy.getUserType()==1||voipFlowStrategy.getUserType()==2){
            List<String> userNames = Arrays.asList(voipFlowStrategy.getUserNames().split(","));
            for (int i=0;i<userNames.size();i++){
                if ( !StringUtils.isEmpty(userNames.get(i)) ){
                    VoipFlowManageUserGroup voipFlowManageUserGroup = new VoipFlowManageUserGroup();
                    voipFlowManageUserGroup.setUserType(voipFlowStrategy.getUserType());
                    voipFlowManageUserGroup.setVoipflowId(voipFlowStrategy.getVoipFlowId());
                    voipFlowManageUserGroup.setUserName(userNames.get(i));
                    voipFlowManageUserGroups.add(voipFlowManageUserGroup);

                }
            }

        } else if (voipFlowStrategy.getUserType()==3){
            List<String> userGroups = Arrays.asList(voipFlowStrategy.getUserGroupIds().split(","));
            for (int i=0;i<userGroups.size();i++){
                if ( !StringUtils.isEmpty(userGroups.get(i)) ){
                    long userGroupId = Integer.valueOf(userGroups.get(i));
                    VoipFlowManageUserGroup voipFlowManageUserGroup = new VoipFlowManageUserGroup();
                    voipFlowManageUserGroup.setUserType(voipFlowStrategy.getUserType());
                    voipFlowManageUserGroup.setVoipflowId(voipFlowStrategy.getVoipFlowId());
                    voipFlowManageUserGroup.setUserGroupId(userGroupId);
                    voipFlowManageUserGroup.setUserName(commonService.getUserGroupName(userGroupId));
                    voipFlowManageUserGroups.add(voipFlowManageUserGroup);
                }
            }

        }
        return voipFlowManageUserGroups;
    }

    /**
     * 添加指定应用用户策略和用户绑定策略
     * @param voipFlowStrategy
     * @return
     */
    @Override
    public boolean addVoipFlowManageAndUserBindPolicy(VoipFlowStrategy voipFlowStrategy){
        boolean inValidityPeriod = inValidityPeriod(voipFlowStrategy);
        if (inValidityPeriod) {
            addPolicy(voipFlowStrategy);
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(voipFlowStrategy);
            for(int i=0;i<userPolicyBindStrategyList.size();i++){
                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
            }
        } else {
            addDb(voipFlowStrategy);
//            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(voipFlowStrategy);
//            for(int i=0;i<userPolicyBindStrategyList.size();i++){
//                userPolicyBindService.addDb(userPolicyBindStrategyList.get(i));
//            }
        }

        return true;
    }

    @Override
    public boolean modifyVoipFlowManageAndUserBindPolicy(VoipFlowStrategy voipFlowStrategy){
        voipFlowStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_VOIP_FLOW_POLICY);

        Map<String,Object> queryCondition = generateQueryConditionMap(voipFlowStrategy);
        // 删除原有的绑定策略
        List<UserPolicyBindStrategy> existUserPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);

        boolean inValidityPeriod = inValidityPeriod(voipFlowStrategy);
        if (inValidityPeriod) {
            modifyPolicy(voipFlowStrategy);
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(voipFlowStrategy);

            if ( !CollectionUtils.isEqualCollection(existUserPolicyBindStrategyList,userPolicyBindStrategyList)){
                deleteExistBindPolicy(existUserPolicyBindStrategyList,voipFlowStrategy,true);
                for(int i=0;i<userPolicyBindStrategyList.size();i++){
                    userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
                }
            }
        } else {
            modifyDb(voipFlowStrategy);

        }

        return true;
    }

    @Override
    public List<VoipFlowStrategy> listData(Page<VoipFlowStrategy> page){
        List<VoipFlowStrategy> result =  voipIpManageMapper.listData(page);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            VoipFlowStrategy voipFlowStrategy = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(voipFlowStrategy.getMessageNo()!=null) {
            	PolicyStatus query2 = new PolicyStatus();
            	query2.setMessageNo(voipFlowStrategy.getMessageNo());
            	query2.setMessageType(MessageType.VOIP_FLOW_POLICY.getId());
            	appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
	            voipFlowStrategy.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
	            voipFlowStrategy.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else {
            	voipFlowStrategy.setAppPolicy("0/0");
 	            voipFlowStrategy.setBindPolicy("0/0");
            }
           }
        return result;
    }

    @Override
    public VoipFlowStrategy selectByPrimaryKey(VoipFlowStrategy voipFlowStrategy){
        VoipFlowStrategy result = voipIpManageMapper.selectByPrimaryKey(voipFlowStrategy);
        setVoipFlowManageIp(result);
        return result;
    }

    @Override
    public boolean deletePolicys(List<VoipFlowStrategy> voipFlowStrategies){
        for (int i=0;i<voipFlowStrategies.size();i++) {
            Map<String,Object> queryCondition = generateQueryConditionMap(voipFlowStrategies.get(i));
            // 删除原有的绑定策略
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
            deleteExistBindPolicy(userPolicyBindStrategyList,voipFlowStrategies.get(i),true);
            // 删除本策略
            deletePolicy(voipFlowStrategies.get(i));
        }
        return true;
    }

    @Override
    public boolean existSameNameRecord(VoipFlowStrategy voipFlowStrategies){
        return voipIpManageMapper.existSameNameRecord(voipFlowStrategies)>0?true:false;
    }

    @Override
    public List<Long> resendPolicy(long topTaskId,long messageNo,boolean needSendBindPolicy, List<String> dpiIp){
    	List<Long> bindMessageNo = new ArrayList<Long>();
    	// 重发主策略
        manualRetryPolicy(topTaskId,0,MessageType.VOIP_FLOW_POLICY.getId(),messageNo,dpiIp);
        // 重发关联的绑定策略
        if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageType.VOIP_FLOW_POLICY.getId());
            queryMap.put("operateType",OperationConstants.OPERATION_DELETE);
            List<UserPolicyBindStrategy> userPolicyBindStrategies = userPolicyBindMapper.getByBindMessageNoAndType(queryMap);
            for(int i=0;i<userPolicyBindStrategies.size();i++){
                manualRetryPolicy(topTaskId,0,MessageType.USER_POLICY_BIND.getId(),
                        userPolicyBindStrategies.get(i).getMessageNo(),dpiIp);
                bindMessageNo.add(userPolicyBindStrategies.get(i).getMessageNo());
            }
        }
        return bindMessageNo;
    }



    /**
     *
     * @param userPolicyBindStrategyList
     * @param voipFlowStrategy
     * @param sendStrategyChannel 是否发送通道
     */
    private void deleteExistBindPolicy(List<UserPolicyBindStrategy> userPolicyBindStrategyList,VoipFlowStrategy voipFlowStrategy,boolean sendStrategyChannel){

        voipFlowStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_VOIP_FLOW_POLICY);

        for (UserPolicyBindStrategy userPolicyBindStrategy : userPolicyBindStrategyList) {
            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(voipFlowStrategy.getMessageNo());
            bindMessage.setBindMessageType(voipFlowStrategy.getMessageType());
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            if (sendStrategyChannel) {
                userPolicyBindService.deletePolicy(userPolicyBindStrategy);
            } else {
                userPolicyBindService.deleteDb(userPolicyBindStrategy);
            }

        }
    }

    /**
     * 根据AppUserStrategy相关值确定用户应用绑定策略list
     * @param voipFlowStrategy
     * @return
     */
    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByBaseVo(VoipFlowStrategy voipFlowStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (voipFlowStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(voipFlowStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (voipFlowStrategy.getUserType()==1 || voipFlowStrategy.getUserType()==2) {
            // 用户组
            List<String> userNames = Arrays.asList(voipFlowStrategy.getUserNames().split(","));
            for (int i=0;i<userNames.size();i++) {
                // 账号或者IP地址
                if (!StringUtils.isEmpty(userNames.get(i))) {
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserName(userNames.get(i));
                    copyProperties(voipFlowStrategy, userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }
        } else if (voipFlowStrategy.getUserType()==3){
            // 用户组
            List<String> userGroups = Arrays.asList(voipFlowStrategy.getUserGroupIds().split(","));
            for (int i=0;i<userGroups.size();i++){
                if ( !StringUtils.isEmpty(userGroups.get(i)) ){
                    long userGroupId = Integer.valueOf(userGroups.get(i));
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserGroupId(userGroupId);
                    userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                    copyProperties(voipFlowStrategy,userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }

        }

        return userPolicyBindStrategyList;

    }

    /**
     * 赋值相关属性
     * @param voipFlowStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(VoipFlowStrategy voipFlowStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(voipFlowStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(voipFlowStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(voipFlowStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(voipFlowStrategy.getMessageNo());
        bindMessage.setBindMessageType(voipFlowStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !StringUtils.isEmpty(voipFlowStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(voipFlowStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(voipFlowStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(voipFlowStrategy.getModifyOper());
        }
        if ( voipFlowStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(voipFlowStrategy.getCreateTime());
        }
        if ( voipFlowStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(voipFlowStrategy.getModifyTime());
        }
    }

    /**
     * 生成对应绑定策略查询条件
     * @param voipFlowStrategy
     * @return
     */
    private Map<String,Object> generateQueryConditionMap(VoipFlowStrategy voipFlowStrategy){
        Map<String,Object> queryCondition = Maps.newHashMap();
        queryCondition.put("messageNo",voipFlowStrategy.getMessageNo());
        if (voipFlowStrategy.getMessageType()==null){
            queryCondition.put("userBindMessageType",MessageType.VOIP_FLOW_POLICY.getId());
        } else {
            queryCondition.put("userBindMessageType",voipFlowStrategy.getMessageType());
        }
        queryCondition.put("operateType",OperationConstants.OPERATION_DELETE);
        return queryCondition;
    }

    /**
     * 获取当前日期的时间戳 精确到s
     * @return
     */
    private static Long getTodayTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        //设置日期格式
        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        long timestamp = 0;
        try {
            timestamp = simpleDateFormat.parse(date).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 当前时间是否在策略有效期
     * @return
     */
    private boolean inValidityPeriod(VoipFlowStrategy voipFlowStrategy){
        long currentTime = getTodayTimeStamp();
        return (currentTime >= voipFlowStrategy.getRStartTime())?true:false;
    }

}
