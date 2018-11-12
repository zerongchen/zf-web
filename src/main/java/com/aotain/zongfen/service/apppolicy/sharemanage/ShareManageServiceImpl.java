package com.aotain.zongfen.service.apppolicy.sharemanage;

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
import com.aotain.common.policyapi.model.ShareManageStrategy;
import com.aotain.common.policyapi.model.ShareManageUserGroup;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.cache.CommonCache;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.apppolicy.ShareManageMapper;
import com.aotain.zongfen.mapper.apppolicy.ShareManageUserGroupMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
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
public class ShareManageServiceImpl extends BaseService implements ShareManageService {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ShareManageServiceImpl.class);

	@Autowired
	private ShareManageMapper shareManageMapper;
	
    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;
    
    @Autowired 
    private ShareManageUserGroupMapper shareManageUserGroupMapper;
    
    @Autowired
    private UserPolicyBindService userPolicyBindService;
    
    @Autowired
    private CommonCache commonCache;
    
    @Autowired
    private PolicyStatusMapper policyStatusMapper;
	
	@Override
	public PageResult<ShareManageStrategy> getList(Integer pageSize, Integer pageIndex, String policyName,String startTime,String endTime) {
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("messageName", policyName);
		query.put("startTime", startTime);
		query.put("endTime", endTime);
		PageResult<ShareManageStrategy> result = new PageResult<ShareManageStrategy>();
		PageHelper.startPage(pageIndex, pageSize);
		List<ShareManageStrategy> info = shareManageMapper.getIndexList(query);
		PageInfo<ShareManageStrategy> pageResult = new PageInfo<ShareManageStrategy>(info);
		for (ShareManageStrategy dto:info){
            dto.setStartTimeShow(DateUtils.parse2DateString(Long.valueOf(dto.getStartTime()),DateFormatConstant.DATE_CHS_HYPHEN));
            dto.setEndTimeShow(DateUtils.parse2DateString(Long.valueOf(dto.getEndTime()),DateFormatConstant.DATE_CHS_HYPHEN));
            dto.setType(0);
            PolicyStatus appPolicy = new PolicyStatus();
            long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
            if(dto.getMessageNo()!=null) {
            	PolicyStatus query2 = new PolicyStatus();
            	query2.setMessageNo(dto.getMessageNo());
            	query2.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
            	appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            	if(dto.getStartTime()==0 || (now>=Long.valueOf(dto.getStartTime()) &&
    					(dto.getEndTime()==0 || now <= (Long.valueOf(dto.getEndTime())+86400)))) {
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
	public ResponseResult<BaseKeys> save(ShareManageStrategy share) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		int samecount = shareManageMapper.isSamePolicyName(share);
		if(samecount>0) {
			result.setResult(0);
			result.setMessage("已存在相同名称的策略！");
			return result;
		}
		//开始时间处理
		if(share.getStartTimeShow()==null) {
			share.setStartTime((long) 0);
		}else {
			share.setStartTime(DateUtils.parse2TimesTamp(share.getStartTimeShow(),DateFormatConstant.DATE_CHS_HYPHEN));
		}
		//结束时间处理
		if(share.getEndTimeShow()==null) {
			share.setEndTime((long) 0);
		}else {
			share.setEndTime(DateUtils.parse2TimesTamp(share.getEndTimeShow(),DateFormatConstant.DATE_CHS_HYPHEN));
		}
		long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
		if(share.getStartTime()==0 || (now>=share.getStartTime() &&
				(share.getEndTime()==0 || now <= (share.getEndTime()+86400)))) {
			share.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.SHARE_MANAGE_POLICY.getId()));
			share.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.SHARE_MANAGE_POLICY.getId()));
		}
		share.setCreateTime(new Date());
		share.setModifyTime(share.getCreateTime());
		share.setCreateOper(SpringUtil.getSysUserName());
		share.setModifyOper(share.getCreateOper());
		share.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
		share.setOperationType(OperationConstants.OPERATION_SAVE);
		share.setProbeType(ProbeType.DPI.getValue());
		shareManageMapper.insert(share);
		//用户绑定
		if(share.getUserGroups().size()>0) {
			if(share.getUserGroups().get(0).getUserType()==1 
					|| share.getUserGroups().get(0).getUserType()==2) {
				List<ShareManageUserGroup>  newList = new ArrayList<ShareManageUserGroup>(new LinkedHashSet<ShareManageUserGroup>(share.getUserGroups())) ;
				share.setUserGroups(newList);
			}
			for(ShareManageUserGroup user:share.getUserGroups()) {
				user.setShareId(share.getShareId());
			}
			shareManageUserGroupMapper.insertList(share.getUserGroups());
		}
		boolean message = true;
		if(share.getStartTime()==0 || (now>=share.getStartTime() &&
				(share.getEndTime()==0 || now <= (share.getEndTime()+86400)))) {
			message = addPolicy(share);
			//绑定用户策略下发
			reUserBind(share);
			key.setMessageNo(share.getMessageNo());
		}
		key.setMessageName(share.getMessageName());
		key.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
		key.setId((long)share.getShareId());
		key.setOperType(OperationType.CREATE.getType());
		key.setDataType(DataType.POLICY.getType());
		keys.add(key);
		result.setKeys(keys);
		if(message) {
			result.setResult(1);
		}else {
			result.setMessage("策略下发错误！");
			result.setResult(0);
		}
		return result;
	}
	
	public ResponseResult<BaseKeys> update(ShareManageStrategy share) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		int samecount = shareManageMapper.isSamePolicyName(share);
		if(samecount>0) {
			result.setResult(0);
			result.setMessage("已存在相同名称的策略！");
			return result;
		}
		if(samecount>0) {
			result.setResult(0);
			result.setMessage("已存在相同名称的策略！");
			return result;
		}
		//开始时间处理
		if(share.getStartTimeShow()==null) {
			share.setStartTime((long) 0);
		}else {
			share.setStartTime(DateUtils.parse2TimesTamp(share.getStartTimeShow(),DateFormatConstant.DATE_CHS_HYPHEN));
		}
		//结束时间处理
		if(share.getEndTimeShow()==null) {
			share.setEndTime((long) 0);
		}else {
			share.setEndTime(DateUtils.parse2TimesTamp(share.getEndTimeShow(),DateFormatConstant.DATE_CHS_HYPHEN));
		}
		long now = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT);
		share.setModifyTime(new Date());
		share.setModifyOper(SpringUtil.getSysUserName());
		share.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
		share.setOperationType(OperationConstants.OPERATION_UPDATE);
		share.setProbeType(ProbeType.DPI.getValue());
		//用户绑定
		List<ShareManageUserGroup> delUser = new ArrayList<ShareManageUserGroup>();
		List<ShareManageUserGroup> newUser = new ArrayList<ShareManageUserGroup>();
		if(share.getUserGroups().size()>0) {
			List<ShareManageUserGroup> userGroup = shareManageUserGroupMapper.getListById(share.getShareId());
			if(userGroup.get(0).getUserType().equals(share.getUserGroups().get(0).getUserType())) {
				for(ShareManageUserGroup tem:share.getUserGroups()){
					boolean isSame = false;
					for(ShareManageUserGroup old:userGroup) {
						if(tem.equals(old)) {
							isSame = true;
							break;
						}
					}
					if(!isSame) {
						newUser.add(tem);
					}
				}
				for(ShareManageUserGroup old: userGroup ){
					boolean isSame = false;
					for(ShareManageUserGroup tem:share.getUserGroups()) {
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
				newUser = share.getUserGroups();
				for(ShareManageUserGroup user:newUser) {
					user.setShareId(share.getShareId());
				}
			}
			if(delUser.size()>0) {
				shareManageUserGroupMapper.deleteEntity(delUser);
			}
			if(newUser.size()>0) {
				shareManageUserGroupMapper.insertList(newUser);
			}
		}
		shareManageMapper.updateByPrimaryKeySelective(share);
		boolean message = true;
		if(share.getStartTime()==0 || (now>=share.getStartTime() &&
				(share.getEndTime()==0 || now <= (share.getEndTime()+86400)))) {
			share.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.SHARE_MANAGE_POLICY.getId()));
			message = modifyPolicy(share);
			//解绑
			if(delUser.size()>0) {
				ShareManageStrategy delShareuser = new ShareManageStrategy();
				delShareuser.setUserGroups(delUser);
				delShareuser.setMessageNo(share.getMessageNo());
				delUserBind(delShareuser);
			}
			//绑定用户策略下发
			if(newUser.size()>0) {
				ShareManageStrategy addShareuser = new ShareManageStrategy();
				addShareuser.setUserGroups(newUser);
				addShareuser.setMessageNo(share.getMessageNo());
				reUserBind(addShareuser);
			}
			key.setMessageNo(share.getMessageNo());
		}
		key.setMessageName(share.getMessageName());
		key.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
		key.setId((long)share.getShareId());
		key.setOperType(OperationType.MODIFY.getType());
		key.setDataType(DataType.POLICY.getType());
		keys.add(key);
		result.setKeys(keys);
		if(message) {
			result.setResult(1);
		}else {
			result.setMessage("策略下发错误！");
			logger.error("Share manage policy send fial!");
			result.setResult(0);
		}
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> delete(Integer[] policys) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		List<ShareManageStrategy> shareList = shareManageMapper.getRecordsByIds(policys);
		for(ShareManageStrategy tem:shareList) {
			BaseKeys key = new BaseKeys();
			if(tem.getMessageNo()==null) {
				tem.setOperationType(OperationConstants.OPERATION_DELETE);
				tem.setModifyOper(SpringUtil.getSysUserName());
				tem.setModifyTime(new Date());
				shareManageMapper.updateByPrimaryKeySelective(tem);
			}else {
				tem.setOperationType(OperationConstants.OPERATION_DELETE);
				tem.setModifyOper(SpringUtil.getSysUserName());
				tem.setModifyTime(new Date());
				shareManageMapper.updateByPrimaryKeySelective(tem);
				
				tem.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.SHARE_MANAGE_POLICY.getId()));
				tem.setOperationType(OperationConstants.OPERATION_DELETE);
				tem.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
				tem.setProbeType(ProbeType.DPI.getValue());
				deletePolicy(tem);
				//解绑用户
				delUserBind(tem);
				key.setMessageNo(tem.getMessageNo());
			}
			key.setMessageName(tem.getMessageName());
			key.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
			key.setId((long)tem.getShareId());
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
		if(baseVO instanceof ShareManageStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(baseVO.getMessageType());
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
		if(baseVO instanceof ShareManageStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(baseVO.getMessageType());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(OperationConstants.OPERATION_DELETE);
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            policy.setModifyTime(new Date());
            policyMapper.updatePolicyByMessageNoAndType(policy);
		}
		return true;
	}

	@Override
	protected boolean modifyDb(BaseVO baseVO) {
		if(baseVO instanceof ShareManageStrategy){
			Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(baseVO.getMessageType());
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
    protected void reUserBind(ShareManageStrategy share){
        //绑定信息
    	for(ShareManageUserGroup record:share.getUserGroups()) {
    		UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
    		List<BindMessage> bindInfo = new ArrayList<BindMessage>();
    		BindMessage bind = new BindMessage();
    		bind.setBindMessageNo(share.getMessageNo());
    		bind.setBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
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
    
    public void delUserBind(ShareManageStrategy share) {
    	UserPolicyBindStrategy record = new UserPolicyBindStrategy();
    	record.setUserBindMessageNo(share.getMessageNo());
    	record.setUserBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
    	List<UserPolicyBindStrategy> userBindList = userPolicyBindMapper.getByBindMessages(record);
    	if(userBindList.size()>0) {
    		for(ShareManageUserGroup userGroup:share.getUserGroups()) {
    			UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
        		for(UserPolicyBindStrategy old : userBindList) {
        			if((userGroup.getUserType() == 1 || userGroup.getUserType()==2) &&
        					userGroup.getUserType().equals(old.getUserType()) && 
        					userGroup.getUserName().equals(old.getUserName())){
        	        	userPolicyBindStrategy.setUserName(old.getUserName());
        	        	userPolicyBindStrategy.setMessageNo(old.getMessageNo());
        	        	BindMessage bind = new BindMessage();
        	        	List<BindMessage> bindInfo = new ArrayList<BindMessage>();
        	    		bind.setBindMessageNo(share.getMessageNo());
        	    		bind.setBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
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
        	    		bind.setBindMessageNo(share.getMessageNo());
        	    		bind.setBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
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
        	    		bind.setBindMessageNo(share.getMessageNo());
        	    		bind.setBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
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
	public ResponseResult<BaseKeys> policyResend(Integer[] shareId) {
		ResponseResult<BaseKeys> result =  new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		List<ShareManageStrategy> shareList = shareManageMapper.getRecordsByIds(shareId);
		List<String> ip = new ArrayList<String>();
		List<Long> bindMessageNo = new ArrayList<Long>();
		if(shareList.size()>0) {
			for(ShareManageStrategy share:shareList) {
				BaseKeys key = new BaseKeys();
				manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.SHARE_MANAGE_POLICY.getId(), share.getMessageNo(), ip);
				UserPolicyBindStrategy record = new UserPolicyBindStrategy();
		    	record.setUserBindMessageNo(share.getMessageNo());
		    	record.setUserBindMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
	    		List<UserPolicyBindStrategy> userBindList = userPolicyBindMapper.getByBindMessages(record);
		    	for(UserPolicyBindStrategy bind:userBindList) {
		    		manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.USER_POLICY_BIND.getId(), bind.getMessageNo(), ip);
		    		bindMessageNo.add(bind.getMessageNo());
		    	}
		    	key.setMessageName(share.getMessageName());
				key.setMessageType(MessageType.SHARE_MANAGE_POLICY.getId());
				key.setId((long)share.getShareId());
				key.setOperType(OperationType.RESEND.getType());
				key.setMessageNo(share.getMessageNo());
				key.setDataType(DataType.POLICY.getType());
				keys.add(key);
			}
		}
		result.setBindMessageNo(bindMessageNo);
		result.setKeys(keys);
		result.setResult(1);
		return result;
		}

}
