package com.aotain.zongfen.mapper.general.user;

import java.util.List;
import java.util.Set;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.user.UserDTO;
import com.aotain.zongfen.model.general.user.User;
import com.aotain.zongfen.model.general.user.UserGroup;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface UserMapper {


    int insertSelective(User record);

    int selectSingleUser( User record);

    int insertUsers(@Param("set") Set<User> set);

    int updateExitUsers(@Param("set") Set<User> set);

    int deleteUsers(@Param("set") Set<User> set,@Param("userGroup") UserGroup userGroup);

    int deleteUsersByGroups(Long[] array);

    List<UserDTO> getUsers( UserDTO dto);

    int deleteOrUpdateUsers(List<UserDTO> list);
}