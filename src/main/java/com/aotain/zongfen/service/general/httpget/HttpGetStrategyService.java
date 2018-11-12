package com.aotain.zongfen.service.general.httpget;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.HttpGetStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.zongfen.mapper.general.ClassInfoMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.general.ClassInfo;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 
* @ClassName: HttpGetStrategyService 
* @Description: 用来下发HTTPGET策略的service(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年2月5日 上午9:30:47 
*
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class HttpGetStrategyService extends BaseService {


	private Logger logger = LoggerFactory.getLogger(HttpGetStrategyService.class);
	@Autowired
	private PolicyMapper policyMapper;

	@Autowired
	private ClassInfoMapper classInfoMapper;

	/**
	 * httpget策略不操作DB
	 */
	@Override
	protected boolean addDb(BaseVO vo) {
		if(vo instanceof HttpGetStrategy) {
			try {
				HttpGetStrategy strategy = (HttpGetStrategy) vo;
				Policy policy = new Policy();
				policy.setMessageType(MessageType.HTTP_GET_POLICY.getId());
				
				long messageNo = vo.getMessageNo();
				long messageSqNo = vo.getMessageSequenceNo();
				
				Date time = new Date();
				policy.setCreateTime(time);
				policy.setModifyTime(time);
				policy.setCreateOper(SpringUtil.getSysUserName());
				policy.setModifyOper(SpringUtil.getSysUserName());
				
				policy.setMessageNo(messageNo);
				policy.setMessageName(MessageType.HTTP_GET_POLICY.getMessageType());
				policy.setMessageSequenceno(messageSqNo);
				policy.setFlag((long)0);
				policy.setOperateType(1);
				
				policyMapper.insertSelective(policy);
				
				vo.setProbeType(ProbeType.DPI.getValue());
				
				ClassInfo obj = new  ClassInfo();
				obj.setMessageType(MessageType.HTTP_GET_POLICY.getId());
				obj.setMessageNo(messageNo);
				obj.setZongfenId(strategy.getZongfenId());
				classInfoMapper.insertSelective(obj);
				
				return true;
				
			}catch(Exception e) {
				logger.error("add http strategy error",e);
				return false;
			}
		}
		return false;
	}

	/**
	 * httpget策略不操作DB
	 */
	@Override
	protected boolean deleteDb(BaseVO vo) {
		if(vo instanceof HttpGetStrategy) {
			
			try {
				Policy policy = new Policy();
				policy.setMessageType(MessageType.HTTP_GET_POLICY.getId());
				long messageNo = vo.getMessageNo();
				policy.setMessageNo(messageNo);
				policyMapper.deleteByMessageNoAndType(policy);
				vo.setProbeType(ProbeType.DPI.getValue());
				return true;
			} catch (Exception e) {
				logger.error("delete http strategy error",e);
				return false;
			}
		}
		return false;
	}

	/**
	 * httpget策略不操作DB
	 */
	@Override
	protected boolean modifyDb(BaseVO policy) {
		return true;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy);
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy);
	}

}
