package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.CommonCookie;

@MyBatisDao
public interface CommonCookieMapper {
    int deleteByPrimaryKey(Long cookieId);

    int insert(CommonCookie record);

    int insertSelective(CommonCookie record);

    CommonCookie selectByPrimaryKey(Long cookieId);

    int updateByPrimaryKeySelective(CommonCookie record);

    int updateByPrimaryKey(CommonCookie record);
    
    int isSameDate(CommonCookie record);
    
    int isSameDates(CommonCookie record);
    
    int insertList(List<CommonCookie> records);
    
    List<CommonCookie> getCookieList();
    
    int deleteByIds(String[] ids);
}