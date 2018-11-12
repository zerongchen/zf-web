package com.aotain.zongfen.mapper.general;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.GeneralApp;

@MyBatisDao
public interface GeneralAppMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GeneralApp record);

    int insertSelective(GeneralApp record);

    GeneralApp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GeneralApp record);

    int updateByPrimaryKey(GeneralApp record);
    
    int insertList(List<GeneralApp> list);
    
    List<GeneralApp> getIndexList(Map<String,String> query);
    
    int deleteAll();

    int countAppByAppName(GeneralApp record);
}