package com.aotain.zongfen.service.general.usergroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.config.redis.BaseRedisService;
import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.constant.UserGroupAction;
import com.aotain.common.policyapi.model.UserGroupStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.UserMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.zongfen.mapper.general.user.UserGroupMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserGroupStrategyService extends BaseService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private PolicyMapper policyMapper;
	
    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(UserGroupStrategyService.class);
	 /**
     * userInfo的messageNo存在redis中的key
     */
    private static String REDIS_USER_MESSAGE_NO_HASH_KEY = "userInfoMN_%d";
    
    /**
     * userInfo的messageSequenceNo存在redis中的key
     */
    private static String REDIS_USER_MESSAGE_SEQ_NO_HASH_KEY = "userInfoMSN_%d";
    
    @Autowired
    private BaseRedisService<String, String, String> rediscluster;

	@Override
	protected boolean addDb(BaseVO vo) {
		if(vo instanceof UserGroupStrategy) {
			try {
				Policy policy = new Policy();
				policy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
				long messageNo = 0L;
				if (vo.getMessageNo()==null){
					messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.USER_GROUP_POLICY.getId());
				} else {
					messageNo = vo.getMessageNo();
				}

				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_GROUP_POLICY.getId());
				
				Date time = new Date();
				policy.setCreateTime(time);
				policy.setModifyTime(time);
				policy.setCreateOper(SpringUtil.getSysUserName());
				policy.setModifyOper(SpringUtil.getSysUserName());
				
				policy.setMessageNo(messageNo);
				policy.setMessageName(MessageType.USER_GROUP_POLICY.getMessageType());
				policy.setMessageSequenceno(messageSqNo);
				policy.setFlag((long)0);
				policy.setOperateType(vo.getOperationType());
				
				policyMapper.insertSelective(policy);
				
				vo.setProbeType(ProbeType.DPI.getValue());
				vo.setMessageSequenceNo(messageSqNo);
				vo.setMessageNo(messageNo);
				return true;
				
			}catch(Exception e) {
				logger.error("add user group strategy error ",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO vo) {
		if(vo instanceof UserGroupStrategy) {
			try {
				Policy policy = new Policy();
				policy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_GROUP_POLICY.getId());
				policy.setMessageNo(vo.getMessageNo());
				policy.setMessageSequenceno(messageSqNo);
				policyMapper.deletePolicyByMesNoAndType(policy);
				vo.setProbeType(ProbeType.DPI.getValue());
				vo.setMessageSequenceNo(messageSqNo);
				return true;
				
			}catch(Exception e) {
				logger.error("delete user group strategy error ",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO vo) {
		if(vo instanceof UserGroupStrategy) {
			try {
				Policy policy = new Policy();
				policy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
				
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_GROUP_POLICY.getId());
				
				Date time = new Date();
				policy.setModifyTime(time);
				policy.setModifyOper(SpringUtil.getSysUserName());
				
				policy.setMessageNo(vo.getMessageNo());
				policy.setMessageSequenceno(messageSqNo);
				policy.setFlag((long)0);
				policy.setOperateType(vo.getOperationType());
				
				policyMapper.updatePolicyByMessageNoAndType(policy);
				
				vo.setProbeType(ProbeType.DPI.getValue());
				vo.setMessageSequenceNo(messageSqNo);
				return true;
				
			}catch(Exception e) {
				logger.error("modify user group strategy error ",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		if(setUserGroupHashKey(policy)){
			return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
		}
		else return false;
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		if(modifyUserGroupHashKey(policy)){
			return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
		}else return false;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return deleteUserGroupHashKey(policy) && setPolicyOperateSequenceToRedis(policy);
	}
	/**
	 * 
	* @Title: setUserGroupHashKey 
	* @Description: 设置userInfo的redis信息(这里用一句话描述这个方法的作用) 
	* @param @param policy
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	public boolean setUserGroupHashKey(BaseVO policy) {
		  try{
			  if(policy instanceof UserGroupStrategy) {
				  UserGroupStrategy strategy = (UserGroupStrategy) policy;
				  //MessageNo Key
				  String mnKey = String.format(REDIS_USER_MESSAGE_NO_HASH_KEY, policy.getMessageNo());
				  //MessageSquenceNo Key
				  String msnKey = String.format(REDIS_USER_MESSAGE_SEQ_NO_HASH_KEY, policy.getMessageSequenceNo());
				  List<UserMessage> userInfoList = strategy.getUserInfo(); 
				  if(userInfoList != null && userInfoList.size() > 0) {
					  for(UserMessage userInfo : userInfoList) {
						 
						  String hashKey = userInfo.getUserName();
						  
						  rediscluster.putHash(mnKey, hashKey, userInfo.getUserType().toString());
						  rediscluster.putHash(msnKey, hashKey, userInfo.getUserType().toString());
						  rediscluster.expire(msnKey,7, TimeUnit.DAYS);
						  logger.info("user infotion message_no key on the redis, mnKey = "+mnKey+", its msg = "+ userInfo.getUserType().toString() + " userName"+userInfo.getUserName().toString() );
						  logger.info("user infotion message_sequence_no key on the redis, msnKey = "+msnKey+", its msg = "+ userInfo.getUserType().toString()+" userName"+userInfo.getUserName().toString());
					  }
					  return true;
					  
				  }else {
					  logger.error("usergroup has no users!"); 
					  return false;
				  }
			  }
	        }catch(Exception e){
	            logger.error("policy put to redis hash error! msg=" + policy.objectToJson(), e);
	            MonitorStatisticsUtils.addEvent(e);
	            return false;
	        }
		return false;
	}

	/**
	 *
	 * @Title: modifyUserGroupHashKey
	 * @Description: 设置userInfo的redis信息,对MN表进行增加或者删除 (这里用一句话描述这个方法的作用)
	 * @param @param policy
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 * @throws
	 */
	public boolean modifyUserGroupHashKey(BaseVO policy) {
		try{
			if(policy instanceof UserGroupStrategy) {
				UserGroupStrategy strategy = (UserGroupStrategy) policy;
				//MessageNo Key
				String mnKey = String.format(REDIS_USER_MESSAGE_NO_HASH_KEY, policy.getMessageNo());
				//MessageSquenceNo Key
				String msnKey = String.format(REDIS_USER_MESSAGE_SEQ_NO_HASH_KEY, policy.getMessageSequenceNo());
				List<UserMessage> userInfoList = strategy.getUserInfo();
				Integer action = strategy.getAction();
				if(userInfoList != null && userInfoList.size() > 0) {
					for(UserMessage userInfo : userInfoList) {

						String hashKey = userInfo.getUserName();
						if(action == UserGroupAction.DELETE.getValue()){
							rediscluster.removeHash(mnKey, hashKey);
						}else {
							rediscluster.putHash(mnKey, hashKey, userInfo.getUserType().toString());
							rediscluster.expire(msnKey,7, TimeUnit.DAYS);
						}
						rediscluster.putHash(msnKey, hashKey, userInfo.getUserType().toString());
						logger.info("user infotion message_no key on the redis, mnKey = "+mnKey+", its msg = "+ userInfo.getUserType().toString() + " userName"+userInfo.getUserName().toString() );
						logger.info("user infotion message_sequence_no key on the redis, msnKey = "+msnKey+", its msg = "+ userInfo.getUserType().toString()+" userName"+userInfo.getUserName().toString());
					}
					return true;

				}else {
					logger.error("usergroup has no users!");
					return false;
				}
			}
		}catch(Exception e){
			logger.error("policy put to redis hash error! msg=" + policy.objectToJson(), e);
			MonitorStatisticsUtils.addEvent(e);
			return false;
		}
		return false;
	}

	/**
	 *
	 * @Title: deleteUserGroupHashKey
	 * @Description: 设置userInfo的redis信息,对MN表进行删除 (这里用一句话描述这个方法的作用)
	 * @param @param policy
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 * @throws
	 */
	public boolean deleteUserGroupHashKey(BaseVO policy) {
		try{
			if(policy instanceof UserGroupStrategy) {
				UserGroupStrategy strategy = (UserGroupStrategy) policy;
				//MessageNo Key
				String mnKey = String.format(REDIS_USER_MESSAGE_NO_HASH_KEY, policy.getMessageNo());
				//MessageSquenceNo Key
				String msnKey = String.format(REDIS_USER_MESSAGE_SEQ_NO_HASH_KEY, policy.getMessageSequenceNo());
				List<UserMessage> userInfoList = strategy.getUserInfo();
				if(userInfoList != null && userInfoList.size() > 0) {
					for(UserMessage userInfo : userInfoList) {

						String hashKey = userInfo.getUserName();
						rediscluster.remove(mnKey);
						rediscluster.putHash(msnKey, hashKey, userInfo.getUserType().toString());
						rediscluster.expire(msnKey,7, TimeUnit.DAYS);
						logger.info("user infotion message_no key on the redis, mnKey = "+mnKey+", its msg = "+ userInfo.getUserType().toString() + " userName = "+userInfo.getUserName().toString() );
						logger.info("user infotion message_sequence_no key on the redis, msnKey = "+msnKey+", its msg = "+ userInfo.getUserType().toString()+" userName = "+userInfo.getUserName().toString());
					}
					return true;

				}else {
					logger.error("usergroup has no users!");
					return false;
				}
			}
		}catch(Exception e){
			logger.error("policy put to redis hash error! msg=" + policy.objectToJson(), e);
			MonitorStatisticsUtils.addEvent(e);
			return false;
		}
		return false;
	}

}
