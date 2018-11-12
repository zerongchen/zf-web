package com.aotain.zongfen.service.device;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.DpiDeviceStatusStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.device.DpiStatusQueryMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.utils.SpringUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class DpiStatusService extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(DpiStatusService.class);
	
	@Autowired
	private DpiStatusQueryMapper mapper;

	@Autowired
	private PolicyMapper policyMapper;

	protected boolean addDb(BaseVO vo) {
		if(vo instanceof DpiDeviceStatusStrategy) {
			try {
				//转换
				DpiDeviceStatusStrategy record = (DpiDeviceStatusStrategy) vo ;
				
				String typeMsg = record.getRType() == 1 ? "静态信息" : "动态信息";
				Policy policy = new Policy();
				policy.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				
				long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				
				Date time = new Date();
				policy.setCreateTime(time);
				policy.setModifyTime(time);
				policy.setCreateOper(SpringUtil.getSysUserName());
				policy.setModifyOper(SpringUtil.getSysUserName());
				policy.setMessageNo(messageNo);
				policy.setMessageName(typeMsg+":"+MessageType.DPI_DEVICE_QUERY_POLICY.getMessageType());
				policy.setMessageSequenceno(messageSqNo);
				policy.setFlag((long)0);
				policy.setOperateType(1);
				policyMapper.insertSelective(policy);
				
				record.setProbeType(ProbeType.DPI.getValue());
				record.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				record.setMessageNo(messageNo);
				record.setMessageSequenceNo(messageSqNo);
				record.setOperationType(OperationConstants.OPERATION_SAVE);
				
				mapper.insert(record);
				
				return true;
			} catch (Exception e) {
				logger.error("upload fail",e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO policy) {
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO vo) {
		if(vo instanceof DpiDeviceStatusStrategy) {
			try {
				//转换
				DpiDeviceStatusStrategy record = (DpiDeviceStatusStrategy) vo ;
				
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				
				Policy policy = new Policy();
				Date time = new Date();
				policy.setModifyTime(time);
				policy.setModifyOper(SpringUtil.getSysUserName());
				policy.setMessageNo(record.getMessageNo());
				policy.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				policy.setMessageSequenceno(messageSqNo);
				policy.setOperateType(OperationConstants.OPERATION_UPDATE);
				policyMapper.updatePolicyByMessageNoAndType(policy);
				
				record.setProbeType(ProbeType.DPI.getValue());
				record.setOperationType(OperationConstants.OPERATION_UPDATE);
				record.setMessageType(MessageType.DPI_DEVICE_QUERY_POLICY.getId());
				record.setMessageSequenceNo(messageSqNo); 
				mapper.update(record);
				
				return true;
			} catch (Exception e) {
				logger.error("send policy fail",e);
				return false;
			}
		}
		
		return false;
	}
	/**
     * 各自实现，新增自定义逻辑，主要用于实现与redis相关信息操作
     * 主要用于写StrategySorted_x_x操作顺序信息 写通道信息...
     * addTaskAndChannelToRedis:只要发通道Channel，就得写jobstatus
     * 
     * @param policy
     * @return
     */
	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		return addTaskAndChannelToRedis(policy);
	}
	
	/**
     * 各自实现，修改自定义逻辑，主要用于实现与redis相关信息操作
     * 主要用于写StrategySorted_x_x操作顺序信息 写通道信息...
     * @param policy
     * @return
     */
	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		return addTaskAndChannelToRedis(policy);
	}
	
	/**
     * 各自实现，删除自定义逻辑，主要用于实现与redis相关信息操作
     * 主要用于写StrategySorted_x_x操作顺序信息 写通道信息...
     * @param policy
     * @return
     */
	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return false;
	}
	
	public List<DpiDeviceStatusStrategy> get() {
		return mapper.get();
	}
	
	public DpiDeviceStatusStrategy getOne(Integer rType) {
		return mapper.getOne(rType);
	}

}
