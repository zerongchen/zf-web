package com.aotain.zongfen.service.userbind;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.system.OperationLogMapper;
import com.aotain.zongfen.model.system.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.BindAction;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;

/**
 * 
* @ClassName: UserPolicyBindService 
* @Description: 用户应用绑定策略，该策略只针对表
*  zf_v2_policy_userpolicy_bind 和 zf_v2_policy_messageno 操作(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年2月6日 下午3:06:41 
*
 */
@Service
@PropertySource({"classpath:application.properties"})
public class UserPolicyBindService extends BaseService{
	
	private static Logger logger = LoggerFactory.getLogger(UserPolicyBindService.class);
	
	@Autowired
	private PolicyMapper policyMapper;

	@Autowired
	private OperationLogMapper operationLogMapper;
	
	@Autowired
	private UserPolicyBindMapper userPolicyBindMapper;

	@Value("${server.port}")
	private Integer serverPort;

	/**
	 * 注意要传入BindMessage的值
	 */
	@Override
	public boolean addDb(BaseVO vo) {
		if(vo instanceof UserPolicyBindStrategy) {
			//转换
			UserPolicyBindStrategy record = (UserPolicyBindStrategy) vo ;
			
			Policy policy = new Policy();
			policy.setMessageType(MessageType.USER_POLICY_BIND.getId());
			Date time = new Date();
			
			long messageNo = record.getMessageNo() == null ? MessageNoUtil.getInstance().getMessageNo(MessageType.USER_POLICY_BIND.getId()) : record.getMessageNo();
			long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_POLICY_BIND.getId());
			
			//这个很关键，主要是用于可能一个用户组应用绑定策略，可以对应多种不同的应用策略
			policy.setMessageNo(messageNo);
			policy.setMessageType(MessageType.USER_POLICY_BIND.getId());
			policy.setMessageName(MessageType.USER_POLICY_BIND.getMessageType());
			policy.setMessageSequenceno(messageSqNo);
			policy.setCreateTime(time);
			policy.setModifyTime(time);
			policy.setCreateOper(SpringUtil.getSysUserName());
			policy.setModifyOper(SpringUtil.getSysUserName());
			policy.setOperateType(OperationConstants.OPERATION_SAVE);
			/**
			 * 需要补充createOper和modifyOper
			 */
			policyMapper.insertSelective(policy);
		
	        
			record.setMessageType(MessageType.USER_POLICY_BIND.getId());
	        record.setMessageNo(messageNo);
	        record.setMessageSequenceNo(messageSqNo);
	        record.setOperationType(OperationConstants.OPERATION_SAVE);
	        record.setCreateTime(time);
	        record.setModifyTime(time);
	        record.setCreateOper(SpringUtil.getSysUserName());
	        record.setModifyOper(SpringUtil.getSysUserName());
	        record.setBindAction(BindAction.BIND.getValue());
			record.setProbeType(ProbeType.DPI.getValue());
	        
	        List<BindMessage> bindInfoList = record.getBindInfo();
	        if(bindInfoList != null && bindInfoList.size() > 0) {
	        	for(BindMessage bindMessage: bindInfoList) {
	        		
	        		record.setUserBindMessageNo(bindMessage.getBindMessageNo());
	        		record.setUserBindMessageType(bindMessage.getBindMessageType());
	        		
	        		userPolicyBindMapper.insertSelective(record);

	        		try{
//						OperationLog operationLog = createOperationLogByStrategy(record);
//						operationLogMapper.insertSelective(operationLog);
					} catch (Exception e){
	        			logger.error("add operation Log failed...");
					}
	        	}
	        }else {
	        	logger.error("you have no bind message, error add database!");
	        }
	        return true;
		}
		return false;
	}
	
	
	/**
	 * 注意要传入BindMessage的值
	 */
	@Override
	public boolean deleteDb(BaseVO vo) {
		if(vo instanceof UserPolicyBindStrategy) {
			//转换
			UserPolicyBindStrategy record = (UserPolicyBindStrategy) vo ;
			long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_POLICY_BIND.getId());

			Policy policy = new Policy();
			policy.setMessageType(MessageType.USER_POLICY_BIND.getId());
			policy.setMessageNo(record.getMessageNo());
			policy.setOperateType(3);
			policy.setMessageSequenceno(messageSqNo);


	        Date time = new Date();
	        record.setMessageSequenceNo(messageSqNo);
	        record.setOperationType(OperationConstants.OPERATION_DELETE);
	        record.setCreateTime(time);
	        record.setModifyTime(time);
	        record.setBindAction(BindAction.UNBIND.getValue());
			record.setProbeType(ProbeType.DPI.getValue());
			record.setCreateOper(SpringUtil.getSysUserName());
			record.setModifyOper(SpringUtil.getSysUserName());
			record.setMessageType(MessageType.USER_POLICY_BIND.getId());

	        policyMapper.updatePolicyByMessageNoAndType(policy);
	        
	        List<BindMessage> bindInfoList = record.getBindInfo();
	        if(bindInfoList != null && bindInfoList.size() > 0) {
	        	for(BindMessage bindMessage: bindInfoList) {
	        		record.setUserBindMessageNo(bindMessage.getBindMessageNo());
	        		record.setUserBindMessageType(bindMessage.getBindMessageType());
	        		userPolicyBindMapper.updateOrDelete(record);

	        		try{
//						OperationLog operationLog = createOperationLogByStrategy(record);
//						operationLogMapper.insertSelective(operationLog);
					}catch (Exception e){
						logger.error("add operation Log failed...");
					}
	        	}
	        }else {
	        	logger.error("you have no bind message, error delete database!");
	        }
	        
	        return true;
		}
		return false;
	}

	@Override
	public boolean modifyDb(BaseVO vo) {
		return true;
	}

	@Override
	protected boolean addCustomLogic(BaseVO vo) {
		return setPolicyOperateSequenceToRedis(vo) && addTaskAndChannelToRedis(vo);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO vo) {
		return true;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO vo) {
		return setPolicyOperateSequenceToRedis(vo) && addTaskAndChannelToRedis(vo);
	}

	private OperationLog createOperationLogByStrategy(UserPolicyBindStrategy userPolicyBindStrategy){
		OperationLog operationLog = new OperationLog();
		if ( userPolicyBindStrategy.getModifyOper() != null ){
			operationLog.setOperUser(userPolicyBindStrategy.getModifyOper());
		} else {
			operationLog.setOperUser(SpringUtil.getSysUserName());
		}
		if ( userPolicyBindStrategy.getModifyTime() != null ){
			operationLog.setOperTime(userPolicyBindStrategy.getModifyTime());
		} else {
			operationLog.setOperTime(new Date());
		}
		operationLog.setOperModel(103003);
		if (userPolicyBindStrategy.getOperationType()==1){
			operationLog.setOperType(OperationType.CREATE.getType());
		} else if (userPolicyBindStrategy.getOperationType()==2){
			operationLog.setOperType(OperationType.MODIFY.getType());
		} else if (userPolicyBindStrategy.getOperationType()==3){
			operationLog.setOperType(OperationType.DELETE.getType());
		}

		operationLog.setClientIp(getLocalHost());
		operationLog.setClientPort(serverPort);
		operationLog.setServerName(getLocalHost());
		operationLog.setDataJson("messageNo="+userPolicyBindStrategy.getMessageNo());
		return operationLog;
	}

	/**
	 * 获取当前系统的主机名
	 * @return
	 * @throws UnknownHostException
	 */
	private String getLocalHost() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		}catch (UnknownHostException e) {
			logger.error("获取当前主机host失败",e);
			return "";
		}
	}

}
