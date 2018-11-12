package com.aotain.zongfen.mapper.general;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.GeneralURL;

@MyBatisDao
public interface GeneralURLMapper {
    int deleteByPrimaryKey(Long urlId);

    int insert(GeneralURL record);

    int insertSelective(GeneralURL record);

    GeneralURL selectByPrimaryKey(Long urlId);

    int updateByPrimaryKeySelective(GeneralURL record);

    int updateByPrimaryKey(GeneralURL record);
    
    int insertList(List<GeneralURL> array);
    
    List<GeneralURL> getIndexList(Map<String,String> query);
    
    int deleteAll();
}