package com.aotain.zongfen.service.general.usergroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aotain.zongfen.dto.general.user.UserDTO;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.user.User;
import com.aotain.zongfen.model.general.user.UserGroup;

public interface UserService {

    /**
     * 判断该用户账号是否已经存在
     * @param dto
     * @return
     */
    boolean isExitUser( User dto);


    /**
     * 批量新增用户
     * @param dtos
     * @return
     */
    int insertUsers( Set<User> dtos );

    /**
     * 对存在的用户更新操作日期 和状态
     * @param dtos
     * @return
     */
    int updateExitUsers( Set<User> dtos);

    /**
     * 操作为删除的时候，删除用户组对应的用户，实际上是将用户operate_type设置为1
     * @param dtos
     * @return
     */
    int deleteUsers( Set<User> dtos , UserGroup userGroup) ;

    /**
     * 删除某个用户组下得一组用户,实际上是将用户operate_type设置为1
     * @param array
     * @return
     */
    int deleteUsersByGroups(Long[] array);

    /**
     * 获取用户信息，用于查询用户列表
     * @param dto
     * @return
     */
    List<UserDTO> getUsers( UserDTO dto);

    /**
     * 前端直接删除用户列表
     * @param users
     * @return
     */
    Long deleteSelectUsers(List<UserDTO> users);

    /**
     * 新增用户
     * @param recodes
     */
    ResponseResult insetUser( List<User> recodes);
}
