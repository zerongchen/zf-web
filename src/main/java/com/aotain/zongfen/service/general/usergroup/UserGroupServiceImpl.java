package com.aotain.zongfen.service.general.usergroup;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.constant.UserGroupAction;
import com.aotain.common.policyapi.model.UserGroupStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.policyapi.model.msg.UserMessage;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.dto.general.user.UserDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.device.DpiStaticPortMapper;
import com.aotain.zongfen.mapper.general.BrasMapper;
import com.aotain.zongfen.mapper.general.user.UserGroupMapper;
import com.aotain.zongfen.mapper.general.user.UserMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.model.general.Bras;
import com.aotain.zongfen.model.general.user.User;
import com.aotain.zongfen.model.general.user.UserGroup;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.dataImport.general.UserImportMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

@Service
public class UserGroupServiceImpl extends BaseService implements UserGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(UserGroupServiceImpl.class);
    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PolicyMapper policyMapper;

    @Autowired
    private BrasMapper brasMapper;

    @Autowired
    private DpiStaticPortMapper dpiStaticPortMapper;
    
    @Autowired
    public UserGroupStrategyService userGroupStrategyService;

    @Autowired
    private PolicyStatusMapper policyStatusMapper;

    @Autowired
    private CommonService commonService;
    
    @Override
    public synchronized ResponseResult<List<ImportResultList>> addOrUpdateUserGroup( HttpServletRequest request, UserGroup userGroup) throws ImportException {

        //日志打印,在拦截器中使用。
        List<BaseKeys> keys = new ArrayList<BaseKeys>();

        Long LogMessageNo = null;
        Map<String,InputStream> lIs = ExcelUtil.getInputStreamList(request,"importFile");

        //保存导入文件到本地，返回保存的文件名
        String fileName="";
        fileName =  commonService.saveFile(request, "importFile","userGroupFile");
        String originalFileName = ExcelUtil.getFileName(request,"importFile");

        LOG.debug("user management,user group management ，upload fileName is："+originalFileName+",save fileName is："+fileName);

        String operate = request.getParameter("operate");
        UserImportMgr importMgr = getUserImportMgr();
        final Map<String,Map<Integer,Map<Integer, String[]>>> maps = importMgr.readDataFromStream(lIs);
        boolean isNewAdd = false;
        Long currentUserGroupId =0l;
        if(userGroup.getUserGroupId()==null){
            isNewAdd = true;
            currentUserGroupId =userGroup.getUserGroupId();
        }
        List<UserMessage> actionList = new ArrayList<>();
        if(maps!=null && maps.size()>=1){
            importMgr.validateData(maps);
            Map<String,ImportResultList> importResultListMap = importMgr.getImportResultMap();

            //用于存储数据
            Set<User> newUserSet = new HashSet<User>();
            Set<User> exitUserSet = new HashSet<User>();
           

            Date time = new Date();
            Set<String> set  = importResultListMap.keySet();
            Iterator<String> it= set.iterator();
            for(;it.hasNext();){
                ImportResultList importResult= importResultListMap.get(it.next());
                if(!importResult.hasError()){
                    for(Map.Entry<String, List> listEntry:importResult.getDatas().entrySet()){
                        for(int j =0 ;j<listEntry.getValue().size();j++){
                            User importUser = (User) listEntry.getValue().get(j);
                            if(isNewAdd){
                                User dto = new User();
                                dto.setUserName(importUser.getUserName());
                                dto.setUserType(importUser.getUserType());
                                dto.setUserGroupId(userGroup.getUserGroupId());
                                dto.setOperateType(1);
                                dto.setCreateTime(time);
                                dto.setModifyTime(time);
                                dto.setCreateOper(SpringUtil.getSysUserName());
                                dto.setModifyOper(SpringUtil.getSysUserName());
                                newUserSet.add(dto);
                            }else{
                                User dto = new User();
                                dto.setUserGroupId(userGroup.getUserGroupId());
                                dto.setUserName(importUser.getUserName());
                                dto.setUserType(importUser.getUserType());
                                dto.setOperateType(Integer.parseInt(operate));
                                if(userService.isExitUser(dto)){
                                    dto.setCreateTime(time);
                                    dto.setModifyTime(time);
                                    dto.setModifyOper(SpringUtil.getSysUserName());
                                    dto.setOperateType(1);
                                    exitUserSet.add(dto);
                                }else{
                                    dto.setCreateTime(time);
                                    dto.setModifyTime(time);
                                    dto.setCreateOper(SpringUtil.getSysUserName());
                                    dto.setModifyOper(SpringUtil.getSysUserName());
                                    newUserSet.add(dto);
                                }
                            }
                        }
                    }
                }
            }
            
            List<UserMessage> newUserList = setToList(newUserSet,userGroup.getUserGroupId());
            List<UserMessage> exitUserList = setToList(exitUserSet,userGroup.getUserGroupId());

            UserGroup importUserGroup = new UserGroup();
            //增加
            if(Integer.parseInt(operate)==0){
                if(isNewAdd){//1：添加用户组
                	//1.1 发用户组策略    :添加

                    long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.USER_GROUP_POLICY.getId());
                    LogMessageNo = messageNo;
                	//1.2 新增用户组到数据库里
                    if(userGroupMapper.isSampleUserGroup(userGroup)>0){
                        ImportResultList rs = new ImportResultList();
                        rs.setResult(ImportConstant.DATA_IMPORT_FAIL_DUPLICATNAME);
                        rs.setDescribtion("该用户组名称已经存在,或者已经删除的用户组也不允许新增");
                        throw new ImportException(Arrays.asList(rs));
                    }
                    importUserGroup.setOperateType(OperationConstants.OPERATION_SAVE);
                    // 先获取messageNo
                    importUserGroup.setMessageNo(messageNo);
                    userGroup.setMessageNo(messageNo);
                    importUserGroup.setUserGroupName(userGroup.getUserGroupName());
                    userGroupMapper.insertSelective(importUserGroup);
                    currentUserGroupId = importUserGroup.getUserGroupId();
                    for (User user:newUserSet){
                        user.setUserGroupId(importUserGroup.getUserGroupId());
                    }
                    actionList.addAll(newUserList);
                    userService.insertUsers(newUserSet);

                    userGroup.setUserGroupId(currentUserGroupId);
                    sendStrategyMessage(setStrategy(UserGroupAction.ADD.getValue(),userGroup,OperationConstants.OPERATION_SAVE,newUserList));

                    /**
                     * 添加日志
                     */

                    BaseKeys bk = new BaseKeys();
                    bk.setDataType(DataType.OTHER.getType());
                    bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
                    //   String originalFileName = ExcelUtil.getFileName(request,"importFile");
                    bk.setFileName(originalFileName);
                    bk.setId(currentUserGroupId);
                    bk.setMessage("filename="+originalFileName+",messageNo="+LogMessageNo);
                    if(Integer.parseInt(operate)==0){
                        bk.setOperType(OperationType.CREATE.getType());
                    }else {
                        bk.setOperType(OperationType.DELETE.getType());
                    }
                    keys.add(bk);
                    //--------------------------------------------------
                }else{//2:更新用户组

                    UserGroup exitUserGroup =  userGroupMapper.selectSingleUserGroup(userGroup);
     
                    //2.1 发用户组策略   :添加
                    sendStrategyMessage(setStrategy(UserGroupAction.ADD.getValue(),exitUserGroup,OperationConstants.OPERATION_UPDATE,newUserList));
                    LogMessageNo = exitUserGroup.getMessageNo();
                    //2.2 存在的不变，不存在的，新增到用户里
                    userService.insertUsers(newUserSet);
                    userService.updateExitUsers(exitUserSet);
                    actionList.addAll(newUserList);

                    /**
                     * 添加日志
                     */
                    BaseKeys bk = new BaseKeys();
                    bk.setDataType(DataType.OTHER.getType());
                    bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
                    //   String originalFileName = ExcelUtil.getFileName(request,"importFile");
                    bk.setFileName(originalFileName);
                    bk.setId(currentUserGroupId);
                    bk.setMessage("filename="+originalFileName+",messageNo="+LogMessageNo+",operateType="+operate);
                    bk.setOperType(OperationType.MODIFY.getType());
                    keys.add(bk);
                    //--------------------------------------------------
                }
            //3:删除列表 
            }else {
                UserGroup exitUserGroup =  userGroupMapper.selectSingleUserGroup(userGroup);
                LogMessageNo = exitUserGroup.getMessageNo();
                //3.1 发用户组策略   :删除
                sendStrategyMessage(setStrategy(UserGroupAction.DELETE.getValue(),exitUserGroup,OperationConstants.OPERATION_DELETE,exitUserList));
                //3.2 删除用户组
                exitUserGroup.setOperateType(OperationConstants.OPERATION_DELETE);
                userService.deleteUsers(exitUserSet,exitUserGroup);
                actionList.addAll(exitUserList);

                /**
                 * 添加日志
                 */
                BaseKeys bk = new BaseKeys();
                bk.setDataType(DataType.OTHER.getType());
                bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
                //   String originalFileName = ExcelUtil.getFileName(request,"importFile");
                bk.setFileName(originalFileName);
                bk.setId(currentUserGroupId);
                bk.setMessage("messageNo="+LogMessageNo);
                bk.setOperType(OperationType.DELETE.getType());
                keys.add(bk);
                //--------------------------------------------------
            }
        }

        ResponseResult<List<ImportResultList>> responseResult = new ResponseResult<>();
        responseResult.setKeys(keys);
        responseResult.setData(new ArrayList<ImportResultList>(importMgr.getImportResultMap().values()));
        return responseResult;
    }
    private String printListOb(List<UserMessage> list){
        if (list==null || list.size()==0) return null;
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(new Consumer<UserMessage>() {
            @Override
            public void accept( UserMessage userMessage ) {
                stringBuilder.append(userMessage.objectToJson());
            }
        });
        return stringBuilder.toString();
    }
    /**
     * 
    * @Title: setToList 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param set
    * @param @param userGroupId
    * @param @return    设定文件 
    * @return List<UserMessage>    返回类型 
    * @throws
     */
    public List<UserMessage> setToList(Set<User> set,Long userGroupId){
    	List<UserMessage> list = new ArrayList<UserMessage>();
    	
        for(User user: set) {
            UserMessage usermessage = new UserMessage();
            usermessage.setUserName(user.getUserName());
        	usermessage.setUserType(user.getUserType());
        	list.add(usermessage);
        }
        return list;
    }
    /**
     * 
    * @Title: setStrategy 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param action
    * @param @param exitUserGroup
    * @param @param opType
    * @param @param userInfo
    * @param @return    设定文件 
    * @return UserGroupStrategy    返回类型 
    * @throws
     */
    private UserGroupStrategy setStrategy(Integer action,UserGroup exitUserGroup,
    		Integer opType, List<UserMessage> userInfo) {
    	 UserGroupStrategy strategy = new UserGroupStrategy();
         strategy.setAction(action);//新增用户
         if(exitUserGroup != null) {
             if(exitUserGroup.getUserGroupId()!=null){
                 strategy.setUserGroupNo(exitUserGroup.getUserGroupId());
             }
             if(exitUserGroup.getMessageNo()!=null){
                 strategy.setMessageNo(exitUserGroup.getMessageNo());
             }
             if(exitUserGroup.getUserGroupName()!=null){
                 strategy.setUserGroupName(exitUserGroup.getUserGroupName());
             }
         }
         strategy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
         strategy.setOperationType(opType);
         strategy.setProbeType(ProbeType.DPI.getValue());
         strategy.setUserInfo(userInfo);
         return strategy;
    }
    
    /**
     * 
    * @Title: setStrategy 
    * @Description: 初始化策略的实体(这里用一句话描述这个方法的作用) 
    * @param @param action
    * @param @param messageNo
    * @param @param messageSqNo
    * @param @param opType
    * @param @param userGroupName
    * @param @param userGoupNo
    * @param @return    设定文件 
    * @return UserGroupStrategy    返回类型 
    * @throws
     */
    private UserGroupStrategy setStrategy(Integer action,Long messageNo,Long messageSqNo,
    		Integer opType,String userGroupName, Long userGoupNo, List<UserMessage> userInfo) {
    	 UserGroupStrategy strategy = new UserGroupStrategy();
         strategy.setAction(action);//新增用户
         strategy.setMessageNo(messageNo);
         strategy.setMessageSequenceNo(messageSqNo);
         strategy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
         strategy.setOperationType(opType);
         strategy.setProbeType(ProbeType.DPI.getValue());
         strategy.setUserGroupName(userGroupName);
         strategy.setUserGroupNo(userGoupNo);
         strategy.setUserInfo(userInfo);
         return strategy;
    }
    /**
     * 
    * @Title: sendStrategyMessage 
    * @Description: 发策略(这里用一句话描述这个方法的作用) 
    * @param @param strategy    设定文件 
    * @return void    返回类型 
    * @throws
     */
    private UserGroupStrategy sendStrategyMessage(UserGroupStrategy strategy) {
    	if(strategy.getOperationType()==OperationConstants.OPERATION_SAVE) {//新增策略
    		userGroupStrategyService.addPolicy(strategy);
    	}else if(strategy.getOperationType()==OperationConstants.OPERATION_UPDATE) {//修改策略
    		userGroupStrategyService.modifyPolicy(strategy);
    	}else if(strategy.getOperationType()==OperationConstants.OPERATION_DELETE) {//删除策略
    		userGroupStrategyService.deletePolicy(strategy);
    	}
    	return strategy;
    }

    @Override
    public List<UserGroup> getUserGroupList( UserGroup userGroup ) {
        userGroup.setMessageType(MessageType.USER_GROUP_POLICY.getId());
        List<UserGroup> lists = userGroupMapper.getUserGroupList(userGroup);
        // 为每一个实体设置 appPolicy 和 bindPolicy
        for (int i=0;i<lists.size();i++){
            UserGroup userGroup1 = lists.get(i);
            PolicyStatus appPolicy = new PolicyStatus();
            if(userGroup1.getMessageNo()!=null) {
                PolicyStatus query2 = new PolicyStatus();
                query2.setMessageNo(userGroup1.getMessageNo());
                query2.setMessageType(MessageType.USER_GROUP_POLICY.getId());
                appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
            }
            if(appPolicy!=null) {
                userGroup1.setAppPolicy(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
            }else {
                userGroup1.setAppPolicy("0/0");
            }

        }
        return lists;
    }

    @Override
    @Transactional
    public void deleteUserGroupPolicy( String[] groupIds, String[] messageNos ) {
        Long[] userGroups = new Long[groupIds.length];
        Long[] policyMessageNos =new Long[messageNos.length];
        UserGroup deleteUserGroup = null;
        for(int i=0 ;i <groupIds.length;i++){
            
            Long userGroupNo = Long.valueOf(groupIds[i]);
           
            List<UserMessage> infoList = getListByGroupId(userGroupNo);
            
            userGroups[i]=userGroupNo;
            //查找要被删除的UserGroup
            deleteUserGroup = new UserGroup();
            deleteUserGroup.setUserGroupId(userGroupNo);
            deleteUserGroup = userGroupMapper.selectSingleUserGroup(deleteUserGroup);
          
            // 发用户组策略    :删除
            sendStrategyMessage(setStrategy(UserGroupAction.DELETE.getValue(),deleteUserGroup,OperationConstants.OPERATION_DELETE,infoList)); 
        }
        for(int i=0 ;i <messageNos.length;i++){
            policyMessageNos[i]=Long.valueOf(messageNos[i]);
        }
        //删除数据库
        policyMapper.deletePolicyByMessageNoAndType(policyMessageNos,MessageType.USER_GROUP_POLICY.getId());
        userService.deleteUsersByGroups(userGroups);
    }
    /**
     * 
    * @Title: getListByGroupId 
    * @Description: 根据UserGroupID获取userinfo信息(这里用一句话描述这个方法的作用) 
    * @param @param userGroupNo
    * @param @return    设定文件 
    * @return List<UserMessage>    返回类型 
    * @throws
     */
    private List<UserMessage> getListByGroupId(Long userGroupNo){
    	 List<UserMessage> userInfoList = new ArrayList<UserMessage>();
    	 UserDTO dto = new UserDTO();
        dto.setUserGroupId(userGroupNo);
        List<UserDTO> userList = userService.getUsers(dto);
        for(UserDTO user:userList) {
             UserMessage userInfo = new UserMessage();
             userInfo.setUserName(user.getUserName());
        	 userInfo.setUserType(user.getUserType());
        	 userInfoList.add(userInfo);
         }
         return userInfoList;
    }

    /**
     * 用户组名称暂不支持修改
     * @param userGroup
     */
    @Override
    @Deprecated
    public void updateUserGroupName( UserGroup userGroup ) {
    	/*****************************************************************************
         * 发用户组策略                                                                                                     *修改**
         *****************************************************************************/  
    	 List<UserMessage> infoList = getListByGroupId(userGroup.getUserGroupId());
    	long messageSqNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.USER_GROUP_POLICY.getId());
        sendStrategyMessage(setStrategy(UserGroupAction.ADD.getValue(),userGroup.getMessageNo(),messageSqNo,
        		OperationConstants.OPERATION_DELETE,userGroup.getUserGroupName(),userGroup.getUserGroupId(),infoList)); 
        
        userGroupMapper.updateUserGroupName(userGroup);
        policyMapper.updatePolicyName(userGroup.getUserGroupName(),userGroup.getMessageNo(),MessageType.USER_GROUP_POLICY.getId());
    }

    @Override
    public boolean resendPolicy(long topTaskId,long messageNo, List<String> dpiIp){
        // 重发主策略
        manualRetryPolicy(topTaskId,0,MessageType.USER_GROUP_POLICY.getId(),messageNo,dpiIp);
        return true;
    }

    @Override
    public boolean existSameGroupName(UserGroup userGroup){
        return userGroupMapper.isSampleUserGroup2(userGroup)>0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualAddOrUpdateUserGroup(UserGroupStrategy userGroupStrategy,BaseEntity baseEntity){
        UserGroup userGroup = new UserGroup();
        int messageType = MessageType.USER_GROUP_POLICY.getId();

        userGroup.setMessageType(messageType);
        userGroup.setUserGroupId(userGroupStrategy.getUserGroupId());
        userGroup.setUserGroupName(userGroupStrategy.getUserGroupName());
        userGroup.setMessageNo(userGroupStrategy.getMessageNo());
        BeanUtils.copyProperties(baseEntity,userGroup);

        boolean update = userGroup.getUserGroupId()==null?false:true;


        if (!update){
            userGroupStrategy.setOperationType(1);
            long messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.USER_GROUP_POLICY.getId());
            userGroupStrategy.setMessageNo(messageNo);
            userGroup.setMessageNo(messageNo);
            userGroupMapper.insertSelective(userGroup);

            userGroupStrategy.setUserGroupNo(userGroup.getUserGroupId());
            UserGroupStrategy result = sendStrategyMessage(userGroupStrategy);
            userGroup.setMessageNo(result.getMessageNo());

        } else {
            userGroupStrategy.setOperationType(2);
            userGroupStrategy.setUserGroupNo(userGroup.getUserGroupId());
            sendStrategyMessage(userGroupStrategy);
            userGroupMapper.updateUserGroupName(userGroup);
        }

        for (int i=0;i<userGroupStrategy.getUserInfo().size();i++){
            User user = new User();
            // 设置userGroupId
            user.setUserGroupId(userGroup.getUserGroupId());
            user.setOperateType(1);
            // 设置userName userType
            BeanUtils.copyProperties(userGroupStrategy.getUserInfo().get(i),user);
            //操作人等基本信息
            BeanUtils.copyProperties(baseEntity,user);
            if (userMapper.selectSingleUser(user)>0){
                Set<User> set = new HashSet<User>();
                set.add(user);
                userMapper.updateExitUsers(set);
            } else {
                userMapper.insertSelective(user);
            }

        }

    }

    @Override
    public List<DpiStaticPort> getLinkUser(){
        return dpiStaticPortMapper.selectDistinctLinkList();
    }

    @Override
    public List<Bras> getBrasUser(){
        return brasMapper.selectDistinctBrasList();
    }

    /**
     * 根据不同的导入用户类型返回校验管理器
     * @return
     */
    private UserImportMgr getUserImportMgr() {
        UserImportMgr userImportMgr = (UserImportMgr) SpringUtil.getBean("userImportMgr");
        if (userImportMgr != null) {
            userImportMgr.initValidator(); // 初始化校验管理器
        }
        return userImportMgr;
    }

    @Override
    protected boolean addDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean deleteDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean modifyDb(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean addCustomLogic(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean modifyCustomLogic(BaseVO policy) {
        return true;
    }

    @Override
    protected boolean deleteCustomLogic(BaseVO policy) {
        return true;
    }
}
