package com.aotain.zongfen.service.general.usergroup;

import java.util.*;

import com.aotain.common.policyapi.constant.BindAction;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.UserGroupAction;
import com.aotain.common.policyapi.model.UserGroupStrategy;
import com.aotain.common.policyapi.model.msg.UserMessage;
import com.aotain.zongfen.dto.general.user.UserDTO;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.general.user.UserGroupMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.user.UserGroup;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.general.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.zongfen.mapper.general.user.UserMapper;
import com.aotain.zongfen.model.general.user.User;

import javax.swing.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;


    @Autowired
    private CommonService commonService;

    @Autowired
    private UserGroupStrategyService userGroupStrategyService;

    /**
     * 判断用户组内的某个账号是否存在
     * @param dto
     * @return
     */
    @Override
    public boolean isExitUser( User dto ) {
        return userMapper.selectSingleUser(dto)>0;
    }


    @Override
    public int insertUsers( Set<User> dtos ) {
        if(dtos.size()==0){
            return 0;
        }
        return userMapper.insertUsers(dtos);
    }

    @Override
    public int updateExitUsers( Set<User> dtos) {
        if(dtos.size()==0){
            return 0;
        }
        return userMapper.updateExitUsers(dtos);
    }

    @Override
    public int deleteUsers( Set<User> dtos,UserGroup userGroup ) {
        if(dtos.size()<1){
            return 0;
        }
//        userGroup.setOperateType(3);
        return userMapper.deleteUsers(dtos,userGroup);
    }

    @Override
    public int deleteUsersByGroups( Long[] array ) {
        return userMapper.deleteUsersByGroups(array) ;
    }

    @Override
    public List<UserDTO> getUsers( UserDTO dto ) {
        List<UserDTO> list = userMapper.getUsers(dto);
        for (UserDTO userDTO:list){
            userDTO.setUserTypeDesc(UserType.getUserTypeDesc(userDTO.getUserType()));
        }
        return list;
    }

    @Override
    public synchronized Long deleteSelectUsers( List<UserDTO> users) {

        if (users.size()<1) return null;

        userMapper.deleteOrUpdateUsers(users);

        List<UserMessage> list = new ArrayList<UserMessage>(users.size());
        for (UserDTO userDTO:users){
            UserMessage userMessage = new UserMessage();
            userMessage.setUserType(userDTO.getUserType());
            userMessage.setUserName(userDTO.getUserName());
            list.add(userMessage);
        }
        UserDTO firstDto = users.get(0);
        UserGroupStrategy strategy = new UserGroupStrategy();
        strategy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
        strategy.setMessageNo(firstDto.getMessageNo());
        strategy.setOperationType(2);
        strategy.setMessageName(commonService.getUserGroupName(firstDto.getUserGroupId()));
        strategy.setUserGroupName(commonService.getUserGroupName(firstDto.getUserGroupId()));
        strategy.setUserGroupNo(firstDto.getUserGroupId());
        strategy.setAction(UserGroupAction.DELETE.getValue());
        strategy.setUserInfo(list);
        userGroupStrategyService.modifyPolicy(strategy);
        return strategy.getMessageNo();
    }

    @Override
    public ResponseResult insetUser( List<User> recodes ) {

        UserDTO dto= new UserDTO();
        dto.setUserGroupId(recodes.get(0).getUserGroupId());
        List<UserDTO> dtos = userMapper.getUsers(dto);
        List<User> exits = new ArrayList<User>();
        for (UserDTO userDTO:dtos){
            User user = new User();
            user.setUserGroupId(userDTO.getUserGroupId());
            user.setUserName(userDTO.getUserName());
            user.setUserType(userDTO.getUserType());
            exits.add(user);
        }
        recodes.removeAll(exits);
        ResponseResult responseResult =new ResponseResult();
        if (recodes.size()<1){
            responseResult.setResult(0);
            responseResult.setMessage("新增的用户已经存在");
            return responseResult;
        }
        Set<User> sets = resetList2Set(recodes);
        List<UserMessage> userMessages = new ArrayList<UserMessage>();

        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        Date time = new Date();
        for (User user:sets){
            user.setOperateType(1);
            user.setCreateTime(time);
            user.setModifyTime(time);
            user.setCreateOper(SpringUtil.getSysUserName());
            user.setModifyOper(SpringUtil.getSysUserName());

            UserMessage userMessage = new UserMessage();
            userMessage.setUserType(user.getUserType());
            userMessage.setUserName(user.getUserName());
            userMessages.add(userMessage);
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.CREATE.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
            bk.setDataType(DataType.OTHER.getType());
            bk.setMessage("userGroupId="+user.getUserGroupId()+",userType="+user.getUserType()+",userName="+user.getUserName());
            keys.add(bk);
        }
        userMapper.insertUsers(sets);

        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupId(recodes.get(0).getUserGroupId());
        UserGroup exitObj = userGroupMapper.selectSingleUserGroup(userGroup);

        UserGroupStrategy strategy = new UserGroupStrategy();
        strategy.setMessageType(MessageType.USER_GROUP_POLICY.getId());
        strategy.setMessageNo(exitObj.getMessageNo());
        strategy.setOperationType(2);
        strategy.setMessageName(exitObj.getUserGroupName());
        strategy.setUserGroupName(exitObj.getUserGroupName());
        strategy.setUserGroupNo(exitObj.getUserGroupId());
        strategy.setAction(UserGroupAction.ADD.getValue());
        strategy.setUserInfo(userMessages);
        userGroupStrategyService.modifyPolicy(strategy);

        responseResult.setKeys(keys);
        responseResult.setResult(1);
        responseResult.setMessage("添加成功");
        return responseResult;
    }


    /**
     * List 去重
     * @param list
     */
    protected Set<User> resetList2Set(List<User> list){
        HashSet<User> h = new HashSet<User>(list);
        return h;
    }
}
