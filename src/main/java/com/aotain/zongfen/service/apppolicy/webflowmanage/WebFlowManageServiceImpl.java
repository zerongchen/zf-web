package com.aotain.zongfen.service.apppolicy.webflowmanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.WebFlowStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.cache.CommonCache;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.apppolicy.WebFlowManageMapper;
import com.aotain.zongfen.mapper.apppolicy.WebFlowManageWebTypeMapper;
import com.aotain.zongfen.mapper.apppolicy.WebFlowUserGroupMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.apppolicy.WebFlowManage;
import com.aotain.zongfen.model.apppolicy.WebFlowManageWebType;
import com.aotain.zongfen.model.apppolicy.WebFlowUserGroup;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
public class WebFlowManageServiceImpl extends BaseService implements WebFlowManageService {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebFlowManageServiceImpl.class);

	@Autowired
	private WebFlowManageMapper policyWebflowMapper;
	
    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;
    
    @Autowired
    private WebFlowManageWebTypeMapper webFlowManageWebTypeMapper;
    
    @Autowired 
    private WebFlowUserGroupMapper webFlowUserGroupMapper;
    
    @Autowired
    private UserPolicyBindService userPolicyBindService;
    
    @Autowired
    private CommonCache commonCache;
    
    @Autowired
    private PolicyStatusMapper policyStatusMapper;
	
	@Override
	public PageResult<WebFlowManage> getWebFlowData(Integer pageSize, Integer pageIndex, String policyName,String startTime,String endTime) {
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("messageName", policyName);
		query.put("startTime", startTime);
		query.put("endTime", endTime);
		PageResult<WebFlowManage> result = new PageResult<WebFlowManage>();
		PageHelper.startPage(pageIndex, pageSize);
		List<WebFlowManage> info = policyWebflowMapper.getIndexList(query);
		PageInfo<WebFlowManage> pageResult = new PageInfo<WebFlowManage>(info);
		for (WebFlowManage dto:info){
			dto.setRStartTime(DateUtils.parse2DateString(Long.valueOf(dto.getStartTime()),DateFormatConstant.DATE_CHS_HYPHEN));
            dto.setREndTime(DateUtils.parse2DateString(Long.valueOf(dto.getEndTime()),DateFormatConstant.DATE_CHS_HYPHEN));
            String webType = "";
            for(WebFlowManageWebType webtype : dto.getWebTypes()) {
            	if("".equals(webType)) {
            		webType = webtype.getWebTypeName();
            	}else {
            		webType = webType+","+webtype.getWebTypeName();
            	}
            }
            dto.setWebType(webType);
			dto.setType(0);
            PolicyStatus appPolicy = new PolicyStatus();
            if(dto.getMessageNo()!=null) {
            	PolicyStatus query2 = new PolicyStatus();
            	query2.setMessageNo(dto.getMessageNo());
            	query2.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
            	appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
                long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
    			if(dto.getStartTime().equals("0") || (now>=Long.valueOf(dto.getStartTime()) &&
    					(dto.getEndTime().equals("0") || now <= (Long.valueOf(dto.getEndTime())+86400)))) {
    				dto.setType(1);
    			}
            }
            if(appPolicy!=null) {
            	dto.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
            	dto.setBindPolicy(appPolicy.getBindCount() == null ? "0/0" : appPolicy.getBindCount());
            }else{
            	dto.setAppPolicy("0/0");
            	dto.setBindPolicy("0/0");
            }
        	
		}
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> webFlowSave(WebFlowManage webFlow) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		if(webFlow.getWebflowId() == null) {
			if(policyWebflowMapper.isSamePolicyName(webFlow)>0) {
				result.setResult(0);
				result.setMessage("存在相同名称的策略！");
				return result;
			}else {
				WebFlowStrategy webFlowStrategy =new WebFlowStrategy();
				//开始时间处理
				if(webFlow.getStartTime()==null) {
					webFlow.setStartTime("0");
				}else {
					webFlow.setStartTime(DateUtils.parse2TimesTamp(webFlow.getStartTime(),DateFormatConstant.DATE_CHS_HYPHEN).toString());
				}
				//结束时间处理
				if(webFlow.getEndTime()==null) {
					webFlow.setEndTime("0");
				}else {
					webFlow.setEndTime(DateUtils.parse2TimesTamp(webFlow.getEndTime(),DateFormatConstant.DATE_CHS_HYPHEN).toString());
				}
				webFlow.setCreateTime(new Date());
				webFlow.setCreateOper(SpringUtil.getSysUserName());
				webFlow.setModifyTime(webFlow.getCreateTime());
				webFlow.setModifyOper(webFlow.getCreateOper());
				webFlow.setOperateType(OperationConstants.OPERATION_SAVE);
				long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
				//有效时间内，需要当前下发策略
				if(webFlow.getStartTime().equals("0") || now>=Long.valueOf(webFlow.getStartTime())) {
					webFlowStrategy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.WEB_FLOW_POLICY.getId()));
					webFlowStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.WEB_FLOW_POLICY.getId()));
					webFlowStrategy.setCTime(webFlow.getTime());
					webFlowStrategy.setOperationType(webFlow.getOperateType());
					webFlowStrategy.setAdvUrl(webFlow.getAdvUrl());
					webFlowStrategy.setCType(webFlow.getCtype());
					webFlowStrategy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
					webFlowStrategy.setProbeType(ProbeType.DPI.getValue());
					webFlowStrategy.setMessageName(webFlow.getMessageName());
					webFlow.setMessageNo(webFlowStrategy.getMessageNo());
				}
				policyWebflowMapper.insert(webFlow);
				//web类型
				List<Integer> webTypeList = new ArrayList<Integer>();
				if(webFlow.getWebTypes().size()>0) {
					for(WebFlowManageWebType webType:webFlow.getWebTypes()) {
						webType.setWebflowId(webFlow.getWebflowId());
						webTypeList.add(webType.getWebType());
					}
					webFlowManageWebTypeMapper.insertList(webFlow.getWebTypes());
				}
				//用户绑定
				if(webFlow.getUserGroup().size()>0) {
					if(webFlow.getUserGroup().get(0).getUserType()==1 
							|| webFlow.getUserGroup().get(0).getUserType()==2) {
						List<WebFlowUserGroup>  newList = new ArrayList<WebFlowUserGroup>(new LinkedHashSet<WebFlowUserGroup>(webFlow.getUserGroup())) ;
						webFlow.setUserGroup(newList);
					}
					for(WebFlowUserGroup user:webFlow.getUserGroup()) {
						user.setWebflowId((long)webFlow.getWebflowId());
					}
					webFlowUserGroupMapper.insertList(webFlow.getUserGroup());
				}
				//有效时间内，需要当前下发策略
				if(webFlow.getStartTime().equals("0") || now>=Long.valueOf(webFlow.getStartTime())) {
					webFlowStrategy.setWebFlowId(webFlow.getWebflowId());
					webFlowStrategy.setCWebType(webTypeList);
					//策略下发
					addPolicy(webFlowStrategy);
					//绑定用户策略下发
					reUserBind(webFlow);
				}
				}
			key.setMessageName(webFlow.getMessageName());
			key.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
			key.setId((long)webFlow.getWebflowId());
			key.setOperType(OperationType.CREATE.getType());
			key.setIdName("webflowId");
			key.setDataType(DataType.POLICY.getType());
		}else {
			if(policyWebflowMapper.isSamePolicyName(webFlow)>0) {
				result.setResult(0);
				result.setMessage("存在相同名称的策略！");
				return result;
			}else {
				WebFlowStrategy webFlowStrategy =new WebFlowStrategy();
				//开始时间处理
				if(webFlow.getStartTime()==null) {
					webFlow.setStartTime("0");
				}else {
					webFlow.setStartTime(DateUtils.parse2TimesTamp(webFlow.getStartTime(),DateFormatConstant.DATE_CHS_HYPHEN).toString());
				}
				//结束时间处理
				if(webFlow.getEndTime()==null) {
					webFlow.setEndTime("0");
				}else {
					webFlow.setEndTime(DateUtils.parse2TimesTamp(webFlow.getEndTime(),DateFormatConstant.DATE_CHS_HYPHEN).toString());
				}
				long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
				webFlow.setModifyTime(webFlow.getCreateTime());
				webFlow.setModifyOper(webFlow.getCreateOper());
				webFlow.setOperateType(OperationConstants.OPERATION_UPDATE);
				//有效时间内，需要当前下发策略
				if(webFlow.getStartTime().equals("0") || (now>=Long.valueOf(webFlow.getStartTime()) &&
						(webFlow.getEndTime().equals("0") || now <= (Long.valueOf(webFlow.getEndTime())+86400)))) {
					if(webFlow.getMessageNo()!=null) {
						webFlowStrategy.setMessageNo(webFlow.getMessageNo());
					}else {
						webFlowStrategy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.WEB_FLOW_POLICY.getId()));
					}
					webFlowStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.WEB_FLOW_POLICY.getId()));
					webFlowStrategy.setCTime(webFlow.getTime());
					webFlowStrategy.setOperationType(webFlow.getOperateType());
					webFlowStrategy.setAdvUrl(webFlow.getAdvUrl());
					webFlowStrategy.setCType(webFlow.getCtype());
					webFlowStrategy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
					webFlowStrategy.setProbeType(ProbeType.DPI.getValue());
					webFlowStrategy.setMessageName(webFlow.getMessageName());
					webFlow.setMessageNo(webFlowStrategy.getMessageNo());
				}
				policyWebflowMapper.updateByPrimaryKeySelective(webFlow);
				//web类型
				List<Integer> webTypeList = new ArrayList<Integer>();
				if(webFlow.getWebTypes().size()>0) {
					for(WebFlowManageWebType webType:webFlow.getWebTypes()) {
						webType.setWebflowId(webFlow.getWebflowId());
						webTypeList.add(webType.getWebType());
					}
					webFlowManageWebTypeMapper.deleteById(webFlow.getWebflowId());
					webFlowManageWebTypeMapper.insertList(webFlow.getWebTypes());
				}
				//用户绑定
				List<WebFlowUserGroup> delUser = new ArrayList<WebFlowUserGroup>();
				List<WebFlowUserGroup> newUser = new ArrayList<WebFlowUserGroup>();
				if(webFlow.getUserGroup().size()>0) {
					List<WebFlowUserGroup> userGroup = webFlowUserGroupMapper.getListById(webFlow.getWebflowId());
					if(userGroup.get(0).getUserType().equals(webFlow.getUserGroup().get(0).getUserType())) {
						for(WebFlowUserGroup tem:webFlow.getUserGroup()){
							boolean isSame = false;
							for(WebFlowUserGroup old:userGroup) {
								if(tem.equals(old)) {
									isSame = true;
									break;
								}
							}
							if(!isSame) {
								newUser.add(tem);
							}
						}
						for(WebFlowUserGroup old: userGroup ){
							boolean isSame = false;
							for(WebFlowUserGroup tem:webFlow.getUserGroup()) {
								if(old.equals(tem)) {
									isSame = true;
									break;
								}
							}
							if(!isSame) {
								delUser.add(old);
							}
						}
					}else {
						delUser = userGroup;
						newUser = webFlow.getUserGroup();
						for(WebFlowUserGroup user:newUser) {
							user.setWebflowId((long)webFlow.getWebflowId());
						}
					}
					if(delUser.size()>0) {
						webFlowUserGroupMapper.deleteEntity(delUser);
					}
					if(newUser.size()>0) {
						webFlowUserGroupMapper.insertList(newUser);
					}
				}
				//有效时间内，需要当前下发策略
				if(webFlow.getStartTime().equals("0") || (now>=Long.valueOf(webFlow.getStartTime()) &&
						(webFlow.getEndTime().equals("0") || now <= (Long.valueOf(webFlow.getEndTime())+86400)))) {
					webFlowStrategy.setWebFlowId(webFlow.getWebflowId());
					webFlowStrategy.setCWebType(webTypeList);
					//策略下发
					modifyPolicy(webFlowStrategy);
					//解绑
					if(delUser.size()>0) {
						WebFlowManage deluser = new WebFlowManage();
						deluser.setUserGroup(delUser);
						deluser.setMessageNo(webFlow.getMessageNo());
						delUserBind(deluser);
					}
					//绑定用户策略下发
					if(newUser.size()>0) {
						WebFlowManage adduser = new WebFlowManage();
						adduser.setUserGroup(newUser);
						adduser.setMessageNo(webFlow.getMessageNo());
						reUserBind(adduser);
					}
					key.setMessageNo(webFlowStrategy.getMessageNo());
				}
				key.setMessageName(webFlow.getMessageName());
				key.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
				key.setId((long)webFlow.getWebflowId());
				key.setOperType(OperationType.MODIFY.getType());
				key.setDataType(DataType.POLICY.getType());
			}
		}
		result.setResult(1);
		keys.add(key);
		result.setKeys(keys);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> deleteWebFlow(Integer[] policys) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		List<WebFlowManage> webflowList = policyWebflowMapper.getRecordsByIds(policys);
		for(WebFlowManage tem:webflowList) {
			BaseKeys key = new BaseKeys();
			if(tem.getMessageNo()==null) {
				tem.setOperateType(OperationConstants.OPERATION_DELETE);
				tem.setModifyOper(SpringUtil.getSysUserName());
				tem.setModifyTime(new Date());
				policyWebflowMapper.updateByPrimaryKeySelective(tem);
			}else {
				tem.setOperateType(OperationConstants.OPERATION_DELETE);
				tem.setModifyOper(SpringUtil.getSysUserName());
				tem.setModifyTime(new Date());
				policyWebflowMapper.updateByPrimaryKeySelective(tem);
				
				WebFlowStrategy webFlowStrategy = new WebFlowStrategy();
				webFlowStrategy.setMessageNo(tem.getMessageNo());
				webFlowStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.WEB_FLOW_POLICY.getId()));
				webFlowStrategy.setCTime(tem.getTime());
				webFlowStrategy.setOperationType(OperationConstants.OPERATION_DELETE);
				webFlowStrategy.setAdvUrl(tem.getAdvUrl());
				webFlowStrategy.setCType(tem.getCtype());
				webFlowStrategy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
				webFlowStrategy.setProbeType(ProbeType.DPI.getValue());
				webFlowStrategy.setMessageName(tem.getMessageName());
				List<Integer> cWeb = new ArrayList<Integer>();
				for(WebFlowManageWebType web:tem.getWebTypes()) {
					cWeb.add(web.getWebType());
				}
				webFlowStrategy.setCWebType(cWeb);
				deletePolicy(webFlowStrategy);
				//解绑用户
				delUserBind(tem);
				key.setMessageNo(webFlowStrategy.getMessageNo());
			}
			key.setMessageName(tem.getMessageName());
			key.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
			key.setId((long)tem.getWebflowId());
			key.setOperType(OperationType.DELETE.getType());
			key.setDataType(DataType.POLICY.getType());
			keys.add(key);
		}
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}

	@Override
	protected boolean addDb(BaseVO baseVO) {
		if(baseVO instanceof WebFlowStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(OperationConstants.OPERATION_SAVE);
            policy.setCreateOper(SpringUtil.getSysUserName());//待续
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            policy.setCreateTime(new Date());
            policy.setModifyTime(new Date());
            policyMapper.insertSelective(policy);
		}
		return true;
	}

	@Override
	protected boolean deleteDb(BaseVO baseVO) {
		if(baseVO instanceof WebFlowStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(OperationConstants.OPERATION_DELETE);
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            policy.setModifyTime(new Date());
            policyMapper.updatePolicyByMessageNoAndType(policy);
		}
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO baseVO) {
		if(baseVO instanceof WebFlowStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(OperationConstants.OPERATION_UPDATE);
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            policy.setModifyTime(new Date());
            policyMapper.updatePolicyByMessageNoAndType(policy);
		}
		return true;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) ;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		return setPolicyOperateSequenceToRedis(policy) ;
	}

    /**
     * 不同类型的绑定 -- 新增
     * @param record
     */
    protected void reUserBind(WebFlowManage webFlow){
        //绑定信息
    	for(WebFlowUserGroup record:webFlow.getUserGroup()) {
    		UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
    		List<BindMessage> bindInfo = new ArrayList<BindMessage>();
    		BindMessage bind = new BindMessage();
    		bind.setBindMessageNo(webFlow.getMessageNo());
    		bind.setBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
    		bindInfo.add(bind);
    		userPolicyBindStrategy.setBindInfo(bindInfo);
	        userPolicyBindStrategy.setUserType(record.getUserType());
	        userPolicyBindStrategy.setMessageType(MessageType.USER_POLICY_BIND.getId());
	        if(record.getUserType() == 1 || record.getUserType()==2){
	        	userPolicyBindStrategy.setUserName(record.getUserName());
	        }else if(record.getUserType() == 3){
                userPolicyBindStrategy.setUserGroupId(record.getUserGroupId());
                userPolicyBindStrategy.setUserName(getUserGroupName(record.getUserGroupId()));
	        }else {
	            userPolicyBindStrategy.setUserName("");
	        }
	        userPolicyBindService.addPolicy(userPolicyBindStrategy);
    	}
    }
    
    public void delUserBind(WebFlowManage webFlow) {
    	UserPolicyBindStrategy record = new UserPolicyBindStrategy();
    	record.setUserBindMessageNo(webFlow.getMessageNo());
    	record.setUserBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
    	List<UserPolicyBindStrategy> userBindList = userPolicyBindMapper.getByBindMessages(record);
    	if(userBindList.size()>0) {
    		for(WebFlowUserGroup userGroup:webFlow.getUserGroup()) {
    			UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
        		for(UserPolicyBindStrategy old : userBindList) {
        			if((userGroup.getUserType() == 1 || userGroup.getUserType()==2) &&
        					userGroup.getUserType().equals(old.getUserType()) && 
        					userGroup.getUserName().equals(old.getUserName())){
        	        	userPolicyBindStrategy.setUserName(old.getUserName());
        	        	userPolicyBindStrategy.setMessageNo(old.getMessageNo());
        	        	BindMessage bind = new BindMessage();
        	        	List<BindMessage> bindInfo = new ArrayList<BindMessage>();
        	    		bind.setBindMessageNo(webFlow.getMessageNo());
        	    		bind.setBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
        	    		bindInfo.add(bind);
        	        	userPolicyBindStrategy.setBindInfo(bindInfo);
        	        	userPolicyBindStrategy.setUserType(old.getUserType());
        		        userPolicyBindStrategy.setMessageType(MessageType.USER_POLICY_BIND.getId());
        	        }else if(userGroup.getUserType() == 3 &&
        					userGroup.getUserType().equals(old.getUserType()) && 
        					userGroup.getUserGroupId().equals(old.getUserGroupId())){
        	        	userPolicyBindStrategy.setMessageNo(old.getMessageNo());
        	        	BindMessage bind = new BindMessage();
        	        	List<BindMessage> bindInfo = new ArrayList<BindMessage>();
        	    		bind.setBindMessageNo(webFlow.getMessageNo());
        	    		bind.setBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
        	    		bindInfo.add(bind);
        	        	userPolicyBindStrategy.setBindInfo(bindInfo);
        	        	userPolicyBindStrategy.setUserType(old.getUserType());
        		        userPolicyBindStrategy.setMessageType(MessageType.USER_POLICY_BIND.getId());
        		        userPolicyBindStrategy.setUserName(getUserGroupName(record.getUserGroupId()));
        	        }else if(userGroup.getUserType() == 0 &&
        	        		userGroup.getUserType().equals(old.getUserType())) {
        	        	userPolicyBindStrategy.setMessageNo(old.getMessageNo());
        	        	BindMessage bind = new BindMessage();
        	        	List<BindMessage> bindInfo = new ArrayList<BindMessage>();
        	    		bind.setBindMessageNo(webFlow.getMessageNo());
        	    		bind.setBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
        	    		bindInfo.add(bind);
        	        	userPolicyBindStrategy.setBindInfo(bindInfo);
        	        	userPolicyBindStrategy.setUserType(old.getUserType());
        		        userPolicyBindStrategy.setMessageType(MessageType.USER_POLICY_BIND.getId());
        	        }
        		}
        		if(userPolicyBindStrategy!=null) {
        			userPolicyBindService.deletePolicy(userPolicyBindStrategy);
        		}
        	}
    	}
    	
	}
    
    /**
     * 
    * @Title: getUserGroupName
    * @Description: 获取用户组名
    * @param @param userGroupId
    * @param @return
    * @return String
    * @throws
     */
    protected String getUserGroupName(Long userGroupId){
        String userName = "";
        try {
            userName = commonCache.getUserGroupNameCache().get(userGroupId);
            if(StringUtils.isEmpty(userName)){
                commonCache.refreshCache();
                userName = commonCache.getUserGroupNameCache().get(userGroupId);
            }
        } catch (Exception e) {
            logger.error("while getting usergroup name error",e);
        }
        return userName;
    }

	@Override
	public ResponseResult<BaseKeys> policyResend(Integer[] webFlowId) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		List<WebFlowManage> webFlowList = policyWebflowMapper.getRecordsByIds(webFlowId);
		List<String> ip = new ArrayList<String>();
		List<Long> bindMessageNo = new ArrayList<Long>();
		if(webFlowList.size()>0) {
			for(WebFlowManage webFlow:webFlowList) {
				manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.WEB_FLOW_POLICY.getId(), webFlow.getMessageNo(), ip);
				UserPolicyBindStrategy record = new UserPolicyBindStrategy();
		    	record.setUserBindMessageNo(webFlow.getMessageNo());
		    	record.setUserBindMessageType(MessageType.WEB_FLOW_POLICY.getId());
		    		List<UserPolicyBindStrategy> userBindList = userPolicyBindMapper.getByBindMessages(record);
			    	for(UserPolicyBindStrategy bind:userBindList) {
			    		manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.USER_POLICY_BIND.getId(), bind.getMessageNo(), ip);
			    		bindMessageNo.add(bind.getMessageNo());
			    	}
		    	key.setMessageName(webFlow.getMessageName());
				key.setMessageType(MessageType.WEB_FLOW_POLICY.getId());
				key.setId((long)webFlow.getWebflowId());
				key.setOperType(OperationType.RESEND.getType());
				key.setMessageNo(webFlow.getMessageNo());
				key.setDataType(DataType.POLICY.getType());
			}
		}
		result.setBindMessageNo(bindMessageNo);
		result.setResult(1);
		keys.add(key);
		result.setKeys(keys);
		return result;
	}
	
}
