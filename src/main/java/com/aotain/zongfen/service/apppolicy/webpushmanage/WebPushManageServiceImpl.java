package com.aotain.zongfen.service.apppolicy.webpushmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.WebPushStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.apppolicy.WebPushStrategyVo;
import com.aotain.zongfen.mapper.apppolicy.PolicyWebPushMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;

@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
public class WebPushManageServiceImpl extends BaseService implements WebPushManageService {
	private static Logger LOG = LoggerFactory.getLogger(WebPushManageServiceImpl.class);
    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;
	@Autowired
	private PolicyWebPushMapper policyWebPushMapper;
	@Autowired
	private PolicyMapper policyMapper;
	@Autowired
	private PolicyStatusMapper policyStatusMapper;
	@Override
	public boolean addWebPushPolicyAndUserBindPolicy(WebPushStrategy webPushStrategy) {
		/*if(webPushStrategy.getAdvWhiteHostListId()==null){
			webPushStrategy.setAdvWhiteHostListId("");
		}*/
		if(webPushStrategy.getTriggerHostListId()==null){
			webPushStrategy.setTriggerHostListId(new ArrayList<>());
		}
		if(webPushStrategy.getTriggerKwListId()==null){
			webPushStrategy.setTriggerKwListId(new ArrayList<>());
		}
		addPolicy(webPushStrategy);
		return true;
	}

	@Override
	public List<WebPushStrategyVo> listData(WebPushStrategyVo vos) {
		List<WebPushStrategyVo> result = policyWebPushMapper.listData(vos);
		// 为每一个实体设置 appPolicy 和 bindPolicy
		for (int i=0;i<result.size();i++){
			WebPushStrategyVo vo = result.get(i);
			PolicyStatus ddosPolicy = new PolicyStatus();
			if(vo.getMessageNo()!=null) {
				PolicyStatus query2 = new PolicyStatus();
				query2.setMessageNo(vo.getMessageNo());
				query2.setMessageType(MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY);
				ddosPolicy = policyStatusMapper.getCountTotalAndFail(query2);
			}
			if(ddosPolicy!=null) {
				vo.setWebPushPolicy(ddosPolicy.getMainCount() == null ? "0/0" : ddosPolicy.getMainCount());
			}else {
				vo.setWebPushPolicy("0/0");
			}
		}
		return result;
	}

	@Override
	public WebPushStrategyVo selectByPrimaryKey(WebPushStrategyVo vo) {
		return policyWebPushMapper.selectByPrimaryKey(vo);
	}

	@Override
	public boolean deletePolicys(List<WebPushStrategy> webPushStrategys) {
		for (int i=0;i<webPushStrategys.size();i++) {
			// 删除本策略
			deletePolicy(webPushStrategys.get(i));
		}
		return true;
	}

	@Override
	public boolean resendPolicy(long topTaskId, long messageNo, boolean needSendBindPolicy, List<String> dpiIp) {
		// 重发主策略
		manualRetryPolicy(topTaskId,0,MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY,messageNo,dpiIp);
		return true;
	}

	@Override
	protected boolean addDb(BaseVO policy) {
		//设置probeType和operateType
		policy.setOperationType(OperationConstants.OPERATION_SAVE);
		policy.setProbeType(ProbeType.DPI.getValue());
		// 设置messageType messageNo messageSeqNo
		int messageType = MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY;
		policy.setMessageType(messageType);
		policy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(messageType));
		policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

		WebPushStrategy webPushStrategy = (WebPushStrategy)policy;

