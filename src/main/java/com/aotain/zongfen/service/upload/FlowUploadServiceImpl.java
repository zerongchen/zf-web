package com.aotain.zongfen.service.upload;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.aotain.common.policyapi.constant.MessageTypeConstants;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import org.apache.xpath.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.FlowUploadStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.cache.CommonCache;
import com.aotain.zongfen.dto.upload.FlowUploadDTO;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.mapper.upload.FlowUploadMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.device.ZongFenDeviceUser;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.upload.FlowUpload;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.basicdata.UdUploadType;
import org.springframework.util.StringUtils;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class FlowUploadServiceImpl extends BaseService implements FlowUploadService {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(FlowUploadServiceImpl.class);


    @Autowired
    private FlowUploadMapper flowUploadMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private CommonCache commonCache;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Override
    public List<FlowUploadDTO> getPolicyList( FlowUploadDTO record ) {
        record.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
        List<FlowUploadDTO> lists = null;
        Boolean isFlowRecode = false;
        Boolean isDdosRecode = false;
        Boolean isAppUserRecode = false;
        Boolean isWebPushRecode = false;
        if(record.getSearchEndTime()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(record.getSearchEndTime());
            cal.set(Calendar.DATE,cal.get(Calendar.DATE)+1);
            record.setSearchEndTime(cal.getTime());
        }
        if(record.getPacketType().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketType().intValue() &&
                record.getPacketSubtype().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype().intValue()){
            lists = flowUploadMapper.getPolicyList(record);
            isFlowRecode = true;
        }else if(record.getPacketType().intValue() == UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketType().intValue() &&
                record.getPacketSubtype().intValue() == UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketSubtype().intValue()){
            lists = flowUploadMapper.getPolicyList(record);
            isDdosRecode = true;
        }else if(record.getPacketSubtype().intValue() == UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype().intValue()){
            lists = flowUploadMapper.getPolicyList(record);
            isWebPushRecode=true;
        }else if(record.getPacketSubtype().intValue() == UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype().intValue()){
            lists = flowUploadMapper.getPolicyList(record);
            isAppUserRecode = true;
        } else {
            lists = flowUploadMapper.getPolicyList(record);
        }
        String policyCount = null;
        String bindPolicy = null;
        for (FlowUploadDTO flowUploadDTO:lists){
        	//获取流量流向绑定的成功失败次数
            if(isFlowRecode){
                try {
                    Long messageNo = flowUploadDTO.getMessageNo();
                    Map<String,String> params = new HashMap<>();
                    params.put("messageNo",messageNo+"");
                    params.put("appMessageType",MessageType.APP_FLOW_DIRECTION_POLICY.getId()+"");
                    Map<String,String> result = flowUploadMapper.getAppPolicyMessageName(params);
                    flowUploadDTO.setAreaGroupMessageName(result!=null&&result.containsKey("messageName")?result.get("messageName"):"");
                    if(result!=null&&result.containsKey("messageNo")){
                        Object obj= result.get("messageNo");
                        Long appMessageNo = Long.valueOf(obj.toString());
                        flowUploadDTO.setAreaGroupMessageNo(appMessageNo);
                    }
                } catch (NumberFormatException e) {
                    logger.error("",e);
                }
            }else if(isDdosRecode){
                try {
                    Long messageNo = flowUploadDTO.getMessageNo();
                    Map<String,String> params = new HashMap<>();
                    params.put("messageNo",messageNo+"");
                    params.put("appMessageType",MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId()+"");
                    Map<String,String> result = flowUploadMapper.getAppPolicyMessageName(params);
                    flowUploadDTO.setDdosMessageName(result!=null&&result.containsKey("messageName")?result.get("messageName"):"");
                    if(result!=null&&result.containsKey("messageNo")){
                        Object obj= result.get("messageNo");
                        Long ddosMessageNo = Long.valueOf(obj.toString());
                        flowUploadDTO.setDdosManageMessageNo(ddosMessageNo);
                    }
                } catch (NumberFormatException e) {
                   logger.error("",e);
                }
            } else if (isAppUserRecode){
                try {
                    Long messageNo = flowUploadDTO.getMessageNo();
                    Map<String,String> params = new HashMap<>();
                    params.put("messageNo",messageNo+"");
                    params.put("appMessageType",MessageType.APP_USER_DEFINED.getId()+"");
                    Map<String,String> result = flowUploadMapper.getAppPolicyMessageName(params);
                    String messageName=result!=null&&result.containsKey("messageName")?result.get("messageName"):"";
                    flowUploadDTO.setAppUserMessageName(messageName);
                    if(result!=null&&result.containsKey("messageNo")){
                        Object obj= result.get("messageNo");
                        Long ddosMessageNo = Long.valueOf(obj.toString());
                        flowUploadDTO.setAppUserMessageNo(ddosMessageNo);
                    }
                } catch (NumberFormatException e) {
                    logger.error("",e);
                }
            }else if (isWebPushRecode){
                try {
                    Long messageNo = flowUploadDTO.getMessageNo();
                    Map<String,String> params = new HashMap<>();
                    params.put("messageNo",messageNo+"");
                    params.put("appMessageType",MessageType.WEB_PUSH_POLICY.getId()+"");
                    Map<String,String> result = flowUploadMapper.getAppPolicyMessageName(params);
                    String messageName=result!=null&&result.containsKey("messageName")?result.get("messageName"):"";
                    flowUploadDTO.setWebPushMessageName(messageName);
                    if(result!=null&&result.containsKey("messageNo")){
                        Object obj= result.get("messageNo");
                        Long ddosMessageNo = Long.valueOf(obj.toString());
                        flowUploadDTO.setWebPushMessageNo(ddosMessageNo);
                    }
                } catch (NumberFormatException e) {
                    logger.error("",e);
                }
            }

            if(flowUploadDTO.getRStarttime()>0l) {
                flowUploadDTO.setStartTime(DateUtils.parse2DateString(flowUploadDTO.getRStarttime(),DateFormatConstant.DATE_CHS_HYPHEN));
            }else {
                flowUploadDTO.setStartTime("");
            }
            if(flowUploadDTO.getREndtime()>0l){
                flowUploadDTO.setEndTime(DateUtils.parse2DateString(flowUploadDTO.getREndtime(),DateFormatConstant.DATE_CHS_HYPHEN));
            }else {
                flowUploadDTO.setEndTime("");
            }

            PolicyStatus policyStatus = new PolicyStatus();
            policyStatus.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
            policyStatus.setMessageNo(flowUploadDTO.getMessageNo());
            PolicyStatus policyStatusObj = policyStatusMapper.getCountTotalAndFail(policyStatus);
            if(policyStatusObj!=null) {
                policyCount = policyStatusObj.getMainCount() == null ? "0/0" : policyStatusObj.getMainCount();
                bindPolicy = policyStatusObj.getBindCount() == null ? "0/0" : policyStatusObj.getBindCount();
            }else {
                policyCount="0/0";
                bindPolicy = "0/0";
            }
            flowUploadDTO.setPolicyCount(policyCount);
            flowUploadDTO.setBindPolicyCount(bindPolicy);
        }
        return lists;
    }

    @Override
    public ResponseResult<FlowUploadDTO> addDB( FlowUploadDTO record ) {

        ResponseResult<FlowUploadDTO> responseResult = new ResponseResult<FlowUploadDTO>();
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        
        record.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
        int count = flowUploadMapper.isSameMessageName(record);
        if(count>0){
            responseResult.setResult(0);
            responseResult.setMessage("该策略名称已经存在");
            return responseResult;
        }

        long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.FLOW_UPLOAD_POLICY.getId());
        long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.FLOW_UPLOAD_POLICY.getId());

        //add Policy
        FlowUploadStrategy flowUploadStrategy = new FlowUploadStrategy();
        flowUploadStrategy.setZongfenId(record.getZongfenId());
        flowUploadStrategy.setMessageName(record.getMessageName());
        flowUploadStrategy.setMessageSequenceNo(messageSqNo);
        flowUploadStrategy.setMessageNo(messageNo);
        flowUploadStrategy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
        flowUploadStrategy.setOperationType(1);
        flowUploadStrategy.setPacketType(record.getPacketType());
        flowUploadStrategy.setPacketSubType(record.getPacketSubtype());
        flowUploadStrategy.setFreq(record.getRfreq());
        flowUploadStrategy.setStartTime(DateUtils.parse2TimesTamp(record.getStartTime(), DateFormatConstant.DATE_CHS_HYPHEN));
        flowUploadStrategy.setEndTime(DateUtils.parse2TimesTamp(record.getEndTime(),DateFormatConstant.DATE_CHS_HYPHEN));
        flowUploadStrategy.setMethod(getRmethodByPackType(record.getPacketType()));
        flowUploadStrategy.setProbeType(ProbeType.DPI.getValue());

        initStrategy(flowUploadStrategy,record.getZongfenId());
        addPolicy(flowUploadStrategy);
        
       
        //绑定策略
        record.setMessageNo(messageNo);
        record.setMessageSequenceNo(messageSqNo);
        //添加绑定的策略
        List<BaseKeys> binkeys = reUserBind(record);
        Long[] bineMessageNo = new Long[binkeys.size()];
        for (int i=0;i<binkeys.size();i++){
            bineMessageNo[i]=binkeys.get(i).getMessageNo();
        }
        /*****************添加返回信息************************/
        BaseKeys bk = new BaseKeys();
		bk.setMessageNos(new Long[]{messageNo});
		bk.setBindMessageNo(bineMessageNo);
		//0=流量上报策略的时候，就用 = messageType * 100000 + packetType * 1000 + packetSubtype
		bk.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId() * 100000 + record.getPacketType() * 1000 + record.getPacketSubtype());
		bk.setPacketType(record.getPacketType());
		bk.setPacketSubtype(record.getPacketSubtype());
		bk.setOperType(OperationType.CREATE.getType());
		bk.setDataType(DataType.UPLOAD.getType());
		keys.add(bk);
		responseResult.setKeys(keys);
		
        responseResult.setResult(1);
        responseResult.setMessage("新增成功");
       
        return responseResult;
    }
   
    
    @Override
    public ResponseResult<FlowUploadDTO> modifyDb( FlowUploadDTO record )  {
    	
        ResponseResult<FlowUploadDTO> responseResult = new ResponseResult<FlowUploadDTO>();
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        record.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
        int count = flowUploadMapper.isSameMessageName(record);
        if(count>0){
            responseResult.setResult(0);
            responseResult.setMessage("该策略名称已经存在");
            return responseResult;
        }
    	long messageSeqNO = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.FLOW_UPLOAD_POLICY.getId());
        //modify policy DB
        FlowUploadStrategy strategy = new FlowUploadStrategy();
        strategy.setZongfenId(record.getZongfenId());
        strategy.setMessageName(record.getMessageName());
        strategy.setMessageNo(record.getMessageNo());
        strategy.setMessageSequenceNo(messageSeqNO);
        strategy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
        strategy.setOperationType(2);
        strategy.setPacketType(record.getPacketType());
        strategy.setPacketSubType(record.getPacketSubtype());
        strategy.setFreq(record.getRfreq());
        strategy.setStartTime(DateUtils.parse2TimesTamp(record.getStartTime(),DateFormatConstant.DATE_CHS_HYPHEN));
        strategy.setEndTime(DateUtils.parse2TimesTamp(record.getEndTime(),DateFormatConstant.DATE_CHS_HYPHEN));
        strategy.setMethod(getRmethodByPackType(record.getPacketType()));
        strategy.setProbeType(ProbeType.DPI.getValue());
        
        initStrategy(strategy,record.getZongfenId());
        modifyPolicy(strategy);

        //get bind message : 流量上报策略
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("messageNo",record.getMessageNo());
        uploadMap.put("messageType",MessageType.FLOW_UPLOAD_POLICY.getId());
        uploadMap.put("operateType",3);
        List<UserPolicyBindStrategy> uploadUserPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(uploadMap);
        
        //get bind message : 流量流向策略
        Map<String,Object> redirectionMap = new HashMap<>();
        redirectionMap.put("messageNo",record.getLastAreaGroupMessageNo());
        redirectionMap.put("messageType",MessageType.APP_FLOW_DIRECTION_POLICY.getId());
        redirectionMap.put("operateType",3);
        List<UserPolicyBindStrategy> redirectionUserPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(redirectionMap);
        
        //合并
        List<UserPolicyBindStrategy> mergetUserPolicyBinds = new ArrayList<UserPolicyBindStrategy>();
        UserPolicyBindStrategy userPolicyBindStrategy = null;

        if(record.getPacketSubtype().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype().intValue()){

            for(UserPolicyBindStrategy upload: uploadUserPolicyBinds) {
                for(UserPolicyBindStrategy redirection: redirectionUserPolicyBinds) {
                    if(upload.getMessageNo().longValue() == redirection.getMessageNo().longValue()) {
                        userPolicyBindStrategy = new UserPolicyBindStrategy();
                        BindMessage bindMessage = null;
                        List<BindMessage> list = new ArrayList<BindMessage>();

                        bindMessage = new BindMessage();
                        bindMessage.setBindMessageNo(record.getMessageNo());
                        bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                        list.add(bindMessage);

                        bindMessage = new BindMessage();
                        bindMessage.setBindMessageNo(record.getLastAreaGroupMessageNo());
                        bindMessage.setBindMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
                        list.add(bindMessage);

                        userPolicyBindStrategy.setBindInfo(list);
                        userPolicyBindStrategy.setMessageNo(upload.getMessageNo());
                        mergetUserPolicyBinds.add(userPolicyBindStrategy);
                    }
                }
            }
        }

        //web推送结果策略更新
        if(record.getPacketSubtype().intValue() == UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype().intValue()) {
	       	 Map<String,Object> webPushMap = new HashMap<>();
	       	 webPushMap.put("messageNo",record.getLastAreaGroupMessageNo());
	       	 webPushMap.put("userBindMessageType",MessageType.WEB_PUSH_POLICY.getId());
	       	 webPushMap.put("operateType",3);
	         List<UserPolicyBindStrategy> webPushUserPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(webPushMap);
	            
	         for(UserPolicyBindStrategy upload: uploadUserPolicyBinds) {
	        	for(UserPolicyBindStrategy webPush: webPushUserPolicyBinds) {
	        		if(upload.getMessageNo().longValue() == webPush.getMessageNo().longValue()) {
	        			userPolicyBindStrategy = new UserPolicyBindStrategy();
	        			BindMessage bindMessage = null;
	            		List<BindMessage> list = new ArrayList<BindMessage>();
	            		
	                	bindMessage = new BindMessage();
	        			bindMessage.setBindMessageNo(record.getMessageNo());
	        			bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
	        			list.add(bindMessage);
	        			
	        			bindMessage = new BindMessage();
	        			bindMessage.setBindMessageNo(record.getLastAreaGroupMessageNo());
	        			bindMessage.setBindMessageType(MessageType.WEB_PUSH_POLICY.getId());
	        			list.add(bindMessage);
	        			
	        			userPolicyBindStrategy.setUserType(webPush.getUserType());
                        userPolicyBindStrategy.setUserName(webPush.getUserName());
	        			userPolicyBindStrategy.setBindInfo(list);
	        			userPolicyBindStrategy.setMessageNo(upload.getMessageNo());
	        			mergetUserPolicyBinds.add(userPolicyBindStrategy);
	        		}
	            }
	        }
       }
        //指定应用用户策略更新
        if(record.getPacketSubtype().intValue() == UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype().intValue()) {
            Map<String,Object> queryMap = new HashMap<>();
            queryMap.put("messageNo",record.getLastAreaGroupMessageNo());
            queryMap.put("userBindMessageType",MessageType.APP_USER_DEFINED.getId());
            queryMap.put("operateType",3);
            List<UserPolicyBindStrategy> appUserPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(queryMap);

            for(UserPolicyBindStrategy upload: uploadUserPolicyBinds) {
                for(UserPolicyBindStrategy appUserPolicyBind: appUserPolicyBinds) {
                    if(upload.getMessageNo().longValue() == appUserPolicyBind.getMessageNo().longValue()) {
                        userPolicyBindStrategy = new UserPolicyBindStrategy();
                        BindMessage bindMessage = null;
                        List<BindMessage> list = new ArrayList<BindMessage>();

                        bindMessage = new BindMessage();
                        bindMessage.setBindMessageNo(record.getMessageNo());
                        bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                        list.add(bindMessage);

                        bindMessage = new BindMessage();
                        bindMessage.setBindMessageNo(record.getLastAppUserMessageNo());
                        bindMessage.setBindMessageType(MessageType.APP_USER_DEFINED.getId());
                        list.add(bindMessage);

                        userPolicyBindStrategy.setUserType(appUserPolicyBind.getUserType());
                        userPolicyBindStrategy.setUserName(appUserPolicyBind.getUserName());
                        userPolicyBindStrategy.setBindInfo(list);
                        userPolicyBindStrategy.setMessageNo(upload.getMessageNo());
                        mergetUserPolicyBinds.add(userPolicyBindStrategy);
                    }
                }
            }
        }
        List<BaseKeys> bindkeys = null;
        if (record.getPacketType().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketType().intValue()
				&& record.getPacketSubtype().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype().intValue() ) {
            bindkeys = dealModifyDirectionStrategy(mergetUserPolicyBinds, record);
        }else if(record.getPacketSubtype().intValue() == UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype().intValue()){
            bindkeys = dealModifyDirectionStrategy(mergetUserPolicyBinds, record);
        }else if(record.getPacketSubtype().intValue() == UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype().intValue()){
            bindkeys = dealModifyDirectionStrategy(mergetUserPolicyBinds, record);
        }else {
            bindkeys = dealModifyStrategy(uploadUserPolicyBinds, record);
        }
        Long[] bindeMessageNo = new Long[bindkeys.size()];
        for (int i=0;i<bindkeys.size();i++){
            bindeMessageNo[i]=bindkeys.get(i).getMessageNo();
        }
        
        /*****************添加返回信息************************/
        BaseKeys bk = new BaseKeys();
		bk.setMessageNos(new Long[]{record.getMessageNo()});
		bk.setBindMessageNo(bindeMessageNo);
		//0=流量上报策略的时候，就用 = messageType * 100000 + packetType * 1000 + packetSubtype
		bk.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId() * 100000 + record.getPacketType() * 1000 + record.getPacketSubtype());
		bk.setPacketType(record.getPacketType());
		bk.setPacketSubtype(record.getPacketSubtype());
		bk.setOperType(OperationType.MODIFY.getType());
		bk.setDataType(DataType.UPLOAD.getType());
		keys.add(bk);
		responseResult.setKeys(keys);
		
		responseResult.setResult(1);
		responseResult.setMessage("修改成功");
  
        return responseResult;
    }
    
    /**
     * 处理一些绑定的策略，主要针对于解绑和绑定的 流量流向策略的特制
     * @param userPolicyBinds
     * @param record
     */
    private List<BaseKeys> dealModifyDirectionStrategy( List<UserPolicyBindStrategy> userPolicyBinds, FlowUploadDTO record) {
    	List<BaseKeys> keylist = new ArrayList<BaseKeys>();
//    	BaseKeys bk = null;
    	//先解绑
    	//对所有存在的解绑
	     for (UserPolicyBindStrategy policyBind:userPolicyBinds){
	    	 policyBind.setProbeType(ProbeType.DPI.getValue());
	         userPolicyBindService.deletePolicy(policyBind);
//	         bk = new BaseKeys();
//     		 bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
//     		 bk.setMessageNo(policyBind.getMessageNo());
//     		 bk.setDataType(DataType.POLICY.getType());
//     		 bk.setOperType(OperationType.DELETE.getType());
//     		 keylist.add(bk);
	         
	     }
	     //重新绑定不同类型的用户
	     keylist.addAll(reUserBind(record));
	     
	     return keylist;
    }
   
    
    
    /**
     * 处理一些绑定的策略，主要针对于解绑和绑定的
     * @param userPolicyBinds
     * @param record
     */
    private List<BaseKeys> dealModifyStrategy( List<UserPolicyBindStrategy> userPolicyBinds, FlowUploadDTO record) {
    	
    	 List<BaseKeys> keylist = new ArrayList<BaseKeys>();
    	 BaseKeys bk = null;
    	 UserPolicyBindStrategy userPolicyBindStrategy = null;
		 //初始化部分绑定信息


		 //同类型的
		 if (userPolicyBinds.get(0).getUserType() == record.getUserType()){
		     //账号或者IP
		     if(record.getUserType() ==1 || record.getUserType()==2){
		         resetList(record);
		         List<String> userNames = record.getUserName();
		         List<String> newUsers = new ArrayList<>(userNames);
		         List<String> repeatUserNames =  new ArrayList<>();

		         //筛选出重叠部分
		         for (String userName:userNames){
		             for (UserPolicyBindStrategy policyBind:userPolicyBinds){
		                 if(userName.equals(policyBind.getUserName())){
		                     repeatUserNames.add(userName);
                             bk = new BaseKeys();
                             bk.setMessageNo(policyBind.getMessageNo());
                             keylist.add(bk);
		                     break;
		                 }
		             }
		         }
		         //新加的进行绑定策略下发
		         newUsers.removeAll(repeatUserNames);
		         if(!newUsers.isEmpty()){
		             for (String user:newUsers){
                         userPolicyBindStrategy = new UserPolicyBindStrategy();
                         initStrategy(userPolicyBindStrategy,record);
		                 userPolicyBindStrategy.setUserName(user);
		                 userPolicyBindStrategy.setUserType(record.getUserType());
		                 userPolicyBindService.addPolicy(userPolicyBindStrategy);
		                 
		                 bk = new BaseKeys();
		         		 bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
		         		 bk.setMessageNo(userPolicyBindStrategy.getMessageNo());
		         		 bk.setDataType(DataType.POLICY.getType());
		         		 bk.setOperType(OperationType.CREATE.getType());
		         		 keylist.add(bk);
		             }
		         }
		         //去除已经存在的,对修改后不存在的进行解绑
		         for (UserPolicyBindStrategy policyBind:userPolicyBinds){
		             if(!repeatUserNames.contains(policyBind.getUserName())){
		                 initStrategy(policyBind,record);
		                 userPolicyBindService.deletePolicy(policyBind);
		             }
		         }
		     }else if(record.getUserType() == 3){
		         List<Long> puserGroups = record.getPuserGroup();
		         List<Long> newGroups = new ArrayList<>(puserGroups);
		         List<Long> repeatUerGroups = new ArrayList<>();

		         //筛选出重叠部分
		         for (Long userId:puserGroups){
		             for (UserPolicyBindStrategy policyBind:userPolicyBinds){
		                 if(userId == policyBind.getUserGroupId()){
		                     repeatUerGroups.add(userId);
                             bk = new BaseKeys();
                             bk.setMessageNo(policyBind.getMessageNo());
                             keylist.add(bk);
		                     break;
		                 }
		             }
		         }
		         //新加的进行绑定策略下发
		         newGroups.removeAll(repeatUerGroups);
		         if(!newGroups.isEmpty()){
		             for (Long user:newGroups){
                         userPolicyBindStrategy = new UserPolicyBindStrategy();
                         initStrategy(userPolicyBindStrategy,record);
		                 userPolicyBindStrategy.setUserGroupId(user);
		                 userPolicyBindStrategy.setUserType(record.getUserType());
		                 try {
		                     userPolicyBindStrategy.setUserName(commonCache.getUserGroupNameCache().get(user));
		                 } catch (ExecutionException e) {
		                     logger.debug("userGroup id is "+user + " But there is not any information about this user group" +  e);
		                 }
		                 userPolicyBindService.addPolicy(userPolicyBindStrategy);
                         bk = new BaseKeys();
                         bk.setMessageNo(userPolicyBindStrategy.getMessageNo());
                         keylist.add(bk);
		             }
		         }
                 //不存在的解绑
		         for (UserPolicyBindStrategy policyBind:userPolicyBinds){
		             if(!repeatUerGroups.contains(policyBind.getUserGroupId())){
		                 initStrategy(policyBind,record);
		                 userPolicyBindService.deletePolicy(policyBind);
		                 
		             }
		         }
		     }else {
		         //全用户不操作
			    
		     }
		 }else {
		     //对所有存在的解绑
		     for (UserPolicyBindStrategy policyBind:userPolicyBinds){
		         initStrategy(policyBind,record);
		         userPolicyBindService.deletePolicy(policyBind);
		         
		         bk = new BaseKeys();
         		 bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
         		 bk.setMessageNo(policyBind.getMessageNo());
         		 bk.setDataType(DataType.POLICY.getType());
         		 bk.setOperType(OperationType.DELETE.getType());
         		 keylist.add(bk);
		     }
		     //重新绑定不同类型的用户
		     keylist.addAll(reUserBind(record));
		 }
		 
		 return keylist;
    }


    @Override
    public ResponseResult<FlowUploadDTO> delete( String[] messageNos ) {
    	ResponseResult<FlowUploadDTO> result = new ResponseResult<FlowUploadDTO>();
    	List<BaseKeys> keylist = new ArrayList<BaseKeys>();
   	    BaseKeys bk = null;
        if(messageNos==null) {
        	result.setResult(0);
        	result.setMessage("请重试");
        	return result;
        }
        int num = messageNos.length;
        if (num<1) {
        	result.setResult(0);
        	result.setMessage("请重试");
        	return result;
        }
        Long[] array = new Long[num];
        for (int i=0;i<num;i++) {
            array[i] = Long.parseLong(messageNos[i]);
        }
        List<FlowUpload> flowUploads = flowUploadMapper.selectByPrimaryKeys(array);
        List<Long> logMessageNos = new ArrayList<>();
        List<Long> logBindMessageNos = new ArrayList<>();
        for (FlowUpload flowUpload:flowUploads){

            if(flowUpload!=null ){
                long messageSeqNO = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.FLOW_UPLOAD_POLICY.getId());
                //modify policy DB
                FlowUploadStrategy strategy = new FlowUploadStrategy();
                strategy.setMessageNo(flowUpload.getMessageNo());
                strategy.setMessageSequenceNo(messageSeqNO);
                strategy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                strategy.setOperationType(3);
                strategy.setPacketType(flowUpload.getPacketType());
                strategy.setPacketSubType(flowUpload.getPacketSubtype());
                strategy.setFreq(flowUpload.getRFreq());
                strategy.setStartTime(flowUpload.getRStarttime());
                strategy.setEndTime(flowUpload.getREndtime());
                strategy.setMethod(flowUpload.getRMethod());
                strategy.setProbeType(ProbeType.DPI.getValue());
                initStrategy(strategy,flowUpload.getZongfenId());
                deletePolicy(strategy);

                logMessageNos.add(strategy.getMessageNo());
        		 
                //get bind message
                Map<String,Object> map = new HashMap<>();
                map.put("messageNo",flowUpload.getMessageNo());
                map.put("messageType",MessageType.FLOW_UPLOAD_POLICY.getId());
                map.put("operateType",3);
                List<UserPolicyBindStrategy> userPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(map);
                FlowUploadDTO flowUploadDTO = new FlowUploadDTO();
                flowUploadDTO.setMessageNo(flowUpload.getMessageNo());
                flowUploadDTO.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                //将类型设置进去，为了方便流量流向策略的处理
                flowUploadDTO.setPacketType(flowUpload.getPacketType());
                flowUploadDTO.setPacketSubtype(flowUpload.getPacketSubtype());

                //解开绑定
                for (UserPolicyBindStrategy userPolicyBind:userPolicyBinds){
                    initStrategy(userPolicyBind,flowUploadDTO);
                    userPolicyBindService.deletePolicy(userPolicyBind);
                    logBindMessageNos.add(userPolicyBind.getMessageNo());
                }


            }
        }
        bk = new BaseKeys();
        //0=流量上报策略的时候，就用 = messageType * 100000 + packetType * 1000 + packetSubtype
        bk.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId() * 100000 + flowUploads.get(0).getPacketType() * 1000 + flowUploads.get(0).getPacketSubtype());
        bk.setPacketType(flowUploads.get(0).getPacketType());
        bk.setPacketSubtype(flowUploads.get(0).getPacketSubtype());
        Long[] lMessageNos =new Long[logMessageNos.size()];
        Long[] lBindMessageNos =new Long[logMessageNos.size()];
        bk.setMessageNos(logMessageNos.toArray(lMessageNos));
        bk.setBindMessageNo(logBindMessageNos.toArray(lBindMessageNos));
        bk.setDataType(DataType.UPLOAD.getType());
        bk.setOperType(OperationType.DELETE.getType());
        keylist.add(bk);
        result.setKeys(keylist);
        result.setResult(1);
        result.setMessage("删除成功");
        return result;
    }

    @Override
    public ResponseResult<FlowUploadDTO> reSendPolicy( FlowUploadDTO dto ) {
    	ResponseResult<FlowUploadDTO> result = new ResponseResult<FlowUploadDTO>();
    	List<BaseKeys> keylist = new ArrayList<BaseKeys>();
   	    BaseKeys bk = null;
   	    
        //重发主策略
        manualRetryPolicy(ProbeType.DPI.getValue(),MessageType.FLOW_UPLOAD_POLICY.getId(),dto.getMessageNo(),new ArrayList<>());
        
        bk = new BaseKeys();
        //0=流量上报策略的时候，就用 = messageType * 100000 + packetType * 1000 + packetSubtype
		bk.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId() * 100000 + dto.getPacketType() * 1000 + dto.getPacketSubtype());
		bk.setPacketType(dto.getPacketType());
		bk.setPacketSubtype(dto.getPacketSubtype());
		bk.setMessageNos(new Long[]{dto.getMessageNo()});
		bk.setDataType(DataType.UPLOAD.getType());
		bk.setOperType(OperationType.RESEND.getType());

		 //重发绑定策略
        Map<String,Object> map = new HashMap<>();
        map.put("messageNo",dto.getMessageNo());
        map.put("userBindMessageType",MessageType.FLOW_UPLOAD_POLICY.getId());
        map.put("operateType",3);
        //获取已经下发的绑定策略
        List<UserPolicyBindStrategy> userPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(map);
        List<Long> list = new ArrayList<>();
    	 for (UserPolicyBindStrategy strategy:userPolicyBinds){
             manualRetryPolicy(ProbeType.DPI.getValue(),MessageType.USER_POLICY_BIND.getId(),strategy.getMessageNo(),new ArrayList<>());
     		list.add(strategy.getMessageNo());
         }
        Long[] binNos = new Long[list.size()];
        bk.setBindMessageNo(list.toArray(binNos));
        keylist.add(bk);
        result.setKeys(keylist);
        result.setResult(1);
    	result.setMessage("重发成功");
    	
        return result;
    }

    private Integer getRmethodByPackType(Integer packType){
        if (packType == 1) return 1; //针对Ud类型I(Packet Type=0x01)，采用Socket方式上报
        else return 2;//针对Ud类型II(Packet Type=0x02)，采用SFTP方式上报/ 针对Ud类型Ⅲ(Packet Type=0x03)，采用SFTP方式上报
    }

    /**
     * 根据UD类型判断获取socket 或者SFTP信息
     * @param strategy
     * @param zongfenId
     */
    private void initStrategy( FlowUploadStrategy strategy, Integer zongfenId){

        ZongFenDevice zongFenDevice = null;
        try {
            zongFenDevice = commonCache.getZongFenDevInfoCache().get(zongfenId);

            //若为空刷选缓存，重新获取
            if(zongFenDevice == null
                    || StringUtils.isEmpty(zongFenDevice.getZongfenIp())
                    || zongFenDevice.getZongfenPort()==null
                    || zongFenDevice.getZongfenPort()==0
                    || zongFenDevice.getDeviceUsers().isEmpty()
                    ){
                commonCache.refreshCache();
                zongFenDevice = commonCache.getZongFenDevInfoCache().get(zongfenId);
            }
        } catch (Exception e) {
            logger.error("zongfenId is "+zongfenId + " But But there is not any information about this device" + e);
        }
        if(zongFenDevice!=null){
            try {
                if(strategy.getPacketType() == 1){
                    strategy.setDestIp(zongFenDevice.getZongfenIp());
                    strategy.setDestPort(zongFenDevice.getZongfenPort());
                    strategy.setUserName("");
                    strategy.setPassword("");
                }else {
                    //ud2 - 3 采用文件上传的形式
                    List<ZongFenDeviceUser> list= zongFenDevice.getDeviceUsers();
                    ZongFenDeviceUser zongFenDeviceUser = new ZongFenDeviceUser();
                    for (ZongFenDeviceUser user:list){
                        if(user.getPacketType()==strategy.getPacketType() && user.getPacketSubType()==strategy.getPacketSubType()){
                            zongFenDeviceUser.setZongfenFtpUser(user.getZongfenFtpUser());
                            zongFenDeviceUser.setZongfenFtpPwd(user.getZongfenFtpPwd());
                            break;
                        }
                    }
                    strategy.setUserName(zongFenDeviceUser.getZongfenFtpUser());
                    strategy.setPassword(zongFenDeviceUser.getZongfenFtpPwd());
                    strategy.setDestIp(zongFenDevice.getZongfenIp());
                    strategy.setDestPort(zongFenDevice.getZongfenFtpPort());
                }
            }catch (Exception e){
                logger.debug("get zongfen device information to init flowUpload strategy error" + e);
                strategy.setUserName("");
                strategy.setPassword("");
                strategy.setDestIp("");
                strategy.setDestPort(0);
            }
        }else {
            strategy.setUserName("");
            strategy.setPassword("");
            strategy.setDestIp("");
            strategy.setDestPort(0);
        }
    }
    
    /**
     * 对绑定策略部分信息进行初始化
     * @param userPolicyBindStrategy
     * @param record
     */
	private void initStrategy(UserPolicyBindStrategy userPolicyBindStrategy, FlowUploadDTO record) {

		BindMessage bindMessage = null;
		List<BindMessage> list = new ArrayList<BindMessage>();
		if (record == null) {
			bindMessage = new BindMessage();
			bindMessage.setBindMessageNo(userPolicyBindStrategy.getUserBindMessageNo());
			bindMessage.setBindMessageType(userPolicyBindStrategy.getUserBindMessageType());
			list.add(bindMessage);
			
			userPolicyBindStrategy.setBindInfo(list);
			userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
		} else {
			
			if (record.getPacketType().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketType().intValue()
					&& record.getPacketSubtype().intValue() == UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype().intValue() ) {
				bindMessage = new BindMessage();
				bindMessage.setBindMessageNo(record.getMessageNo());
				bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
				list.add(bindMessage);
                if(null==record.getAreaGroupMessageNo()){
                    userPolicyBindStrategy.setOperationType(3);
                    List<UserPolicyBindStrategy> bindUsers = userPolicyBindMapper.getByMessageNo(userPolicyBindStrategy);
                    for (int i=0;i<bindUsers.size();i++){
                        UserPolicyBindStrategy bindUser = bindUsers.get(i);
                        if (bindUsers.get(i).getUserBindMessageType()==MessageType.APP_FLOW_DIRECTION_POLICY.getId()){
                            record.setAreaGroupMessageNo(bindUser.getUserBindMessageNo());
                            break;
                        }
                    }
                }
				
				bindMessage = new BindMessage();
				bindMessage.setBindMessageNo(record.getAreaGroupMessageNo());
				bindMessage.setBindMessageType(MessageType.APP_FLOW_DIRECTION_POLICY.getId());
				list.add(bindMessage);
				
				userPolicyBindStrategy.setBindInfo(list);
				userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
			}else if (record.getPacketType().intValue() == UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketType().intValue()
                    && record.getPacketSubtype().intValue() == UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketSubtype().intValue() ) {
                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getMessageNo());
                bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                list.add(bindMessage);

                if(null==record.getDdosManageMessageNo()){
                    userPolicyBindStrategy.setOperationType(3);
                    List<UserPolicyBindStrategy> bindUsers = userPolicyBindMapper.getByMessageNo(userPolicyBindStrategy);
                    for (int i=0;i<bindUsers.size();i++){
                        UserPolicyBindStrategy bindUser = bindUsers.get(i);
                        if (bindUsers.get(i).getUserBindMessageType()==MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId()){
                            record.setDdosManageMessageNo(bindUser.getUserBindMessageNo());
                            break;
                        }
                    }
                }

                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getDdosManageMessageNo());
                bindMessage.setBindMessageType(MessageType.MESSAGE_TYPE_DDOS_EXCEPT_FLOW.getId());
                list.add(bindMessage);

                userPolicyBindStrategy.setBindInfo(list);
                userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
            } else if(record.getPacketSubtype().intValue() == UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype().intValue()){
                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getMessageNo());
                bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                list.add(bindMessage);
                
                if(null==record.getDdosManageMessageNo()){
                    userPolicyBindStrategy.setOperationType(3);
                    List<UserPolicyBindStrategy> bindUsers = userPolicyBindMapper.getByMessageNo(userPolicyBindStrategy);
                    for (int i=0;i<bindUsers.size();i++){
                        UserPolicyBindStrategy bindUser = bindUsers.get(i);
                        if (bindUsers.get(i).getUserBindMessageType()==MessageType.WEB_PUSH_POLICY.getId()){
                            record.setWebPushMessageNo(bindUser.getUserBindMessageNo());
                            break;
                        }
                    }
                }

                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getWebPushMessageNo());
                bindMessage.setBindMessageType(MessageType.WEB_PUSH_POLICY.getId());
                list.add(bindMessage);

                userPolicyBindStrategy.setBindInfo(list);
                userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
            } else if(record.getPacketSubtype().intValue() == UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype().intValue()){
                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getMessageNo());
                bindMessage.setBindMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
                list.add(bindMessage);


                if(null==record.getAppUserMessageNo()){
                    userPolicyBindStrategy.setOperationType(3);
                    List<UserPolicyBindStrategy> bindUsers = userPolicyBindMapper.getByMessageNo(userPolicyBindStrategy);
                    for (int i=0;i<bindUsers.size();i++){
                        UserPolicyBindStrategy bindUser = bindUsers.get(i);
                        if (bindUsers.get(i).getUserBindMessageType()==8){
                            record.setAppUserMessageNo(bindUser.getUserBindMessageNo());
                            break;
                        }
                    }
                }
                bindMessage = new BindMessage();
                bindMessage.setBindMessageNo(record.getAppUserMessageNo());
                bindMessage.setBindMessageType(MessageType.APP_USER_DEFINED.getId());
                list.add(bindMessage);

                userPolicyBindStrategy.setBindInfo(list);
                userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
            } else {
				bindMessage = new BindMessage();
				bindMessage.setBindMessageNo(record.getMessageNo());
				bindMessage.setBindMessageType(record.getMessageType());

				list.add(bindMessage);
				userPolicyBindStrategy.setBindInfo(list);
				userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
			}
		}

	}


    /**
     * List 去重
     */
    private void resetList(FlowUploadDTO recode){
        List<String> list = recode.getUserName();
        HashSet<String> h = new HashSet<String>(list);
        list.clear();
        list.addAll(h);
        recode.setUserName(list);
    }

    /**
     * 不同类型的绑定 -- 新增
     * 返回用户组应用绑定策略对象
     * @param record
     */
    protected List<BaseKeys> reUserBind(FlowUploadDTO record){
    	List<BaseKeys> keylist = new ArrayList<BaseKeys>();
    	BaseKeys bk = null;
        if(record.getUserType() == 1 || record.getUserType()==2){
            resetList(record);
            List<String> userNames = record.getUserName();
            for (String user:userNames){
            	//绑定信息
        		UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
        		userPolicyBindStrategy.setUserType(record.getUserType());
        		initStrategy(userPolicyBindStrategy,record);
        		userPolicyBindStrategy.setUserName(user);
        		userPolicyBindService.addPolicy(userPolicyBindStrategy);
        		
        		bk = new BaseKeys();
        		bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
        		bk.setMessageNo(userPolicyBindStrategy.getMessageNo());
        		bk.setDataType(DataType.POLICY.getType());
        		bk.setOperType(OperationType.CREATE.getType());
        		keylist.add(bk);
            }

        }else if(record.getUserType() == 3){
            List<Long> pusers = record.getPuserGroup();
            for (Long puserId :pusers){
                //绑定信息
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserType(record.getUserType());
                initStrategy(userPolicyBindStrategy,record);
                userPolicyBindStrategy.setUserGroupId(puserId);
                try {
                    userPolicyBindStrategy.setUserName(commonService.getUserGroupName(puserId));
                } catch (Exception e) {
                    logger.error(""+e);
                }
                userPolicyBindService.addPolicy(userPolicyBindStrategy);
                bk = new BaseKeys();
                bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
        		bk.setMessageNo(userPolicyBindStrategy.getMessageNo());
        		bk.setDataType(DataType.POLICY.getType());
        		bk.setOperType(OperationType.CREATE.getType());
        		keylist.add(bk);
            }
        }else {
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            userPolicyBindStrategy.setUserType(record.getUserType());
            initStrategy(userPolicyBindStrategy,record);
            userPolicyBindStrategy.setUserName("");
            userPolicyBindService.addPolicy(userPolicyBindStrategy);
            
            bk = new BaseKeys();
            bk.setMessageType(MessageType.USER_POLICY_BIND.getId());
    		bk.setMessageNo(userPolicyBindStrategy.getMessageNo());
    		bk.setDataType(DataType.POLICY.getType());
    		bk.setOperType(OperationType.CREATE.getType());
    		keylist.add(bk);
        }
        return keylist;
    }



    @Override
    protected boolean addDb( BaseVO baseVO ) {

        if(baseVO instanceof FlowUploadStrategy){

            //主策略存DB
            FlowUpload flowUpload = new FlowUpload();
            flowUpload.setMessageNo(baseVO.getMessageNo());
            flowUpload.setPacketType(((FlowUploadStrategy) baseVO).getPacketType());
            flowUpload.setPacketSubtype(((FlowUploadStrategy) baseVO).getPacketSubType());
            flowUpload.setZongfenId(((FlowUploadStrategy) baseVO).getZongfenId());
            flowUpload.setRStarttime(((FlowUploadStrategy) baseVO).getStartTime());
            flowUpload.setREndtime(((FlowUploadStrategy) baseVO).getEndTime());
            flowUpload.setRFreq(((FlowUploadStrategy) baseVO).getFreq());
            flowUpload.setRMethod(((FlowUploadStrategy) baseVO).getMethod());
            flowUploadMapper.insertSelective(flowUpload);

            //zf_v2_policy_messageno 数据表
            Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(1);
            policy.setCreateOper(SpringUtil.getSysUserName());//待续
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            policy.setCreateTime(new Date());
            policy.setModifyTime(new Date());
            policyMapper.insertSelective(policy);

            return true;
        }
        return false;
    }

    @Override
    protected boolean deleteDb( BaseVO baseVO ) {
        if(baseVO instanceof FlowUploadStrategy){

            //zf_v2_policy_messageNo 逻辑删除  数据表
            Policy policy = new Policy();
            policy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(3);
            policy.setModifyOper(SpringUtil.getSysUserName());
            policy.setModifyTime(new Date());
            policyMapper.updatePolicyByMessageNoAndType(policy);
            return true;
        }
        return false;
    }

    @Override
    protected boolean modifyDb( BaseVO baseVO ) {
        if(baseVO instanceof FlowUploadStrategy){

            //更新信息不发送通道
            FlowUpload flowUpload = new FlowUpload();
            flowUpload.setMessageNo(baseVO.getMessageNo());
            flowUpload.setRFreq(((FlowUploadStrategy) baseVO).getFreq());
            flowUpload.setZongfenId(((FlowUploadStrategy) baseVO).getZongfenId());
            flowUpload.setRMethod(((FlowUploadStrategy) baseVO).getZongfenId());
            flowUpload.setPacketType(((FlowUploadStrategy) baseVO).getPacketType());
            flowUpload.setPacketSubtype(((FlowUploadStrategy) baseVO).getPacketSubType());
            flowUpload.setRStarttime(((FlowUploadStrategy) baseVO).getStartTime());
            flowUpload.setREndtime(((FlowUploadStrategy) baseVO).getEndTime());
            flowUploadMapper.updateByPrimaryKeySelective(flowUpload);

            //更新zf_v2_policy_messageno 数据表
            Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.FLOW_UPLOAD_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(2);
            policy.setModifyOper(SpringUtil.getSysUserName());
            policy.setModifyTime(new Date());
            policyMapper.updatePolicyByMessageNoAndType(policy);

            return true;
        }
        return false;
    }

    @Override
    protected boolean addCustomLogic( BaseVO baseVO ) {
        return setPolicyOperateSequenceToRedis(baseVO) && addTaskAndChannelToRedis(baseVO);
    }

    @Override
    protected boolean modifyCustomLogic( BaseVO baseVO ) {
        return setPolicyOperateSequenceToRedis(baseVO);
    }

    @Override
    protected boolean deleteCustomLogic( BaseVO baseVO ) {
        return setPolicyOperateSequenceToRedis(baseVO);
    }
    
}
