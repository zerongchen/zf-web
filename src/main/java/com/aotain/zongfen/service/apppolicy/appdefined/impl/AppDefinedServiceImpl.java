package com.aotain.zongfen.service.apppolicy.appdefined.impl;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.AppDefinedKey;
import com.aotain.common.policyapi.model.AppDefinedQuintet;
import com.aotain.common.policyapi.model.AppDefinedStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.appdefined.AppDefinedKeyMapper;
import com.aotain.zongfen.mapper.apppolicy.appdefined.AppDefinedMapper;
import com.aotain.zongfen.mapper.apppolicy.appdefined.AppDefinedQuintetMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.apppolicy.appdefined.IAppDefinedService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/10
 */
@Service
public class AppDefinedServiceImpl extends BaseService implements IAppDefinedService{

    @Autowired
    private AppDefinedMapper appDefinedMapper;

    @Autowired
    private AppDefinedQuintetMapper appDefinedQuintetMapper;

    @Autowired
    private AppDefinedKeyMapper appDefinedKeyMapper;

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
    protected boolean addDb(BaseVO policy) {

        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_SAVE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageType.APP_DEFINED.getId();
        policy.setMessageType(messageType);
        policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        AppDefinedStrategy appDefinedStrategy = (AppDefinedStrategy)policy;
        int result = appDefinedMapper.insertSelective(appDefinedStrategy);
        if ( result>0 ){
            List<AppDefinedQuintet> appDefinedQuintetList = appDefinedStrategy.getSignature();

            // 插入关联表
            for (int i=0;i<appDefinedQuintetList.size();i++){
                AppDefinedQuintet appDefinedQuintet = appDefinedQuintetList.get(i);
                appDefinedQuintet.setDefinedId(appDefinedStrategy.getDefinedId());
                appDefinedQuintetMapper.insertSelective(appDefinedQuintet);

                List<AppDefinedKey> appDefinedKeyList = appDefinedQuintetList.get(i).getKw();
                for (int j=0;j<appDefinedKeyList.size();j++){
                    AppDefinedKey appDefinedKey = appDefinedKeyList.get(j);
                    appDefinedKey.setQuintetId(appDefinedQuintet.getQuintetId());
                    appDefinedKeyMapper.insertSelective(appDefinedKey);
                }
            }
            // 插入messageNo表
            Policy policyNo = createPolicyBeanByBaseVo(appDefinedStrategy);
            policyMapper.insert(policyNo);
        }
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        setPropertiesBeforeSaveOrUpdate(policy);

        AppDefinedStrategy appDefinedStrategy = (AppDefinedStrategy)policy;
        // messageNo表
        Policy record = createPolicyBeanByBaseVo(appDefinedStrategy);
        policyMapper.deletePolicyByMesNoAndType(record);

        // 删除原有的数据
//        List<AppDefinedQuintet> searchResult = appDefinedQuintetMapper.selectByDefinedId(appDefinedStrategy.getDefinedId());
//        for (int i=0;i<searchResult.size();i++){
//            appDefinedKeyMapper.deleteByQuintetId(searchResult.get(i).getQuintetId());
//        }
//        appDefinedQuintetMapper.deleteByDefinedId(appDefinedStrategy.getDefinedId());
//
//        appDefinedMapper.deleteData(appDefinedStrategy);
        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_UPDATE);
        setPropertiesBeforeSaveOrUpdate(policy);

        AppDefinedStrategy appDefinedStrategy = (AppDefinedStrategy)policy;

        // 删除原有的数据
        List<AppDefinedQuintet> searchResult = appDefinedQuintetMapper.selectByDefinedId(appDefinedStrategy.getDefinedId());
        for (int i=0;i<searchResult.size();i++){
            appDefinedKeyMapper.deleteByQuintetId(searchResult.get(i).getQuintetId());
        }
        appDefinedQuintetMapper.deleteByDefinedId(appDefinedStrategy.getDefinedId());

