package com.aotain.zongfen.service.apppolicy.flowdirection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aotain.zongfen.utils.DateUtils;
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
import com.aotain.common.policyapi.model.ApplicationFlowStrategy;
import com.aotain.common.policyapi.model.IpAddressArea;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.AreaGroup;
import com.aotain.common.policyapi.model.msg.AreaGroupAS;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.mapper.apppolicy.AreaGroupASMapper;
import com.aotain.zongfen.mapper.apppolicy.AreaGroupMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.Getter;
import lombok.Setter;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class AppFlowDirectionStrategyService extends BaseService  {

	private static final Logger logger = LoggerFactory.getLogger(AppFlowDirectionStrategyService.class);
	 
	@Autowired
    private PolicyMapper policyMapper;
	
	@Autowired
	private AreaGroupMapper areaGroupMapper;
	
	@Autowired
	private AreaGroupASMapper areaGroupASMapper;
	
	@Autowired
    private PolicyStatusMapper policyStatusMapper;
	
	enum FlowDirectionSuffix{
		TEC("0000000000000000","电信网内"),
		IAI("000000000000","互联互通");
		
		@Getter
		@Setter
		private String value;
		
		@Getter
		@Setter
		private String description;
		
		FlowDirectionSuffix(String value, String description){
			this.value=value;
			this.description=description;
		}
		
	}
	
	/**
	 * 获取策略的列表
	 * @param messageName
	 * @return
	 */
	public PageResult<ApplicationFlowStrategy> getList(Integer pageIndex,Integer pageSize, String messageName,Policy record) {
		//计算总数
//		Policy record = new Policy();
		record.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
		record.setMessageName(messageName==null ? "" : messageName);
//		long count = policyMapper.count(record);
		
		PageResult<ApplicationFlowStrategy> result = new PageResult<ApplicationFlowStrategy>();
		PageHelper.startPage(pageIndex, pageSize);

		if (record.getSearchEndTime()!=null){
			record.setSearchEndTime(DateUtils.addDateOfMonth(record.getSearchEndTime(),1));
		}

		List<Policy> policyList = policyMapper.selectList(record);
		PageInfo pageInfo = new PageInfo(policyList);
		result.setTotal(pageInfo.getTotal());

		ApplicationFlowStrategy strategy = null;
		
		List<ApplicationFlowStrategy> strategyList = new ArrayList<ApplicationFlowStrategy>();
		Long messageNo = 0L;
		AreaGroup areaGroup = null;
		if(policyList != null) {
			for(Policy policy : policyList) {
				messageNo = policy.getMessageNo();
				
				strategy = new ApplicationFlowStrategy();
				strategy.setModifyOper(policy.getModifyOper());
				strategy.setCreateyTime(policy.getCreateTimeStr());
				strategy.setModifyTime(policy.getModifyTimeStr());
				strategy.setMessageName(policy.getMessageName());
				strategy.setMessageNo(messageNo);
				strategy.setMessageSequenceNo(policy.getMessageNo());
				
				PolicyStatus policyStatus = new PolicyStatus();
				policyStatus.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				policyStatus.setMessageNo(policy.getMessageNo());
				PolicyStatus ps = policyStatusMapper.getCountFailForMain(policyStatus);
				//统计下发成功和失败次数
				if(ps==null) {
					strategy.setPolicyCount("0/0");
				}else {
					strategy.setPolicyCount(ps.getMainCount()==null?"0/0":ps.getMainCount());
				}
				AreaGroup g = null;
				try {
					g = areaGroupMapper.selectInternal(messageNo);
					//获取来源和目的区域的字符串
					if(g!=null){
						strategy.setInternalAreaGroupInfoStr(g.getInternalAreaStr());
					}
				} catch (Exception e) {
					logger.error(" ",e);
				}
				try {
					g = areaGroupMapper.selectExternal(messageNo);
					//获取来源和目的区域的字符串
					if(g!=null){
						strategy.setExternalAreaGroupInfoStr(g.getExternalAreaStr());
					}
				} catch (Exception e) {
					logger.error(" ",e);
				}
			//	strategy.setExternalAreaGroupInfoStr(areaGroupMapper.selectExternal(messageNo).getExternalAreaStr());
				//---------来源-------
				//1：IDC	
				areaGroup = new AreaGroup();
				areaGroup.setMessageNo(messageNo);
				areaGroup.setAreaType((byte) 0);
				areaGroup.setAreaSubid1("001");
				areaGroup.setAreaSubid2("10");		
				strategy.setInternalIdcGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//2:城域网
				areaGroup.setAreaSubid2("01");	
				strategy.setInternalAreaGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//---------目的----------
				
				//1：IDC			
				areaGroup.setAreaType((byte) 1);
				areaGroup.setAreaSubid1("001");
				areaGroup.setAreaSubid2("10");		
				strategy.setExternalIdcGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//2:城域网
				areaGroup.setAreaSubid2("01");	
				strategy.setExternalAreaGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				/****************
				 * 需求待确定***
				 **************/
				
				strategyList.add(strategy);
			}
			PageInfo<ApplicationFlowStrategy> pageResult = new PageInfo<ApplicationFlowStrategy>(strategyList);
			
			
			result.setRows(strategyList);
			return result;
		}
		return null;
	}
	
	/**
	 * 核心方法，假设zTree里可以传进的值：id（地市ID）、pid（省份）、name(区域名称) 、isparent，记住要把父节点剔除
	 * 并且写数据到Area_group和area_group_as
	 * zf_v2_policy_areagroup ： MESSAGE_NO AREAGROUP_ID AREAGROUP_NAME AREA_TYPE AREA_SUBID1 AREA_SUBID2 AREA_SUBID3 AREA_SUBID4
	 * zf_v2_policy_areagroup_as ： AREAGROUP_ID  AS_TYPE（0=id，1=name）  AS_AREA_ID
	 * @return
	 */
	private List<AreaGroup> IpAreaToAreaGroup(Byte areaType, String areaSubId1,String areaSubId2,Long messageNo,List<IpAddressArea> ipAreaList){
		if(ipAreaList != null ) {
			List<AreaGroup> areaGroupList = new ArrayList<AreaGroup>();
			AreaGroup areaGroup = null;
			for(IpAddressArea iparea : ipAreaList) {
				if(iparea.getIsParent()!=null&&iparea.getIsParent()) {
					continue;
				}else {
					String areaSubId3 = iparea.getPareaId();
					String areaSubId4 = iparea.getAreaId();
					Long areaGroupId = Long.parseLong(areaSubId1 + areaSubId2 + areaSubId3 + areaSubId4 + FlowDirectionSuffix.TEC.getValue(), 2);
					areaGroup = new AreaGroup();
					areaGroup.setMessageNo(messageNo);
				//	areaGroup.setAreagroupId(areaGroupId);
					areaGroup.setAreaGroupId(areaGroupId);
					areaGroup.setAreaType(areaType);
					areaGroup.setAreagroupName(iparea.getAreaName());
					areaGroup.setAreaSubid1(areaSubId1);
					areaGroup.setAreaSubid2(areaSubId2);
					areaGroup.setAreaSubid3(areaSubId3);
					areaGroup.setAreaSubid4(areaSubId4);
					
					//设置zf_v2_policy_areagroup_as
					List<AreaGroupAS> areaGroupASList = new ArrayList<AreaGroupAS>();
					List<Long> asIds = new ArrayList<Long>();
					List<String> areaNames = new ArrayList<String>();
					//名字
					AreaGroupAS areaGroupAS = new AreaGroupAS();
					areaGroupAS.setAreagroupId(areaGroupId);
					areaGroupAS.setAreaId(areaGroupId);
					areaGroupAS.setAreaName(iparea.getAreaName());
					areaGroupAS.setAsType(1);
					areaGroupAS.setAsAreaId(iparea.getAreaName());
					areaGroupASList.add(areaGroupAS);
					//ID
					areaGroupAS = new AreaGroupAS();
					areaGroupAS.setAreagroupId(areaGroupId);
					areaGroupAS.setAreaId(areaGroupId);
					areaGroupAS.setAreaName(iparea.getAreaName());
					areaGroupAS.setAsType(0);
					areaGroupAS.setAsAreaId(Long.toString(areaGroupId));
					areaGroupASList.add(areaGroupAS);
					
					//asIds.add(areaGroupId);
					areaNames.add(iparea.getAreaName());
					
					areaGroup.setAsIds(asIds);
					areaGroup.setAreaNames(areaNames);
					areaGroup.setAreaGroupAsList(areaGroupASList);
					areaGroupList.add(areaGroup);
				}
			}
			return areaGroupList;
		}
		return null;
	} 
	/**
	 * 核心方法,用来展示和记录被选中的树形数据
	 * @param areaList
	 * @return
	 */
	private List<IpAddressArea> AreaGroupToIpArea( List<AreaGroup> areaList){
		if(areaList != null) {
			List<IpAddressArea> ipAreaList = new ArrayList<IpAddressArea>();
			IpAddressArea ipArea = null;
			for(AreaGroup areaGroup : areaList) {
				ipArea = new IpAddressArea();
				ipArea.setAreaId(areaGroup.getAreaSubid4());
				ipArea.setPareaId(areaGroup.getAreaSubid3());
				ipArea.setAreaName(areaGroup.getAreagroupName());
				ipAreaList.add(ipArea);
			}
			return ipAreaList;
		}
		return null;
	}
	
	@Override
	protected boolean addDb(BaseVO vo) {
		if(vo instanceof ApplicationFlowStrategy){
			try {
				ApplicationFlowStrategy strategy = (ApplicationFlowStrategy) vo;
				Policy policy = new Policy();
				policy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				
				long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				
				Date time = new Date();
				policy.setCreateTime(time);
				policy.setModifyTime(time);
				policy.setCreateOper(SpringUtil.getSysUserName());
				policy.setModifyOper(SpringUtil.getSysUserName());
				    
				policy.setMessageNo(messageNo);
				policy.setMessageName(strategy.getMessageName());
				policy.setMessageSequenceno(messageSqNo);
				policy.setOperateType(OperationConstants.OPERATION_SAVE);
				
				policyMapper.insertSelective(policy);
				
				List<AreaGroup> internalAreaGroupInfo = new ArrayList<AreaGroup>();
				List<AreaGroup> externalAreaGroupInfo = new ArrayList<AreaGroup>();
				
				//---------来源-------
				//1：IDC	
				if(null != strategy.getInternalIdcGroupList()) {
					internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","10", messageNo, strategy.getInternalIdcGroupList()));
				}
				//2:城域网//
				if(null != strategy.getInternalAreaGroupList()) {
					internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","01", messageNo, strategy.getInternalAreaGroupList()));
				}
				//---------目的----------
				if(null != strategy.getExternalIdcGroupList()) {
					externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","10", messageNo, strategy.getExternalIdcGroupList()));
				}
				//2:城域网 
				if(null != strategy.getExternalAreaGroupList()) {
					externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","01", messageNo, strategy.getExternalAreaGroupList()));
				}
				//=====设置来源和目的=====.
				if(internalAreaGroupInfo.size() == 0 ) {
					logger.error("--internalArea is null--");
					return false;
				}
				if(externalAreaGroupInfo.size() == 0) {
					logger.error("--externalArea is null--");
					return false;
				}
				
				strategy.setProbeType(ProbeType.DPI.getValue());
				strategy.setOperationType(OperationConstants.OPERATION_SAVE);
				strategy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				strategy.setMessageNo(messageNo);
				strategy.setMessageSequenceNo(messageSqNo);
				
				if(internalAreaGroupInfo != null && externalAreaGroupInfo!= null) {
					strategy.setInternalAreaGroupInfo(internalAreaGroupInfo );
					strategy.setExternalAreaGroupInfo(externalAreaGroupInfo);
					
					areaGroupMapper.insertList(internalAreaGroupInfo);
					areaGroupMapper.insertList(externalAreaGroupInfo);
					
					for(AreaGroup ag : internalAreaGroupInfo) {
						areaGroupASMapper.insertList(ag.getAreaGroupAsList());
					}
					logger.info("--insert internalArea success!--");
					for(AreaGroup ag : externalAreaGroupInfo) {
						areaGroupASMapper.insertList(ag.getAreaGroupAsList());
					}
					logger.info("--insert externalArea success!--");
					return true;
				}
				
			} catch (Exception e) {
				logger.error("error:"+e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean deleteDb(BaseVO vo) {
		if(vo instanceof ApplicationFlowStrategy) {
			try {
				ApplicationFlowStrategy strategy = (ApplicationFlowStrategy) vo;
				long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.APP_FLOW_DIRECTION_POLICY.getId());

				AreaGroup areaGroup=null;
				//---------来源-------
				//1：IDC
				areaGroup = new AreaGroup();
				areaGroup.setMessageNo(strategy.getMessageNo());
				areaGroup.setAreaType((byte) 0);
				areaGroup.setAreaSubid1("001");
				areaGroup.setAreaSubid2("10");
				strategy.setInternalIdcGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//2:城域网
				areaGroup.setAreaSubid2("01");
				strategy.setInternalAreaGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//---------目的----------

				//1：IDC
				areaGroup.setAreaType((byte) 1);
				areaGroup.setAreaSubid1("001");
				areaGroup.setAreaSubid2("10");
				strategy.setExternalIdcGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));
				//2:城域网
				areaGroup.setAreaSubid2("01");
				strategy.setExternalAreaGroupList(AreaGroupToIpArea(areaGroupMapper.selectByNoAndType(areaGroup)));

				List<AreaGroup> internalAreaGroupInfo = new ArrayList<AreaGroup>();
				List<AreaGroup> externalAreaGroupInfo = new ArrayList<AreaGroup>();
				//1：IDC
				if(null != strategy.getInternalIdcGroupList()) {
					internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","10", strategy.getMessageNo(), strategy.getInternalIdcGroupList()));
				}
				//2:城域网//
				if(null != strategy.getInternalAreaGroupList()) {
					internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","01", strategy.getMessageNo(), strategy.getInternalAreaGroupList()));
				}
				//---------目的----------
				if(null != strategy.getExternalIdcGroupList()) {
					externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","10", strategy.getMessageNo(), strategy.getExternalIdcGroupList()));
				}
				//2:城域网
				if(null != strategy.getExternalAreaGroupList()) {
					externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","01", strategy.getMessageNo(), strategy.getExternalAreaGroupList()));
				}

				if(internalAreaGroupInfo != null && externalAreaGroupInfo!= null) {
					strategy.setInternalAreaGroupInfo(internalAreaGroupInfo);
					strategy.setExternalAreaGroupInfo(externalAreaGroupInfo);
				}

				Policy policy = new Policy();
				policy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				policy.setOperateType(OperationConstants.OPERATION_DELETE);
				policy.setMessageNo(strategy.getMessageNo());
				policy.setMessageSequenceno(messageSqNo);
				policyMapper.deletePolicyByMesNoAndType(policy);

				strategy.setProbeType(ProbeType.DPI.getValue());
				strategy.setMessageSequenceNo(messageSqNo);
				strategy.setOperationType(OperationConstants.OPERATION_DELETE);
				strategy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				//先删除area_as
				areaGroupASMapper.deleteByMessageNo(strategy.getMessageNo());
				//再删除area
				areaGroupMapper.deleteByMessageNo(strategy.getMessageNo());
				return true;
			} catch (Exception e) {
				logger.error("error:"+e);
				return false;
			}
		}
		return false;
	}
	/**
	 * 策略的重发
	 * @param strategy
	 * @return
	 */
	public boolean resendPolicy(ApplicationFlowStrategy strategy) {

		if (strategy.getMessageNo() == null) {
			return false;
		} else {
			// 重发主策略
			manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.APP_FLOW_DIRECTION_POLICY.getId(),
					strategy.getMessageNo(), new ArrayList<>());
			return true;
		}
	}
	  
	@Override
	protected boolean modifyDb(BaseVO vo) {
		if(vo instanceof ApplicationFlowStrategy) {
			ApplicationFlowStrategy strategy = (ApplicationFlowStrategy) vo;
			long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
			
			Policy policy = new Policy();
			Date time = new Date();
			policy.setCreateTime(time);
			policy.setModifyTime(time);
			policy.setCreateOper(SpringUtil.getSysUserName());
			policy.setModifyOper(SpringUtil.getSysUserName());
			    
			policy.setMessageNo(strategy.getMessageNo());
			policy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());

			policy.setMessageName(strategy.getMessageName());
			policy.setMessageSequenceno(messageSqNo);
			policy.setOperateType(OperationConstants.OPERATION_UPDATE);
			
			policyMapper.updatePolicyByMessageNoAndType(policy);
			
			
			//先删除area_as
			areaGroupASMapper.deleteByMessageNo(strategy.getMessageNo());
			//再删除area
			areaGroupMapper.deleteByMessageNo(strategy.getMessageNo());
			
			
			
			List<AreaGroup> internalAreaGroupInfo = new ArrayList<AreaGroup>();
			List<AreaGroup> externalAreaGroupInfo = new ArrayList<AreaGroup>();
			//---------来源-------
			//1：IDC	
			if(null != strategy.getInternalIdcGroupList()) {
				internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","10", strategy.getMessageNo(), strategy.getInternalIdcGroupList()));
			}
			//2:城域网//
			if(null != strategy.getInternalAreaGroupList()) {
				internalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 0, "001","01", strategy.getMessageNo(), strategy.getInternalAreaGroupList()));
			}
			//---------目的----------
			if(null != strategy.getExternalIdcGroupList()) {
				externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","10", strategy.getMessageNo(), strategy.getExternalIdcGroupList()));
			}
			//2:城域网 
			if(null != strategy.getExternalAreaGroupList()) {
				externalAreaGroupInfo.addAll(IpAreaToAreaGroup((byte) 1, "001","01", strategy.getMessageNo(), strategy.getExternalAreaGroupList()));
			}
			//=====设置来源和目的=====.
			if(internalAreaGroupInfo.size() == 0 ) {
				logger.error("--internalArea is null--");
				return false;
			}
			if(externalAreaGroupInfo.size() == 0) {
				logger.error("--externalArea is null--");
				return false;
			}
			
			strategy.setProbeType(ProbeType.DPI.getValue());
			strategy.setOperationType(OperationConstants.OPERATION_UPDATE);
			strategy.setMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
			strategy.setMessageNo(strategy.getMessageNo());
			strategy.setMessageSequenceNo(messageSqNo);
			
			if(internalAreaGroupInfo != null && externalAreaGroupInfo!= null) {
				strategy.setInternalAreaGroupInfo(internalAreaGroupInfo );
				strategy.setExternalAreaGroupInfo(externalAreaGroupInfo);
				
				areaGroupMapper.insertList(internalAreaGroupInfo);
				areaGroupMapper.insertList(externalAreaGroupInfo);
				
				for(AreaGroup ag : internalAreaGroupInfo) {
					areaGroupASMapper.insertList(ag.getAreaGroupAsList());
				}
				logger.info("--insert internalArea success!--");
				for(AreaGroup ag : externalAreaGroupInfo) {
					areaGroupASMapper.insertList(ag.getAreaGroupAsList());
				}
				logger.info("--insert externalArea success!--");
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO vo) {
		return setPolicyOperateSequenceToRedis(vo) && addTaskAndChannelToRedis(vo);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO vo) {
		return setPolicyOperateSequenceToRedis(vo);
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO vo) {
		return setPolicyOperateSequenceToRedis(vo);
	}

}
