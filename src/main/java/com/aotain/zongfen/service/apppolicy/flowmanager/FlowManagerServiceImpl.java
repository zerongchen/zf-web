package com.aotain.zongfen.service.apppolicy.flowmanager;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.BindAction;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.GeneralFlowStrategy;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.BindMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.apppolicy.FlowManagerDTO;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.apppolicy.AppFlowManagerMapper;
import com.aotain.zongfen.mapper.apppolicy.AppFlowManagerUserGroupMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.mapper.policy.UserPolicyBindMapper;
import com.aotain.zongfen.model.apppolicy.AppFlowManager;
import com.aotain.zongfen.model.apppolicy.AppFlowManagerUserGroup;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.userbind.UserPolicyBindService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.WebParamUtils;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import kafka.coordinator.group.BaseKey;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.rngom.parse.host.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class FlowManagerServiceImpl extends BaseService implements FlowManagerService{

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(FlowManagerServiceImpl.class);

    @Autowired
    private UserPolicyBindMapper userPolicyBindMapper;

    @Autowired
    private AppFlowManagerUserGroupMapper appFlowManagerUserGroupMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private AppFlowManagerMapper appFlowManagerMapper;

    @Autowired
    private CommonService commonService;

     /**
     * 用户应用绑定策略的service层调用
     */
    @Autowired
    private UserPolicyBindService userPolicyBindService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Override
    public List<FlowManagerDTO> getList( FlowManagerDTO flowManagerDTO ) {
        flowManagerDTO.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
        List<FlowManagerDTO> list = appFlowManagerMapper.getList(flowManagerDTO);
        String policyCount =null;
        String bindPolicy =null;
        for (FlowManagerDTO dto:list){
            dto.setTimeBar(WebParamUtils.paseToBinaryString(dto.getCTime(),24));
            dto.setStartTime(DateUtils.parse2DateString(dto.getRStarttime(),DateFormatConstant.DATE_CHS_HYPHEN));
            dto.setEndTime(DateUtils.parse2DateString(dto.getREndtime(),DateFormatConstant.DATE_CHS_HYPHEN));
            dto.setApptypeDesc(commonService.getAppTypeName(dto.getApptype()));
            dto.setActiveFlag(sendPolicy(dto));

            PolicyStatus policyStatusObj =null;
            if(dto.getMessageNo()!=null) {
                PolicyStatus policyStatus = new PolicyStatus();
                policyStatus.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
                policyStatus.setMessageNo(dto.getMessageNo());
                policyStatusObj = policyStatusMapper.getCountTotalAndFail(policyStatus);
            }

            if(policyStatusObj!=null) {
                policyCount = policyStatusObj.getMainCount() == null ? "0/0" : policyStatusObj.getMainCount();
                bindPolicy = policyStatusObj.getBindCount() == null ? "0/0" : policyStatusObj.getBindCount();
            }else {
                policyCount="0/0";
                bindPolicy = "0/0";
            }
            dto.setPolicyCount(policyCount);
            dto.setBindPolicyCount(bindPolicy);
        }
        return list;
    }


    @Override
    public ResponseResult addDB( FlowManagerDTO flowManagerDTO ) {
       return operateDB(flowManagerDTO,1);
    }

    @Override
    public ResponseResult modifyDB( FlowManagerDTO flowManagerDTO ) {
        return operateDB(flowManagerDTO,2);
    }

    @Override
    public ResponseResult deleteMessage( String[] ids) {

        /**
         * 操作日志参数返回
         */
        ResponseResult responseResult = new ResponseResult();
        List<BaseKeys> keys = new ArrayList<>();
        Date date  =new Date();
        if (ids==null) return null;
        int num = ids.length;
        if(num<1) return null;
        for (int i=0;i<num;i++){
            FlowManagerDTO dto = new FlowManagerDTO();
            dto.setAppFlowId(Long.valueOf(ids[i]));
            List<FlowManagerDTO> dtos = appFlowManagerMapper.getList(dto);
             if (dtos!=null && dtos.size()>0){
                FlowManagerDTO flowManagerDTO= dtos.get(0);
                AppFlowManager flowManager = new AppFlowManager();
                flowManager.setAppFlowId(flowManagerDTO.getAppFlowId());
                flowManager.setOperateType(3);
                flowManager.setModifyTime(date);
                flowManager.setModifyOper(SpringUtil.getSysUserName());
                appFlowManagerMapper.updateByPrimaryKeySelective(flowManager);
                 BaseKeys bk = new BaseKeys();
                 bk.setId(Long.valueOf(ids[i]));
                 bk.setMessageType(ModelType.MODEL_FLOW_MANAGER.getMessageType());
                 bk.setOperType(OperationType.DELETE.getType());
                 bk.setDataType(DataType.POLICY.getType());
                 keys.add(bk);

                flowManagerDTO.setStartTime(DateUtils.parse2DateString(flowManagerDTO.getRStarttime(),DateFormatConstant.DATE_CHS_HYPHEN));
                flowManagerDTO.setEndTime(DateUtils.parse2DateString(flowManagerDTO.getREndtime(),DateFormatConstant.DATE_CHS_HYPHEN));
                if(sendPolicy(flowManagerDTO)){
                    GeneralFlowStrategy strategy = new GeneralFlowStrategy();
                    initStrategy(strategy,flowManagerDTO);
                    deletePolicy(strategy);
                }
                keys.addAll(removeUser(flowManagerDTO,sendPolicy(flowManagerDTO),true));
            }
        }
        responseResult.setResult(1);
        responseResult.setMessage("删除成功");
        return responseResult;
    }

    @Override
    public ResponseResult reSendPolicy( FlowManagerDTO flowManagerDTO ) {

        ResponseResult responseResult = new ResponseResult();

        if(flowManagerDTO.getMessageNo()==null || !sendPolicy(flowManagerDTO)){
            responseResult.setResult(0);
            responseResult.setMessage("不在策略有效期");
            return responseResult;
        }
        List<BaseKeys> keys = new ArrayList<>();
        //重发主策略
        manualRetryPolicy(ProbeType.DPI.getValue(),MessageType.GENERAL_FLOW_POLICY.getId(),flowManagerDTO.getMessageNo(),new ArrayList<>());
        BaseKeys bk = new BaseKeys();
        bk.setMessageNo(flowManagerDTO.getMessageNo());
        bk.setId(flowManagerDTO.getAppFlowId());
        bk.setMessageType(ModelType.MODEL_FLOW_MANAGER.getMessageType());
        bk.setOperType(OperationType.RESEND.getType());
        bk.setDataType(DataType.POLICY.getType());
        keys.add(bk);

        //重发绑定策略
        Map<String,Object> map = new HashMap<>();
        map.put("messageNo",flowManagerDTO.getMessageNo());
        map.put("userBindMessageType",MessageType.GENERAL_FLOW_POLICY.getId());
        map.put("operateType",3);
        //获取已经下发的绑定策略
        List<UserPolicyBindStrategy> userPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(map);
        List<Long> bindList = new ArrayList<Long>();
        for (UserPolicyBindStrategy strategy:userPolicyBinds){
        	bindList.add(strategy.getMessageNo());
            manualRetryPolicy(ProbeType.DPI.getValue(),MessageType.USER_POLICY_BIND.getId(),strategy.getMessageNo(),new ArrayList<>());
        }

        responseResult.setBindMessageNo(bindList);
        responseResult.setResult(1);
        responseResult.setMessage("重发成功");
        return responseResult;
    }


    /**
     * 操作DB
     * @param flowManagerDTO
     * @param operateType 1 新增 ,2修改
     */

    public ResponseResult operateDB(FlowManagerDTO flowManagerDTO , Integer operateType){

        ResponseResult responseResult = new ResponseResult();
        AppFlowManager appFlowManager = new AppFlowManager();
        appFlowManager.setMessageName(flowManagerDTO.getMessageName());

        if(operateType==2){
            appFlowManager.setAppFlowId(flowManagerDTO.getAppFlowId());
        }
        int count = appFlowManagerMapper.isSameName(appFlowManager);
        if(count>0){
            responseResult.setResult(0);
            responseResult.setMessage("已经存在相同策略名称");
            return responseResult;
        }
        resetList(flowManagerDTO);
        if(!StringUtils.isEmpty(flowManagerDTO.getStartTime())){
            appFlowManager.setRStarttime(DateUtils.parse2TimesTamp(flowManagerDTO.getStartTime(),DateFormatConstant.DATE_CHS_HYPHEN));
        }else {
            appFlowManager.setRStarttime((long)0);
        }
        if(!StringUtils.isEmpty(flowManagerDTO.getEndTime())){
            appFlowManager.setREndtime(DateUtils.parse2TimesTamp(flowManagerDTO.getEndTime(),DateFormatConstant.DATE_CHS_HYPHEN));
        }else {
            appFlowManager.setREndtime((long)0);
        }

        boolean sendPolicy=sendPolicy(flowManagerDTO);

        Date date = new Date();
        appFlowManager.setAppid(flowManagerDTO.getAppid());
        appFlowManager.setAppname(flowManagerDTO.getAppname());
        appFlowManager.setApptype(flowManagerDTO.getApptype());
        appFlowManager.setAppThresholdUpAbs(flowManagerDTO.getAppThresholdUpAbs());
        appFlowManager.setAppThresholdDnAbs(flowManagerDTO.getAppThresholdDnAbs());
        appFlowManager.setCTime(Long.valueOf(WebParamUtils.paseBinarytoInt(flowManagerDTO.getTimeBar())));
        appFlowManager.setOperateType(1);
        appFlowManager.setModifyOper(SpringUtil.getSysUserName());

        if(operateType == 1){
            appFlowManager.setCreateOper(SpringUtil.getSysUserName());
            appFlowManager.setCreateTime(date);
            appFlowManager.setModifyTime(date);
            /*****************添加返回信息************************/

            List<BaseKeys> keys = new ArrayList<BaseKeys>();

            if(sendPolicy) {
                Long messageNo =MessageNoUtil.getInstance().getMessageNo(MessageType.GENERAL_FLOW_POLICY.getId());
                appFlowManager.setMessageNo(messageNo);
                appFlowManagerMapper.insertSelective(appFlowManager);
                GeneralFlowStrategy strategy = new GeneralFlowStrategy();
                flowManagerDTO.setMessageNo(messageNo);
                flowManagerDTO.setAppFlowId(appFlowManager.getAppFlowId());
                //主策略初始化
                initStrategy(strategy,flowManagerDTO);
                addPolicy(strategy);
                keys.addAll(addUsers(flowManagerDTO,true));
            }else {
                appFlowManagerMapper.insertSelective(appFlowManager);
                flowManagerDTO.setAppFlowId(appFlowManager.getAppFlowId());
                keys.addAll(addUsers(flowManagerDTO,false));
            }
            BaseKeys bk = new BaseKeys();
            bk.setId(appFlowManager.getAppFlowId());
            bk.setMessageType(ModelType.MODEL_FLOW_MANAGER.getMessageType());
            bk.setOperType(OperationType.CREATE.getType());
            bk.setDataType(DataType.POLICY.getType());
            keys.add(bk);
            responseResult.setResult(1);
            responseResult.setMessage("新增成功");
            responseResult.setKeys(keys);
            return responseResult;
        } else {
            List<BaseKeys> keys = new ArrayList<BaseKeys>();
            if(sendPolicy){
                if(flowManagerDTO.getMessageNo()==null){
                    Long messageNo =MessageNoUtil.getInstance().getMessageNo(MessageType.GENERAL_FLOW_POLICY.getId());
                    appFlowManager.setMessageNo(messageNo);
                    flowManagerDTO.setMessageNo(messageNo);
                }else {
                    appFlowManager.setMessageNo(flowManagerDTO.getMessageNo());
                }
                GeneralFlowStrategy strategy = new GeneralFlowStrategy();
                //主策略初始化
                initStrategy(strategy,flowManagerDTO);
                modifyPolicy(strategy);


            }
            appFlowManager.setModifyTime(new Date());
            appFlowManager.setOperateType(2);
            appFlowManager.setMessageName(flowManagerDTO.getMessageName());
            appFlowManagerMapper.updateByPrimaryKeySelective(appFlowManager);
            BaseKeys mainbk = new BaseKeys();
            mainbk.setId(flowManagerDTO.getAppFlowId());
            mainbk.setMessageType(ModelType.MODEL_FLOW_MANAGER.getMessageType());
            mainbk.setOperType(OperationType.MODIFY.getType());
            mainbk.setDataType(DataType.POLICY.getType());
            keys.add(mainbk);
            //get bind message
            AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
            appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
            List<AppFlowManagerUserGroup> list = appFlowManagerUserGroupMapper.getAppGroup(appFlowManagerUserGroup);

            if(list!=null && list.get(0).getUserType() != flowManagerDTO.getUserType()){

                //删除已经存在的绑定策略
                //get bind message

                AppFlowManagerUserGroup userGroup = new AppFlowManagerUserGroup();
                userGroup.setAppflowId(flowManagerDTO.getAppFlowId());
                appFlowManagerUserGroupMapper.delete(userGroup);

                BaseKeys bk = new BaseKeys();
                bk.setId(flowManagerDTO.getAppFlowId());
                bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                bk.setOperType(OperationType.DELETE.getType());
                bk.setDataType(DataType.OTHER.getType());
                bk.setMessage("appFlow="+flowManagerDTO.getAppFlowId()+",userType="+list.get(0).getUserType()+",bindAction="+BindAction.UNBIND.getValue());
                keys.add(bk);

                Map<String,Object> map = new HashMap<>();
                map.put("messageNo",flowManagerDTO.getMessageNo());
                map.put("userBindMessageType",MessageType.GENERAL_FLOW_POLICY.getId());
                map.put("operateType",3);
                //获取已经下发的绑定策略
                List<UserPolicyBindStrategy> userPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(map);
                for (UserPolicyBindStrategy bindStrategy:userPolicyBinds){
                    initBindStrategy(bindStrategy,flowManagerDTO.getMessageNo());
                    userPolicyBindService.deletePolicy(bindStrategy);
                }
                //新增绑定策略
                keys.addAll(addUsers(flowManagerDTO,sendPolicy));

            }else {
                if(flowManagerDTO.getUserType() == 1 || flowManagerDTO.getUserType()==2){
                    List<String>  afterModify = flowManagerDTO.getUserName();
                    List<String>  beforeModify = new ArrayList<String>();
                    for (AppFlowManagerUserGroup us:list){
                        if(!StringUtils.isEmpty(us.getUserName())){
                            beforeModify.add(us.getUserName());
                        }
                    }
                    //新增的用户
                    List<String> newAddList =  new ArrayList<String>(afterModify);
                    newAddList.removeAll(beforeModify);
                    FlowManagerDTO dto = new FlowManagerDTO();
                    dto.setUserName(newAddList);
                    dto.setAppFlowId(flowManagerDTO.getAppFlowId());
                    dto.setUserType(flowManagerDTO.getUserType());
                    dto.setMessageNo(flowManagerDTO.getMessageNo());
                    keys.addAll(addUsers(dto,sendPolicy));

                    //已经存在的用户，但是修改后不存在的
                    beforeModify.removeAll(afterModify);
                    FlowManagerDTO managerDTO = new FlowManagerDTO();
                    managerDTO.setUserName(beforeModify);
                    managerDTO.setAppFlowId(flowManagerDTO.getAppFlowId());
                    managerDTO.setUserType(flowManagerDTO.getUserType());
                    managerDTO.setMessageNo(flowManagerDTO.getMessageNo());
                    keys.addAll(removeUser(managerDTO,sendPolicy,false));

                }else if(flowManagerDTO.getUserType()==3){
                    List<Long>  afterModify = flowManagerDTO.getPuserGroup();
                    List<Long>  beforeModify = new ArrayList<Long>();
                    for (AppFlowManagerUserGroup us:list){
                        if(us.getUserGroupId()!=null){
                            beforeModify.add(us.getUserGroupId());
                        }
                    }
                    //新增的用户组 = 修改后的 - 已经存在的
                    List<Long> newAddList =  new ArrayList<Long>(afterModify);
                    newAddList.removeAll(beforeModify);

                    //新增的用户
                    newAddList.removeAll(beforeModify);
                    FlowManagerDTO dto = new FlowManagerDTO();
                    dto.setPuserGroup(newAddList);
                    dto.setAppFlowId(flowManagerDTO.getAppFlowId());
                    dto.setUserType(flowManagerDTO.getUserType());
                    dto.setMessageNo(flowManagerDTO.getMessageNo());
                    keys.addAll(addUsers(dto,sendPolicy));

                    //已经存在的用户，但是修改后不存在的
                    beforeModify.removeAll(afterModify);
                    FlowManagerDTO managerDTO = new FlowManagerDTO();
                    managerDTO.setPuserGroup(beforeModify);
                    managerDTO.setAppFlowId(flowManagerDTO.getAppFlowId());
                    managerDTO.setUserType(flowManagerDTO.getUserType());
                    managerDTO.setMessageNo(flowManagerDTO.getMessageNo());
                    keys.addAll(removeUser(managerDTO,sendPolicy,false));
                }
            }
            responseResult.setResult(1);
            responseResult.setMessage("修改成功");
            responseResult.setKeys(keys);
            return responseResult;
        }
    }

    /**
     * 根据UserId 获取用户组名
     * @param userGroupId
     * @return
     */
    protected String getUserGroupName(Long userGroupId){

        return commonService.getUserGroupName(userGroupId);
    }

    /**
     * 添加用户绑定
     * @param flowManagerDTO
     */
    public List<BaseKeys> addUsers(FlowManagerDTO flowManagerDTO , boolean sendPolicy){

        /*****************添加返回信息************************/
        List<BaseKeys> keys = new ArrayList<BaseKeys>();

        if(flowManagerDTO.getUserType()==0){
            AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
            appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
            appFlowManagerUserGroup.setUserType(flowManagerDTO.getUserType());
            appFlowManagerUserGroupMapper.insertSelective(appFlowManagerUserGroup);

            if(!sendPolicy){
                BaseKeys bk = new BaseKeys();
                bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                bk.setOperType(OperationType.CREATE.getType());
                bk.setDataType(DataType.OTHER.getType());
                String idKey = "appFlowId="+appFlowManagerUserGroup.getAppflowId()+
                        ",userType="+appFlowManagerUserGroup.getUserType()+",bindAction="+ BindAction.BIND.getValue();
                bk.setMessage(idKey);
                keys.add(bk);
            }
        }else if(flowManagerDTO.getUserType() == 1 || flowManagerDTO.getUserType()==2){
            List<String>  list = flowManagerDTO.getUserName();
            if (list!=null && list.size()>0){
                List<AppFlowManagerUserGroup> recodes = new ArrayList<AppFlowManagerUserGroup>();
                for (String userName:list){
                    AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
                    appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
                    appFlowManagerUserGroup.setUserType(flowManagerDTO.getUserType());
                    appFlowManagerUserGroup.setUserName(userName);
                    recodes.add(appFlowManagerUserGroup);
                    if(!sendPolicy) {
                        BaseKeys bk = new BaseKeys();
                        bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                        bk.setOperType(OperationType.CREATE.getType());
                        bk.setDataType(DataType.OTHER.getType());
                        String idKey = "appFlowId=" + appFlowManagerUserGroup.getAppflowId() +
                                ",userType=" + appFlowManagerUserGroup.getUserType() + ",userName=" + appFlowManagerUserGroup.getUserName()
                                + ",bindAction=" + BindAction.BIND.getValue();
                        bk.setMessage(idKey);
                        keys.add(bk);
                    }
                }
                appFlowManagerUserGroupMapper.insertSelectiveList(recodes);
            }
        }else if(flowManagerDTO.getUserType() == 3){
            List<Long>  list = flowManagerDTO.getPuserGroup();
            if (list!=null && list.size()>0){
                List<AppFlowManagerUserGroup> recodes = new ArrayList<AppFlowManagerUserGroup>();
                for (Long puserId:list){
                    AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
                    appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
                    appFlowManagerUserGroup.setUserType(flowManagerDTO.getUserType());
                    appFlowManagerUserGroup.setUserGroupId(puserId);
                    appFlowManagerUserGroup.setUserName(getUserGroupName(puserId));
                    recodes.add(appFlowManagerUserGroup);

                    if(!sendPolicy) {
                        BaseKeys bk = new BaseKeys();
                        bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                        bk.setOperType(OperationType.CREATE.getType());
                        bk.setDataType(DataType.OTHER.getType());
                        String idKey = "appFlowId=" + appFlowManagerUserGroup.getAppflowId() +
                                ",userType=" + appFlowManagerUserGroup.getUserType() + ",userGroupId=" + appFlowManagerUserGroup.getUserGroupId()
                                + ",bindAction=" + BindAction.BIND.getValue();
                        bk.setMessage(idKey);
                        keys.add(bk);
                    }
                }
                appFlowManagerUserGroupMapper.insertSelectiveList(recodes);
            }
        }
        if(sendPolicy) {
            if (flowManagerDTO.getUserType() == 0) {
                UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                userPolicyBindStrategy.setUserType(flowManagerDTO.getUserType());
                initBindStrategy(userPolicyBindStrategy,flowManagerDTO.getMessageNo());
                userPolicyBindStrategy.setUserName("");
                userPolicyBindStrategy.setUserGroupId((long) 0);
                userPolicyBindService.addPolicy(userPolicyBindStrategy);
            }else if(flowManagerDTO.getUserType() == 1 || flowManagerDTO.getUserType()==2){
                List<String>  list = flowManagerDTO.getUserName();
                for (String userName:list){
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserType(flowManagerDTO.getUserType());
                    initBindStrategy(userPolicyBindStrategy,flowManagerDTO.getMessageNo());
                    userPolicyBindStrategy.setUserName(userName);
                    userPolicyBindStrategy.setUserGroupId((long)0);
                    userPolicyBindService.addPolicy(userPolicyBindStrategy);
                }
            }else if(flowManagerDTO.getUserType() == 3){
                List<Long>  list = flowManagerDTO.getPuserGroup();
                for (Long puserId:list){
                    UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
                    userPolicyBindStrategy.setUserType(flowManagerDTO.getUserType());
                    initBindStrategy(userPolicyBindStrategy,flowManagerDTO.getMessageNo());
                    userPolicyBindStrategy.setUserGroupId(puserId);
                    userPolicyBindStrategy.setUserName(getUserGroupName(puserId));
                    userPolicyBindService.addPolicy(userPolicyBindStrategy);
                }
            }
        }

        return keys;

    }

    /**
     * 解除绑定
     * @param flowManagerDTO
     */
    public List<BaseKeys> removeUser(FlowManagerDTO flowManagerDTO , boolean sendPolicy ,boolean deleteAll){

        /*****************添加返回信息************************/
        List<BaseKeys> keys = new ArrayList<BaseKeys>();

        if(deleteAll){
            AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
            appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
            appFlowManagerUserGroupMapper.delete(appFlowManagerUserGroup);

            if(!sendPolicy) {
                BaseKeys bk = new BaseKeys();
                bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                bk.setOperType(OperationType.DELETE.getType());
                bk.setDataType(DataType.OTHER.getType());
                String idKey = "appFlowId=" + appFlowManagerUserGroup.getAppflowId() +
                        ",userType=" + flowManagerDTO.getUserType() + ",bindAction=" + BindAction.UNBIND.getValue();
                bk.setMessage(idKey);
                keys.add(bk);
            }

        }else {
            if(flowManagerDTO.getUserType() == 1 || flowManagerDTO.getUserType()==2){
                List<String>  list = flowManagerDTO.getUserName();
                List<AppFlowManagerUserGroup> recodes = new ArrayList<AppFlowManagerUserGroup>();
                if(list!=null && list.size()>0){
                    for (String userName:list){
                        AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
                        appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
                        appFlowManagerUserGroup.setUserType(flowManagerDTO.getUserType());
                        appFlowManagerUserGroup.setUserName(userName);
                        recodes.add(appFlowManagerUserGroup);
                        if(!sendPolicy) {
                            BaseKeys bk = new BaseKeys();
                            bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                            bk.setOperType(OperationType.DELETE.getType());
                            bk.setDataType(DataType.OTHER.getType());
                            String idKey = "appFlowId=" + appFlowManagerUserGroup.getAppflowId() +
                                    ",userType=" + flowManagerDTO.getUserType() + ",userName=" + userName + ",bindAction=" + BindAction.UNBIND.getValue();
                            bk.setMessage(idKey);
                            keys.add(bk);
                        }
                    }
                    appFlowManagerUserGroupMapper.deleteList(recodes);
                }
            }else if(flowManagerDTO.getUserType() == 3){
                List<Long>  list = flowManagerDTO.getPuserGroup();
                List<AppFlowManagerUserGroup> recodes = new ArrayList<AppFlowManagerUserGroup>();
                if(list!=null && list.size()>0){
                    for (Long puserId:list){
                        AppFlowManagerUserGroup appFlowManagerUserGroup = new AppFlowManagerUserGroup();
                        appFlowManagerUserGroup.setAppflowId(flowManagerDTO.getAppFlowId());
                        appFlowManagerUserGroup.setUserType(flowManagerDTO.getUserType());
                        appFlowManagerUserGroup.setUserGroupId(puserId);
                        recodes.add(appFlowManagerUserGroup);
                        if(!sendPolicy) {
                            BaseKeys bk = new BaseKeys();
                            bk.setMessageType(ModelType.MODEL_APP_USER_BIND.getMessageType());
                            bk.setOperType(OperationType.DELETE.getType());
                            bk.setDataType(DataType.OTHER.getType());
                            String idKey = "appFlowId=" + appFlowManagerUserGroup.getAppflowId() +
                                    ",userType=" + flowManagerDTO.getUserType() + ",userGroupId=" + puserId + ",bindAction=" + BindAction.UNBIND.getValue();
                            bk.setMessage(idKey);
                            keys.add(bk);
                        }
                    }
                    appFlowManagerUserGroupMapper.deleteList(recodes);
                }
            }
        }

        if(sendPolicy && flowManagerDTO.getMessageNo()!=null){
            UserPolicyBindStrategy userPolicyBindStrategy = new UserPolicyBindStrategy();
            userPolicyBindStrategy.setUserType(flowManagerDTO.getUserType());
            initBindStrategy(userPolicyBindStrategy,flowManagerDTO.getMessageNo());

            //get bind message
            Map<String,Object> map = new HashMap<>();
            map.put("messageNo",flowManagerDTO.getMessageNo());
            map.put("userBindMessageType",MessageType.GENERAL_FLOW_POLICY.getId());
            map.put("operateType",3);
            //获取已经下发的绑定策略
            List<UserPolicyBindStrategy> userPolicyBinds =userPolicyBindMapper.getByBindMessageNoAndType(map);
            if(userPolicyBinds!=null && userPolicyBinds.size()>0){
                for (UserPolicyBindStrategy strategy:userPolicyBinds){
                    initBindStrategy(strategy,flowManagerDTO.getMessageNo());
                    //策略类型是 1 或者 2
                    if(flowManagerDTO.getUserType()==1 || flowManagerDTO.getUserType()==2){
                        //获取需要解绑的账号
                        List<String>  list = flowManagerDTO.getUserName();
                        if(list!=null && list.size()>0){
                            for (String userName:list){
                                //已经绑定的策略用户如果和需要删除的账号一致,且类型一致
                                if(userName.equals(strategy.getUserName())){
                                    userPolicyBindService.deletePolicy(strategy);
                                }
                            }
                        }
                    }else if(flowManagerDTO.getUserType()==3){
                        List<Long>  list = flowManagerDTO.getPuserGroup();
                        if(list!=null && list.size()>0){
                            for (long puserId:list){
                                if(strategy.getUserGroupId()!=null && puserId == strategy.getUserGroupId()){
                                    userPolicyBindService.deletePolicy(strategy);
                                }
                            }
                        }
                    }else if(flowManagerDTO.getUserType()==0){
                        //全用户直接解绑
                        userPolicyBindService.deletePolicy(strategy);
                    }
                }
            }
        }

        return keys;
    }

    /**
     * 对绑定策略部分信息进行初始化
     */
    private void initBindStrategy( UserPolicyBindStrategy userPolicyBindStrategy , Long messageNo){

        BindMessage bindMessage = new BindMessage();
        bindMessage.setBindMessageNo(messageNo);
        bindMessage.setBindMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
        List<BindMessage> list = new ArrayList<BindMessage>();
        list.add(bindMessage);
        userPolicyBindStrategy.setBindInfo(list);
        userPolicyBindStrategy.setProbeType(ProbeType.DPI.getValue());
    }

    /**
     * 对主策略策略部分信息进行初始化
     */
    private void initStrategy( GeneralFlowStrategy strategy , FlowManagerDTO flowManagerDTO){

        if(flowManagerDTO.getAppid()==0){
            strategy.setAppName(flowManagerDTO.getAppname());
        }else {
            strategy.setAppName("");
        }
        Long cTime =0l;
        if(flowManagerDTO.getCTime()!=null){
            cTime = flowManagerDTO.getCTime();
        }else if(!StringUtils.isEmpty(flowManagerDTO.getTimeBar())){
            cTime = Long.valueOf(WebParamUtils.paseBinarytoInt(flowManagerDTO.getTimeBar()));
        }
        strategy.setCTime(cTime);
        strategy.setAppId(flowManagerDTO.getAppid());
        strategy.setAppType(flowManagerDTO.getApptype());
        strategy.setAppThresholdDown(flowManagerDTO.getAppThresholdDnAbs());
        strategy.setAppThresholdUp(flowManagerDTO.getAppThresholdUpAbs());
        strategy.setMessageNo(flowManagerDTO.getMessageNo());
        strategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.GENERAL_FLOW_POLICY.getId()));
        strategy.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
        strategy.setMessageName(flowManagerDTO.getMessageName());
    }

    /**
     * List 去重
     */
    private void resetList(FlowManagerDTO recode){
        if(recode.getUserType()==1 || recode.getUserType()==2){
            List<String> list = recode.getUserName();
            HashSet h = new HashSet(list);
            list.clear();
            list.addAll(h);
            recode.setUserName(list);
        }
    }

    /**
     * 判断当前操作是否是在策略有效期
     * @param flowManagerDTO
     * @return
     */
    private boolean sendPolicy(FlowManagerDTO flowManagerDTO){

        if (StringUtils.isEmpty(flowManagerDTO.getStartTime())){
            return true;
        }
        Date startDate = DateUtils.parse(DateFormatConstant.DATE_CHS_HYPHEN,flowManagerDTO.getStartTime());
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar current = Calendar.getInstance();
        if(current.getTimeInMillis()>=start.getTimeInMillis()) {
            if(StringUtils.isEmpty(flowManagerDTO.getEndTime())){
                return true;
            }else {
                Date endDate  = DateUtils.parse(DateFormatConstant.DATE_CHS_HYPHEN,flowManagerDTO.getEndTime());
                Calendar end = Calendar.getInstance();
                end.setTime(endDate);
                end.set(Calendar.DATE,end.get(Calendar.DATE)+1);
                if(current.getTimeInMillis()<=end.getTimeInMillis()){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected boolean addDb( BaseVO baseVO ) {
        if(baseVO instanceof GeneralFlowStrategy){

            //zf_v2_policy_messageno 数据表
//            Long messageSeqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.GENERAL_FLOW_POLICY.getId());
            Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(1);
            policy.setCreateOper(SpringUtil.getSysUserName());//待续
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            Date date = new Date();
            policy.setCreateTime(date);
            policy.setModifyTime(date);
            policyMapper.insertSelective(policy);
            baseVO.setOperationType(1);
            baseVO.setProbeType(ProbeType.DPI.getValue());

            return true;
        }

        return false;
    }

    @Override
    protected boolean deleteDb( BaseVO baseVO ) {

        //zf_v2_policy_messageno 数据表
        Policy policy = new Policy();
        policy.setMessageName(baseVO.getMessageName());
        policy.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
        policy.setMessageNo(baseVO.getMessageNo());
        policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
        policy.setOperateType(3);
        policy.setModifyOper(SpringUtil.getSysUserName());//待续
        Date date = new Date();
        policy.setModifyTime(date);
        policyMapper.updatePolicyByMessageNoAndType(policy);

        baseVO.setOperationType(3);
        baseVO.setProbeType(ProbeType.DPI.getValue());
        return true;
    }

    @Override
    protected boolean modifyDb( BaseVO baseVO ) {
        if(baseVO instanceof GeneralFlowStrategy){

            //zf_v2_policy_messageno 数据表
            Policy policy = new Policy();
            policy.setMessageName(baseVO.getMessageName());
            policy.setMessageType(MessageType.GENERAL_FLOW_POLICY.getId());
            policy.setMessageNo(baseVO.getMessageNo());
            policy.setMessageSequenceno(baseVO.getMessageSequenceNo());
            policy.setOperateType(2);
            policy.setModifyOper(SpringUtil.getSysUserName());//待续
            Date date = new Date();
            policy.setModifyTime(date);
            policyMapper.updatePolicyByMessageNoAndType(policy);
            baseVO.setOperationType(2);
            baseVO.setProbeType(ProbeType.DPI.getValue());
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