        List<AppDefinedQuintet> appDefinedQuintetList = appDefinedStrategy.getSignature();
        int result = appDefinedMapper.updateSelective(appDefinedStrategy);
        if (result>0){
            // 插入关联表
            for (int i=0;i<appDefinedQuintetList.size();i++){
                AppDefinedQuintet appDefinedQuintet = appDefinedQuintetList.get(i);
                appDefinedQuintet.setDefinedId(appDefinedStrategy.getDefinedId());
                appDefinedQuintetMapper.insertSelective(appDefinedQuintet);

                List<AppDefinedKey> appDefinedKeyList = appDefinedQuintetList.get(i).getKw();
                for (int j=0;j<appDefinedKeyList.size();j++){
                    AppDefinedKey appDefinedKey = appDefinedKeyList.get(j);
                    appDefinedKey.setQuintetId(appDefinedQuintet.getQuintetId());
                    appDefinedKeyMapper.insertSelective(appDefinedKey);
                }
            }

            // 插入messageNo表
            Policy policyNo = createPolicyBeanByBaseVo(appDefinedStrategy);
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
    @Transactional(rollbackFor = Exception.class)
    public List<Long> addAppDefinedAndUserBindPolicy(AppDefinedStrategy appDefinedStrategy) {
    	List<Long> bindMessageNo = new ArrayList<Long>();
        addPolicy(appDefinedStrategy);
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByBaseVo(appDefinedStrategy);
        for(int i=0;i<userPolicyBindStrategyList.size();i++){
            userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
            bindMessageNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
        }
        return bindMessageNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> modifyAppDefinedAndUserBindPolicy(AppDefinedStrategy appDefinedStrategy){
    	List<Long> bindMessageNo = new ArrayList<Long>();
        appDefinedStrategy.setOperationType(OperationConstants.OPERATION_UPDATE);
        setPropertiesBeforeSaveOrUpdate(appDefinedStrategy);

        modifyPolicy(appDefinedStrategy);

        Map<String,Object> queryCondition = generateQueryConditionMap(appDefinedStrategy);
        // 删除原有的绑定策略
        List<UserPolicyBindStrategy> existUserPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
        // 新增用户绑定策略
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = createUserPolicyBindBeanByFlowMirror(appDefinedStrategy);

        if ( !CollectionUtils.isEqualCollection(existUserPolicyBindStrategyList,userPolicyBindStrategyList ) ){
            deleteExistBindPolicy(existUserPolicyBindStrategyList,appDefinedStrategy);
            for(int i=0;i<userPolicyBindStrategyList.size();i++){
                userPolicyBindService.addPolicy(userPolicyBindStrategyList.get(i));
                bindMessageNo.add(userPolicyBindStrategyList.get(i).getMessageNo());
            }
        }else {
        	for(int i=0;i<existUserPolicyBindStrategyList.size();i++){
        		bindMessageNo.add(existUserPolicyBindStrategyList.get(i).getMessageNo());
            }
        }
        return bindMessageNo;
    }

    @Override
    public List<AppDefinedStrategy> listData(Map<String,Object> queryMap){

        List<AppDefinedStrategy> result = appDefinedMapper.listData(queryMap);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            AppDefinedStrategy appDefinedStrategy = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(appDefinedStrategy.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(appDefinedStrategy.getMessageNo());
                query2.setMessageType(MessageType.APP_DEFINED.getId());
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                appDefinedStrategy.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
                appDefinedStrategy.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else {
                appDefinedStrategy.setAppPolicy("0/0");
                appDefinedStrategy.setBindPolicy("0/0");
            }
        }
        return result;
    }

    @Override
    public List<Long> resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp){
    	List<Long> bindMessageNo = new ArrayList<Long>();
    	// 重发主策略
        manualRetryPolicy(topTaskId,0,MessageType.APP_DEFINED.getId(),messageNo,dpiIp);
        // 重发关联的绑定策略
        if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageType.APP_DEFINED.getId());
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

    @Override
    public AppDefinedStrategy selectByPrimaryKey(long definedId){
        return appDefinedMapper.selectByPrimaryKey(definedId);
    }

    @Override
    public List<Long> deletePolicys(List<AppDefinedStrategy> appDefinedStrategies){
    	List<Long> bindMessageNo = new ArrayList<Long>();
        for (int i=0;i<appDefinedStrategies.size();i++) {
            Map<String,Object> queryCondition = generateQueryConditionMap(appDefinedStrategies.get(i));
            // 删除原有的绑定策略
            List<UserPolicyBindStrategy> userPolicyBindStrategyList = userPolicyBindMapper.getByBindMessageNoAndType(queryCondition);
            deleteExistBindPolicy(userPolicyBindStrategyList,appDefinedStrategies.get(i));

            deletePolicy(appDefinedStrategies.get(i));
            for(UserPolicyBindStrategy tem:userPolicyBindStrategyList) {
            	bindMessageNo.add(tem.getMessageNo());
            }
        }
        return bindMessageNo;
    }

    /**
     * 根据appDefinedStrategy实体构造Policy实体
     * @param appDefinedStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo(AppDefinedStrategy appDefinedStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(appDefinedStrategy.getMessageNo());
        policyNo.setMessageSequenceno(appDefinedStrategy.getMessageSequenceNo());
        policyNo.setMessageType(appDefinedStrategy.getMessageType());
        policyNo.setMessageName(appDefinedStrategy.getMessageName());
        policyNo.setOperateType(appDefinedStrategy.getOperationType());
        if ( !StringUtils.isEmpty(appDefinedStrategy.getCreateOper()) ){
            policyNo.setCreateOper(appDefinedStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(appDefinedStrategy.getModifyOper()) ){
            policyNo.setModifyOper(appDefinedStrategy.getModifyOper());
        }
        if ( appDefinedStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(appDefinedStrategy.getCreateTime());
        }
        if ( appDefinedStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(appDefinedStrategy.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 根据appDefinedStrategy相关值确定用户应用绑定策略list
     * @param appDefinedStrategy
     * @return
     */
    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByBaseVo(AppDefinedStrategy appDefinedStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (appDefinedStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(appDefinedStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (appDefinedStrategy.getUserType()==1 || appDefinedStrategy.getUserType()==2) {
            // 用户组
            List<String> userNames = appDefinedStrategy.getUserNames();
            for (int i=0;i<userNames.size();i++) {
                // 账号或者IP地址
                if (!org.springframework.util.StringUtils.isEmpty(userNames.get(i))) {
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserName(userNames.get(i));
                    copyProperties(appDefinedStrategy, userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }
        } else if (appDefinedStrategy.getUserType()==3){
            // 用户组
            List<String> userGroups = appDefinedStrategy.getUserGroupIds();
            for (int i=0;i<userGroups.size();i++){
                if ( !org.springframework.util.StringUtils.isEmpty(userGroups.get(i)) ){
                    long userGroupId = Integer.valueOf(userGroups.get(i));
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserGroupId(userGroupId);
                    userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                    copyProperties(appDefinedStrategy,userPolicyBindStrategy);
                    userPolicyBindStrategyList.add(userPolicyBindStrategy);
                }
            }

        }
        return userPolicyBindStrategyList;
    }

    /**
     * 赋值相关属性
     * @param appDefinedStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(AppDefinedStrategy appDefinedStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(appDefinedStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(appDefinedStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(appDefinedStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(appDefinedStrategy.getMessageNo());
        bindMessage.setBindMessageType(appDefinedStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !org.springframework.util.StringUtils.isEmpty(appDefinedStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(appDefinedStrategy.getCreateOper());
        }
        if ( !org.springframework.util.StringUtils.isEmpty(appDefinedStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(appDefinedStrategy.getModifyOper());
        }
        if ( appDefinedStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(appDefinedStrategy.getCreateTime());
        }
        if ( appDefinedStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(appDefinedStrategy.getModifyTime());
        }
    }

    private void setPropertiesBeforeSaveOrUpdate(BaseVO baseVO){
        //设置一些公共属性
        baseVO.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageType.APP_DEFINED.getId();
        baseVO.setMessageType(messageType);
        if (org.springframework.util.StringUtils.isEmpty(baseVO.getMessageNo())){
            baseVO.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        }
        baseVO.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));
    }

    /**
     * 生成对应绑定策略查询条件
     * @param appDefinedStrategy
     * @return
     */
    private Map<String,Object> generateQueryConditionMap(AppDefinedStrategy appDefinedStrategy){
        Map<String,Object> queryCondition = Maps.newHashMap();
        queryCondition.put("messageNo",appDefinedStrategy.getMessageNo());
        queryCondition.put("userBindMessageType",appDefinedStrategy.getMessageType());
        queryCondition.put("operateType",OperationConstants.OPERATION_DELETE);
        return queryCondition;
    }

    /**
     * 删除原有的用户组绑定策略
     * @param userPolicyBindStrategyList
     * @param appDefinedStrategy
     */
    private void deleteExistBindPolicy(List<UserPolicyBindStrategy> userPolicyBindStrategyList,AppDefinedStrategy appDefinedStrategy){
        appDefinedStrategy.setMessageType(MessageType.APP_DEFINED.getId());
        for (UserPolicyBindStrategy userPolicyBindStrategy : userPolicyBindStrategyList) {
            List<BindMessage> bindMessages = new ArrayList<>();
            BindMessage bindMessage = new BindMessage();
            bindMessage.setBindMessageNo(appDefinedStrategy.getMessageNo());
            bindMessage.setBindMessageType(appDefinedStrategy.getMessageType());
            bindMessages.add(bindMessage);
            userPolicyBindStrategy.setBindInfo(bindMessages);
            userPolicyBindService.deletePolicy(userPolicyBindStrategy);
        }
    }

    private List<UserPolicyBindStrategy> createUserPolicyBindBeanByFlowMirror(AppDefinedStrategy appDefinedStrategy){
        List<UserPolicyBindStrategy> userPolicyBindStrategyList = new ArrayList<>();

        if (appDefinedStrategy.getUserType()==0){
            // 全用户
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            // 将userName设置为空字符串
            userPolicyBindStrategy.setUserName("");
            copyProperties(appDefinedStrategy,userPolicyBindStrategy);
            userPolicyBindStrategyList.add(userPolicyBindStrategy);

        } else if (appDefinedStrategy.getUserType()==1 || appDefinedStrategy.getUserType()==2) {
            // 账号或者IP地址
            for (int i=0;i< appDefinedStrategy.getUserNames().size();i++){
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserName(appDefinedStrategy.getUserNames().get(i));
                copyProperties(appDefinedStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }
        } else if (appDefinedStrategy.getUserType()==3){
            // 用户组
            for (int i=0;i<appDefinedStrategy.getUserGroupIds().size();i++){
                long userGroupId = Long.valueOf(appDefinedStrategy.getUserGroupIds().get(i));
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserGroupId(userGroupId);
                userPolicyBindStrategy.setUserName(commonService.getUserGroupName(userGroupId));
                copyProperties(appDefinedStrategy,userPolicyBindStrategy);
                userPolicyBindStrategyList.add(userPolicyBindStrategy);
            }

        }

        return userPolicyBindStrategyList;
    }
}
