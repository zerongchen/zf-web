package com.aotain.zongfen.service.apppolicy.appuser.impl;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.AppUserStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.AppUserMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.apppolicy.appuser.IAppUserService;
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
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/05
 */
@Service
public class AppUserServiceImpl extends BaseService implements IAppUserService{

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Override
    public AppUserStrategy selectByPrimaryKey(AppUserStrategy appUserStrategy){
        return appUserMapper.selectByPrimaryKey(appUserStrategy);
    }

    @Override
    public List<AppUserStrategy> listData(Map<String,Object> queryMap){
        List<AppUserStrategy> result = appUserMapper.listData(queryMap);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<result.size();i++){
            AppUserStrategy appUserStrategy = result.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(appUserStrategy.getMessageNo()!=null) {
            	PolicyStatus query2 = new PolicyStatus();
            	query2.setMessageNo(appUserStrategy.getMessageNo());
            	query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER);
            	appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
            	appUserStrategy.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
                appUserStrategy.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else {
            	appUserStrategy.setAppPolicy("0/0");
                appUserStrategy.setBindPolicy("0/0");
            }
            
        }
        return result;
    }

    @Override
    public int batchDeleteAppUser(List<Long> messageNos){
        return appUserMapper.batchDelete(messageNos);
    }

    @Override
    public int batchDeleteRelativeMessageNo(List<Long> messageNos,long messageType) {
        return  policyMapper.deletePolicyByMessageNoAndType(messageNos.stream().toArray(Long[]::new),messageType);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(List<Long> messageNos,long messageType){
        batchDeleteAppUser(messageNos);
        batchDeleteRelativeMessageNo(messageNos,messageType);
    }

    @Override
    public boolean resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp){
        // 重发主策略
        manualRetryPolicy(topTaskId,0,MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER,messageNo,dpiIp);
        // 重发关联的绑定策略
        if (needSendBindPolicy){
            Map<String,Object> queryMap = new HashedMap();
            queryMap.put("messageNo",messageNo);
            queryMap.put("userBindMessageType",MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER);
            queryMap.put("operateType",OperationConstants.OPERATION_DELETE);
            List<UserPolicyBindStrategy> userPolicyBindStrategies = userPolicyBindMapper.getByBindMessageNoAndType(queryMap);
            for(int i=0;i<userPolicyBindStrategies.size();i++){
                manualRetryPolicy(topTaskId,0,MessageType.USER_POLICY_BIND.getId(),
                        userPolicyBindStrategies.get(i).getMessageNo(),dpiIp);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected boolean addDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_SAVE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageNo messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER;
        policy.setMessageType(messageType);
        policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        AppUserStrategy appUserStrategy = (AppUserStrategy)policy;
        appUserMapper.insertSelective(appUserStrategy);

        Policy policyNo = createPolicyBeanByBaseVo(appUserStrategy);
        policyMapper.insert(policyNo);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected boolean deleteDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_DELETE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER;
        policy.setMessageType(messageType);
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        AppUserStrategy appUserStrategy = (AppUserStrategy)policy;
//        appUserMapper.deleteByPrimaryKey(appUserStrategy.getMessageNo());

        Policy policyNo = createPolicyBeanByBaseVo(appUserStrategy);
        policyMapper.deleteByMessageNoAndType(policyNo);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected boolean modifyDb(BaseVO policy) {
        //设置probeType和operateType
        policy.setOperationType(OperationConstants.OPERATION_UPDATE);
        policy.setProbeType(ProbeType.DPI.getValue());
        // 设置messageType messageSeqNo
        int messageType = MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER;
        policy.setMessageType(messageType);
        policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

        AppUserStrategy appUserStrategy = (AppUserStrategy)policy;
        appUserMapper.updateByPrimaryKeySelective(appUserStrategy);

        Policy policyNo = createPolicyBeanByBaseVo(appUserStrategy);
        policyMapper.updatePolicyByMessageNoAndType(policyNo);
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

    /**
     * 添加指定应用用户策略和用户绑定策略
     * @param appUserStrategy
     * @return
     */
    public boolean addAppUserPolicyAndUserBindPolicy(AppUserStrategy appUserStrategy){
        addPolicy(appUserStrategy);
        return true;
    }

    /**
     * 修改指定应用用户和用户绑定策略
     * @param appUserStrategy
     * @return
     */
    public boolean modifyAppUserPolicyAndUserBindPolicy(AppUserStrategy appUserStrategy){
        appUserStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_APPLICATION_USER);
        modifyPolicy(appUserStrategy);
        return true;
    }


    /**
     * 修改指定应用用户和用户绑定策略
     * @param appUserStrategies
     * @return
     */
    public boolean deletePolicys(List<AppUserStrategy> appUserStrategies){

        for (int i=0;i<appUserStrategies.size();i++) {
            AppUserStrategy result = appUserMapper.selectByPrimaryKey(appUserStrategies.get(i));
            try{
                appUserStrategies.get(i).setCountStartTime(result.getRStartTime().toString());
                appUserStrategies.get(i).setCountEndTime(result.getREndTime().toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            deletePolicy(appUserStrategies.get(i));
        }


        return true;
    }


    /**
     * 根据AppUserStrategy实体构造Policy实体
     * @param appUserStrategy
     * @return
     */
    private Policy createPolicyBeanByBaseVo(AppUserStrategy appUserStrategy){
        Policy policyNo = new Policy();
        policyNo.setMessageNo(appUserStrategy.getMessageNo());
        policyNo.setMessageSequenceno(appUserStrategy.getMessageSequenceNo());
        policyNo.setMessageType(appUserStrategy.getMessageType());
        policyNo.setMessageName(appUserStrategy.getMessageName());
        policyNo.setOperateType(appUserStrategy.getOperationType());
        if ( !StringUtils.isEmpty(appUserStrategy.getCreateOper()) ){
            policyNo.setCreateOper(appUserStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(appUserStrategy.getModifyOper()) ){
            policyNo.setModifyOper(appUserStrategy.getModifyOper());
        }
        if ( appUserStrategy.getCreateTime() != null ){
            policyNo.setCreateTime(appUserStrategy.getCreateTime());
        }
        if ( appUserStrategy.getModifyTime() != null ){
            policyNo.setModifyTime(appUserStrategy.getModifyTime());
        }
        return policyNo;
    }

    /**
     * 赋值相关属性
     * @param appUserStrategy
     * @param userPolicyBindStrategy
     */
    private void copyProperties(AppUserStrategy appUserStrategy,UserPolicyBindStrategy userPolicyBindStrategy){
        userPolicyBindStrategy.setUserBindMessageNo(appUserStrategy.getMessageNo());
        userPolicyBindStrategy.setUserBindMessageType(appUserStrategy.getMessageType());
        userPolicyBindStrategy.setUserType(appUserStrategy.getUserType());

        List<BindMessage> bindMessages = new ArrayList<>();
        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(appUserStrategy.getMessageNo());
        bindMessage.setBindMessageType(appUserStrategy.getMessageType());
        bindMessages.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(bindMessages);

        if ( !StringUtils.isEmpty(appUserStrategy.getCreateOper()) ){
            userPolicyBindStrategy.setCreateOper(appUserStrategy.getCreateOper());
        }
        if ( !StringUtils.isEmpty(appUserStrategy.getModifyOper()) ){
            userPolicyBindStrategy.setModifyOper(appUserStrategy.getModifyOper());
        }
        if ( appUserStrategy.getCreateTime() != null ){
            userPolicyBindStrategy.setCreateTime(appUserStrategy.getCreateTime());
        }
        if ( appUserStrategy.getModifyTime() != null ){
            userPolicyBindStrategy.setModifyTime(appUserStrategy.getModifyTime());
        }
    }
}
