package com.aotain.zongfen.service.apppolicy.flowmirror.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.FlowMirrorIpAndPrefix;
import com.aotain.common.policyapi.model.FlowMirrorMatchContent;
import com.aotain.common.policyapi.model.FlowMirrorPort;
import com.aotain.common.policyapi.model.FlowMirrorStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.flowmirror.FlowMirrorDataMapper;
import com.aotain.zongfen.mapper.apppolicy.flowmirror.FlowMirrorIpMapper;
import com.aotain.zongfen.mapper.apppolicy.flowmirror.FlowMirrorMapper;
import com.aotain.zongfen.mapper.apppolicy.flowmirror.FlowMirrorPortMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorData;
import com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorIp;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.apppolicy.flowmirror.IFlowMirrorService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.aotain.zongfen.utils.CollectionUtils;
import com.google.common.collect.Maps;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/09
 */
@Service
public class FlowMirrorServiceImpl extends BaseService implements IFlowMirrorService{

    @Autowired
    private FlowMirrorMapper flowMirrorMapper;

    @Autowired
    private FlowMirrorIpMapper flowMirrorIpMapper;

    @Autowired
    private FlowMirrorPortMapper flowMirrorPortMapper;

    @Autowired
    private FlowMirrorDataMapper flowMirrorDataMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private CommonService commonService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected boolean addDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_SAVE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageType.APP_FLOW_MIRROR.getId();
        policy.setMessageType(messageType);
        policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        FlowMirrorStrategy flowMirrorStrategy = (FlowMirrorStrategy)policy;
        int result = flowMirrorMapper.insertSelective(flowMirrorStrategy);
        if ( result > 0 ){
            // 插入关联表
            List<FlowMirrorIpAndPrefix> flowMirrorIpList = flowMirrorStrategy.getSrcIpSegment();
            for (int i=0;i<flowMirrorIpList.size();i++){
                FlowMirrorIp flowMirrorIp = new FlowMirrorIp();
                flowMirrorIp.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorIp.setIpAddr(flowMirrorIpList.get(i).getIpAddr());
                flowMirrorIp.setIpPrefixLen(flowMirrorIpList.get(i).getIpPrefixLen());
                flowMirrorIp.setIpType(0);
                flowMirrorIpMapper.insertSelective(flowMirrorIp);
            }

            List<FlowMirrorIpAndPrefix> destFlowMirrorIpList = flowMirrorStrategy.getDstIpSegment();
            for (int i=0;i<destFlowMirrorIpList.size();i++){
                FlowMirrorIp flowMirrorIp = new FlowMirrorIp();
                flowMirrorIp.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorIp.setIpAddr(destFlowMirrorIpList.get(i).getIpAddr());
                flowMirrorIp.setIpPrefixLen(destFlowMirrorIpList.get(i).getIpPrefixLen());
                flowMirrorIp.setIpType(1);
                flowMirrorIpMapper.insertSelective(flowMirrorIp);
            }

            List<FlowMirrorPort> flowMirrorPorts = flowMirrorStrategy.getSrcPort();
            for (int i=0;i<flowMirrorPorts.size();i++){
                com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort flowMirrorPort =
                        new com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort();
                flowMirrorPort.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorPort.setPortStart(flowMirrorPorts.get(i).getPortStart());
                flowMirrorPort.setPortEnd(flowMirrorPorts.get(i).getPortEnd());
                flowMirrorPort.setPortType(0);
                flowMirrorPortMapper.insertSelective(flowMirrorPort);
            }

            List<FlowMirrorPort> destFlowMirrorPorts = flowMirrorStrategy.getDestPort();
            for (int i=0;i<destFlowMirrorPorts.size();i++){
                com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort flowMirrorPort =
                        new com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort();
                flowMirrorPort.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorPort.setPortStart(destFlowMirrorPorts.get(i).getPortStart());
                flowMirrorPort.setPortEnd(destFlowMirrorPorts.get(i).getPortEnd());
                flowMirrorPort.setPortType(1);
                flowMirrorPortMapper.insertSelective(flowMirrorPort);
            }

            List<FlowMirrorMatchContent> flowMirrorMatchContents = flowMirrorStrategy.getDataMatch();
            for(int i=0;i<flowMirrorMatchContents.size();i++){
                FlowMirrorData flowMirrorData = new FlowMirrorData();
                flowMirrorData.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorData.setMatchOffSet(flowMirrorMatchContents.get(i).getDataMatchOffset());
                flowMirrorData.setMatchContent(flowMirrorMatchContents.get(i).getDataMatchContent());
                flowMirrorDataMapper.insertSelective(flowMirrorData);
            }

            Policy policyNo = createPolicyBeanByBaseVo(flowMirrorStrategy);
            policyMapper.insert(policyNo);

        }
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        setPropertiesBeforeSaveOrUpdate(policy);

