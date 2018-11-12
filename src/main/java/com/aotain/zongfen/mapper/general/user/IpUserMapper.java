package com.aotain.zongfen.mapper.general.user;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.model.general.user.IpUser;

import java.util.List;


@MyBatisDao
public interface IpUserMapper {

    int deleteByPrimaryKey(Long userId);

    int insert(IpUser record);

    int insertSelective(IpUser record);

    IpUser selectByPrimaryKey(Long userId);

    IpUser selectOne( IpUserDTO recode );

    int updateByPrimaryKeySelective(IpUser record);

    int updateByPrimaryKey(IpUser record);

    List<IpUserDTO> getList( IpUserDTO recode);
}