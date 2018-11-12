package com.aotain.zongfen.mapper.general.user;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.user.UserStaticIPDTO;
import com.aotain.zongfen.model.general.user.UserStaticIP;

import java.util.List;

@MyBatisDao
public interface UserStaticIPMapper {

    int insertSelective(UserStaticIP record);

    int insertSelectiveList(List<UserStaticIP> records);

    int updateOrDelete(List<UserStaticIP> records);

    List<UserStaticIP> getExitList(UserStaticIP recode);

    UserStaticIPDTO selectOne( Long ipId);

    List<UserStaticIPDTO> getUserIpInfo(Long userId);


}