        FlowMirrorStrategy flowMirrorStrategy = (FlowMirrorStrategy)policy;
        // messageNo表
        Policy record = createPolicyBeanByBaseVo(flowMirrorStrategy);
        policyMapper.deletePolicyByMesNoAndType(record);

//        flowMirrorIpMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
//        flowMirrorPortMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
//        flowMirrorDataMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
//        flowMirrorMapper.deleteData(flowMirrorStrategy);

        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {

        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_UPDATE);
        setPropertiesBeforeSaveOrUpdate(policy);

        FlowMirrorStrategy flowMirrorStrategy = (FlowMirrorStrategy)policy;

        flowMirrorIpMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
        flowMirrorPortMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
        flowMirrorDataMapper.deleteByPolicyId(flowMirrorStrategy.getPolicyId());
        int result = flowMirrorMapper.updateSelective(flowMirrorStrategy);

        if ( result > 0 ){
            // 插入关联表
            List<FlowMirrorIpAndPrefix> flowMirrorIpList = flowMirrorStrategy.getSrcIpSegment();
            for (int i=0;i<flowMirrorIpList.size();i++){
                FlowMirrorIp flowMirrorIp = new FlowMirrorIp();
                flowMirrorIp.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorIp.setIpAddr(flowMirrorIpList.get(i).getIpAddr());
                flowMirrorIp.setIpPrefixLen(flowMirrorIpList.get(i).getIpPrefixLen());
                flowMirrorIp.setIpType(0);
                flowMirrorIpMapper.insertSelective(flowMirrorIp);
            }

            List<FlowMirrorIpAndPrefix> destFlowMirrorIpList = flowMirrorStrategy.getDstIpSegment();
            for (int i=0;i<destFlowMirrorIpList.size();i++){
                FlowMirrorIp flowMirrorIp = new FlowMirrorIp();
                flowMirrorIp.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorIp.setIpAddr(destFlowMirrorIpList.get(i).getIpAddr());
                flowMirrorIp.setIpPrefixLen(destFlowMirrorIpList.get(i).getIpPrefixLen());
                flowMirrorIp.setIpType(1);
                flowMirrorIpMapper.insertSelective(flowMirrorIp);
            }

            List<FlowMirrorPort> flowMirrorPorts = flowMirrorStrategy.getSrcPort();
            for (int i=0;i<flowMirrorPorts.size();i++){
                com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort flowMirrorPort =
                        new com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort();
                flowMirrorPort.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorPort.setPortStart(flowMirrorPorts.get(i).getPortStart());
                flowMirrorPort.setPortEnd(flowMirrorPorts.get(i).getPortEnd());
                flowMirrorPort.setPortType(0);
                flowMirrorPortMapper.insertSelective(flowMirrorPort);
            }

            List<FlowMirrorPort> destFlowMirrorPorts = flowMirrorStrategy.getDestPort();
            for (int i=0;i<destFlowMirrorPorts.size();i++){
                com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort flowMirrorPort =
                        new com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorPort();
                flowMirrorPort.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorPort.setPortStart(destFlowMirrorPorts.get(i).getPortStart());
                flowMirrorPort.setPortEnd(destFlowMirrorPorts.get(i).getPortEnd());
                flowMirrorPort.setPortType(1);
                flowMirrorPortMapper.insertSelective(flowMirrorPort);
            }

            List<FlowMirrorMatchContent> flowMirrorMatchContents = flowMirrorStrategy.getDataMatch();
            for(int i=0;i<flowMirrorMatchContents.size();i++){
                FlowMirrorData flowMirrorData = new FlowMirrorData();
                flowMirrorData.setPolicyId(flowMirrorStrategy.getPolicyId());
                flowMirrorData.setMatchOffSet(flowMirrorMatchContents.get(i).getDataMatchOffset());
                flowMirrorData.setMatchContent(flowMirrorMatchContents.get(i).getDataMatchContent());
                flowMirrorDataMapper.insertSelective(flowMirrorData);
            }

            Policy policyNo = createPolicyBeanByBaseVo(flowMirrorStrategy);
            policyMapper.updatePolicyByMessageNoAndType(policyNo);
        }

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
    public List<Long> addFlowMirrorAndUserBindPolicy(FlowMirrorStrategy flowMirrorStrategy){

        addPolicy(flowMirrorStrategy);
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(flowMirrorStrategy);
        List<Long> bindMessagNo = new ArrayList<Long>();
        for(int i=0;i<userPolicyBindStrategyList.size();i++){
            userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
            bindMessagNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
        }
       return bindMessagNo;
//        boolean inValidityPeriod = inValidityPeriod(flowMirrorStrategy);
//        if (inValidityPeriod) {
//            addPolicy(flowMirrorStrategy);
//            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(flowMirrorStrategy);
//            for(int i=0;i<userPolicyBindStrategyList.size();i++){
//                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
//            }
//        } else {
//            addDb(flowMirrorStrategy);
//            List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(flowMirrorStrategy);
//            for(int i=0;i<userPolicyBindStrategyList.size();i++){
//                userPolicyBindService.addDb(userPolicyBindStrategyList.get(i));
//            }
//        }
    }