		try {
			WebPushStrategyVo vo = getWebPushStrategyVo(webPushStrategy);
			policyWebPushMapper.insertSelective(vo);
			webPushStrategy.setAdvId(vo.getAdvId());
			Policy policyNo = createPolicyBeanByBaseVo(webPushStrategy);
			policyMapper.insert(policyNo);
		} catch (Exception e) {
			LOG.error("add webpush error,",e);
			return false;
		}
		return true;
	}



	private Policy createPolicyBeanByBaseVo(WebPushStrategy webPushStrategy){
		Policy policyNo = new Policy();
		policyNo.setMessageNo(webPushStrategy.getMessageNo());
		policyNo.setMessageSequenceno(webPushStrategy.getMessageSequenceNo());
		policyNo.setMessageType(webPushStrategy.getMessageType());
		policyNo.setMessageName(webPushStrategy.getMessageName());
		policyNo.setOperateType(webPushStrategy.getOperationType());
		if ( !StringUtils.isEmpty(webPushStrategy.getCreateOper()) ){
			policyNo.setCreateOper(webPushStrategy.getCreateOper());
		}
		if ( !StringUtils.isEmpty(webPushStrategy.getModifyOper()) ){
			policyNo.setModifyOper(webPushStrategy.getModifyOper());
		}
		if ( webPushStrategy.getCreateTime() != null ){
			policyNo.setCreateTime(webPushStrategy.getCreateTime());
		}
		if ( webPushStrategy.getModifyTime() != null ){
			policyNo.setModifyTime(webPushStrategy.getModifyTime());
		}
		return policyNo;
	}

	@Override
	protected boolean deleteDb(BaseVO policy) {
		//设置probeType和operateType
		policy.setOperationType(OperationConstants.OPERATION_DELETE);
		policy.setProbeType(ProbeType.DPI.getValue());
		// 设置messageType messageNo messageSeqNo
		int messageType = MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY;
		policy.setMessageType(messageType);
		policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

		WebPushStrategy webPushStrategy = (WebPushStrategy)policy;
		WebPushStrategyVo vo = getWebPushStrategyVo(webPushStrategy);
		//策略主表是根据messageNo逻辑删除
		//policyWebPushMapper.deleteData(vo);
		Policy policyNo = createPolicyBeanByBaseVo(webPushStrategy);
		policyMapper.updatePolicyByMessageNoAndType(policyNo);
		return true;
	}

	@Override
	public boolean modifyWebPushManageAndUserBindPolicy(WebPushStrategy webPushStrategy) {
		webPushStrategy.setMessageType(MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY);
		if(webPushStrategy.getTriggerHostListId()==null){
			webPushStrategy.setTriggerHostListId(new ArrayList<>());
		}
		if(webPushStrategy.getTriggerKwListId()==null){
			webPushStrategy.setTriggerKwListId(new ArrayList<>());
		}
		modifyPolicy(webPushStrategy);
		return true;
	}

	@Override
	protected boolean modifyDb(BaseVO policy) {
		//设置probeType和operateType
		policy.setOperationType(OperationConstants.OPERATION_UPDATE);
		policy.setProbeType(ProbeType.DPI.getValue());
		// 设置messageType messageSeqNo
		int messageType = MessageTypeConstants.MESSAGE_TYPE_WEB_PUSH_POLICY;
		policy.setMessageType(messageType);
		policy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(messageType));

		WebPushStrategy webPushStrategy = (WebPushStrategy)policy;
		WebPushStrategyVo vo = getWebPushStrategyVo(webPushStrategy);
		policyWebPushMapper.updateByPrimaryKeySelective(vo);
		Policy policyNo = createPolicyBeanByBaseVo(webPushStrategy);
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

	public static WebPushStrategyVo getWebPushStrategyVo(WebPushStrategy webPushStrategy) {
		WebPushStrategyVo vo =new WebPushStrategyVo();
		List<Integer> hostArray = webPushStrategy.getTriggerHostListId();
		String hostList ="";
		if(hostArray!=null&&hostArray.size()>0){
			for(int i=0;i<hostArray.size();i++){
				hostList += hostArray.get(i)+",";
			}
			hostList=hostList.substring(0,hostList.length()-1);
		}
		List<Integer> kwArray = webPushStrategy.getTriggerKwListId();
		String kwList ="";
		if(kwArray!=null&&kwArray.size()>0){
			for(int i=0;i<kwArray.size();i++){
				kwList += kwArray.get(i)+",";
			}
			kwList=kwList.substring(0,kwList.length()-1);
		}
		vo.setMessageNo(webPushStrategy.getMessageNo());
		vo.setAdvType(webPushStrategy.getAdvType());
		vo.setAdvWhiteHostListId(webPushStrategy.getAdvWhiteHostListId());
		vo.setTriggerHostListId(hostList);
		vo.setTriggerKwListId(kwList);
		vo.setAdvFramDPIrl(webPushStrategy.getAdvFramDPIrl());
		vo.setAdvToken(webPushStrategy.getAdvToken());
		vo.setAdvDelay(webPushStrategy.getAdvDelay());
		return vo;
	}

	@Override
	public int isExistBind(Long[] messageNo) {
		Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("array",messageNo);
        uploadMap.put("userBindMessageType",MessageType.WEB_PUSH_POLICY.getId());
        uploadMap.put("operateType",3);
		return userPolicyBindMapper.getByBindMessageNosAndType(uploadMap);
	}

}
