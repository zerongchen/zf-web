package com.aotain.zongfen.service.general.classinfo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.model.ClassInfoLibsStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.zongfen.mapper.general.ClassInfoMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.general.ClassInfo;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class ClassInfoLibsStrategyService extends BaseService {


	private  Logger logger = LoggerFactory.getLogger(ClassInfoLibsStrategyService.class);
	@Autowired
	private PolicyMapper policyMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

	@Override
	protected boolean addDb(BaseVO vo) {
		if(vo instanceof ClassInfoLibsStrategy) {
			try {
				Integer messageType = vo.getMessageType();
				Policy policy = new Policy();
				policy.setMessageType(messageType);
				policy.setMessageNo(vo.getMessageNo());
				policy.setMessageName(MessageType.getTypeById(messageType));
				Date time = new Date();
				policy.setCreateTime(time);
				policy.setModifyTime(time);
				policy.setCreateOper(SpringUtil.getSysUserName());
				policy.setModifyOper(SpringUtil.getSysUserName());
				policy.setMessageSequenceno(vo.getMessageSequenceNo());
				policy.setFlag((long)0);
				policy.setOperateType(1);
				policyMapper.insertSelective(policy);
				ClassInfoLibsStrategy classInfo = (ClassInfoLibsStrategy) vo;
				ClassInfo obj = new  ClassInfo();
				obj.setMessageType(classInfo.getMessageType());
				obj.setMessageNo(classInfo.getMessageNo());
				obj.setZongfenId(classInfo.getZongfenId());
				classInfoMapper.insertSelective(obj);
				return true;
			} catch (Exception e) {
				logger.error("add class info strategy error",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO vo) {
		if(vo instanceof ClassInfoLibsStrategy) {
			try {
				Policy policy = new Policy();
				policy.setMessageType(vo.getMessageType());
				long messageNo = MessageNoUtil.getInstance().getLastMessageNo(vo.getMessageType());
				policy.setMessageNo(messageNo);
				policyMapper.deleteByMessageNoAndType(policy);
				return true;
			} catch (Exception e) {
				logger.error("delete class info strategy error",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		return true;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
	}

}