    @Override
    public List<Long> modifyFlowMirrorAndUserBindPolicy(FlowMirrorStrategy flowMirrorStrategy){
        flowMirrorStrategy.setOperationType(OperationConstants.OPERATION_UPDATE);
//        setPropertiesBeforeSaveOrUpdate(flowMirrorStrategy);

        modifyPolicy(flowMirrorStrategy);

        Map<String,Object> queryCondition = generateQueryConditionMap(flowMirrorStrategy);
        // 删除原有的绑定策略
        List<UserPolicyBindStrategy> existUserPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
        // 新增用户绑定策略
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByFlowMirror(flowMirrorStrategy);

        List<Long> bindMessagNo = new ArrayList<Long>();
        if (!CollectionUtils.isEqualCollection(existUserPolicyBindStrategyList,userPolicyBindStrategyList)){
            deleteExistBindPolicy(existUserPolicyBindStrategyList,flowMirrorStrategy);
            for(int i=0;i<userPolicyBindStrategyList.size();i++){
                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
                bindMessagNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
            }
        }else {
        	for(int i=0;i<existUserPolicyBindStrategyList.size();i++){
                bindMessagNo.add(existUserPolicyBindStrategyList.get(i).getMessageNo());
            }
        }
        return bindMessagNo;
    }

    @Override
    public List<FlowMirrorStrategy> listData(Map<String,Object> queryMap){
        List<FlowMirrorStrategy> result = flowMirrorMapper.listData(queryMap);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            FlowMirrorStrategy flowMirrorStrategy = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(flowMirrorStrategy.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(flowMirrorStrategy.getMessageNo());
                query2.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                flowMirrorStrategy.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
                flowMirrorStrategy.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else {
                flowMirrorStrategy.setAppPolicy("0/0");
                flowMirrorStrategy.setBindPolicy("0/0");
            }
        }
        return result;
    }

    @Override
    public FlowMirrorStrategy selectByPrimaryKey(long policyId){
        return flowMirrorMapper.selectByPrimaryKey(policyId);
    }

    @Override
    public List<Long> deletePolicys(List<FlowMirrorStrategy> flowMirrorStrategies){
    	List<Long> bindMessageNo = new ArrayList<Long>();
        for (int i=0;i<flowMirrorStrategies.size();i++) {
            Map<String,Object> queryCondition = generateQueryConditionMap(flowMirrorStrategies.get(i));
            // 删除原有的绑定策略
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
            deleteExistBindPolicy(userPolicyBindStrategyList,flowMirrorStrategies.get(i));

            deletePolicy(flowMirrorStrategies.get(i));
            for(UserPolicyBindStrategy tem:userPolicyBindStrategyList) {
            	 bindMessageNo.add(tem.getMessageNo());	
            }
        }
        return bindMessageNo;
    }

    @Override
    public List<Long> resendPolicy(long topTaskId,long messageNo,boolean needSendBindPolicy, List<String> dpiIp){
        // 重发主策略
        manualRetryPolicy(topTaskId,0,MessageType.APP_FLOW_MIRROR.getId(),messageNo,dpiIp);
        List<Long> bindMessageNo = new ArrayList<Long>();
        // 重发关联的绑定策略
        if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageType.APP_FLOW_MIRROR.getId());
            queryMap.put("operateType",OperationConstants.OPERATION_DELETE);
            List<UserPolicyBindStrategy> userPolicyBindStrategies = userPolicyBindMapper.getByBindMessageNoAndType(queryMap);
            for(int i=0;i<userPolicyBindStrategies.size();i++){
                manualRetryPolicy(topTaskId,0,MessageType.USER_POLICY_BIND.getId(),
                        userPolicyBindStrategies.get(i).getMessageNo(),dpiIp);
                for(UserPolicyBindStrategy temp:userPolicyBindStrategies) {
                	bindMessageNo.add(temp.getMessageNo());
                }
            }
        }
        return bindMessageNo;
    }

    /**
     * 根据flowMirrorStrategy相关值确定用户应用绑定策略list
     * @param flowMirrorStrategy
     * @return
     */
    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByBaseVo(FlowMirrorStrategy flowMirrorStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (flowMirrorStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(flowMirrorStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (flowMirrorStrategy.getUserType()==1 || flowMirrorStrategy.getUserType()==2) {
            // 用户组
            List<String> userNames = flowMirrorStrategy.getUserNames();
            for (int i=0;i<userNames.size();i++) {
                // 账号或者IP地址
                if (!org.springframework.util.StringUtils.isEmpty(userNames.get(i))) {
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserName(userNames.get(i));
                    copyProperties(flowMirrorStrategy, userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }
        } else if (flowMirrorStrategy.getUserType()==3){
            // 用户组
            List<String> userGroups = flowMirrorStrategy.getUserGroupIds();
            for (int i=0;i<userGroups.size();i++){
                if ( !org.springframework.util.StringUtils.isEmpty(userGroups.get(i)) ){
                    long userGroupId = Integer.valueOf(userGroups.get(i));
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserGroupId(userGroupId);
                    userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                    copyProperties(flowMirrorStrategy,userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }

        }

        return userPolicyBindStrategyList;

    }

    /**
     * 根据flowMirrorStrategy实体构造Policy实体
     * @param flowMirrorStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo(FlowMirrorStrategy flowMirrorStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(flowMirrorStrategy.getMessageNo());
        policyNo.setMessageSequenceno(flowMirrorStrategy.getMessageSequenceNo());
        policyNo.setMessageType(flowMirrorStrategy.getMessageType());
        policyNo.setMessageName(flowMirrorStrategy.getMessageName());
        policyNo.setOperateType(flowMirrorStrategy.getOperationType());
        if ( !StringUtils.isEmpty(flowMirrorStrategy.getCreateOper()) ){
            policyNo.setCreateOper(flowMirrorStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(flowMirrorStrategy.getModifyOper()) ){
            policyNo.setModifyOper(flowMirrorStrategy.getModifyOper());
        }
        if ( flowMirrorStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(flowMirrorStrategy.getCreateTime());
        }
        if ( flowMirrorStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(flowMirrorStrategy.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 当前时间是否在策略有效期
     * @return
     */
    private boolean inValidityPeriod(FlowMirrorStrategy flowMirrorStrategy){
        long currentTime = getTodayTimeStamp();
        return (currentTime >= flowMirrorStrategy.getStartTime())?true:false;
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
     * 赋值相关属性
     * @param flowMirrorStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(FlowMirrorStrategy flowMirrorStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(flowMirrorStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(flowMirrorStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(flowMirrorStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(flowMirrorStrategy.getMessageNo());
        bindMessage.setBindMessageType(flowMirrorStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !org.springframework.util.StringUtils.isEmpty(flowMirrorStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(flowMirrorStrategy.getCreateOper());
        }
        if ( !org.springframework.util.StringUtils.isEmpty(flowMirrorStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(flowMirrorStrategy.getModifyOper());
        }
        if ( flowMirrorStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(flowMirrorStrategy.getCreateTime());
        }
        if ( flowMirrorStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(flowMirrorStrategy.getModifyTime());
        }
    }

    private void setPropertiesBeforeSaveOrUpdate(BaseVO baseVO){
        //设置一些公共属性
        baseVO.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageType.APP_FLOW_MIRROR.getId();
        baseVO.setMessageType(messageType);
        if (org.springframework.util.StringUtils.isEmpty(baseVO.getMessageNo())){
            baseVO.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        }
        baseVO.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));
    }

    /**
     * 生成对应绑定策略查询条件
     * @param flowMirrorStrategy
     * @return
     */
    private Map<String,Object> generateQueryConditionMap(FlowMirrorStrategy flowMirrorStrategy){
        Map<String,Object> queryCondition = Maps.newHashMap();
        queryCondition.put("messageNo",flowMirrorStrategy.getMessageNo());
        queryCondition.put("userBindMessageType",flowMirrorStrategy.getMessageType());
        queryCondition.put("operateType",OperationConstants.OPERATION_DELETE);
        return queryCondition;
    }

    /**
     * 删除原有的用户组绑定策略
     * @param userPolicyBindStrategyList
     * @param flowMirrorStrategy
     */
    private void deleteExistBindPolicy(List<UserPolicyBindStrategy> userPolicyBindStrategyList,FlowMirrorStrategy flowMirrorStrategy){
        flowMirrorStrategy.setMessageType(MessageType.APP_FLOW_MIRROR.getId());
        for (UserPolicyBindStrategy userPolicyBindStrategy : userPolicyBindStrategyList) {
            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(flowMirrorStrategy.getMessageNo());
            bindMessage.setBindMessageType(flowMirrorStrategy.getMessageType());
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            userPolicyBindService.deletePolicy(userPolicyBindStrategy);
        }
    }

    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByFlowMirror(FlowMirrorStrategy flowMirrorStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (flowMirrorStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(flowMirrorStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (flowMirrorStrategy.getUserType()==1 || flowMirrorStrategy.getUserType()==2) {
            // 账号或者IP地址
            for (int i=0;i< flowMirrorStrategy.getUserNames().size();i++){
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserName(flowMirrorStrategy.getUserNames().get(i));
                copyProperties(flowMirrorStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        } else if (flowMirrorStrategy.getUserType()==3){
            // 用户组
            for (int i=0;i<flowMirrorStrategy.getUserGroupIds().size();i++){
                long userGroupId = Long.valueOf(flowMirrorStrategy.getUserGroupIds().get(i));
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserGroupId(userGroupId);
                userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                copyProperties(flowMirrorStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }

        }

        return userPolicyBindStrategyList;
    }

}
