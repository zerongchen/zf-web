package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.CommonSearch;

@MyBatisDao
public interface CommonSearchMapper {
    int deleteByPrimaryKey(Long seId);

    int insert(CommonSearch record);

    int insertSelective(CommonSearch record);

    CommonSearch selectByPrimaryKey(Long seId);

    int updateByPrimaryKeySelective(CommonSearch record);

    int updateByPrimaryKey(CommonSearch record);
    
    int insertList(List<CommonSearch> records);
    
    List<CommonSearch> getAllSearch();
    
    int getRecordByname(String seName);
    
    int isSameRecord(CommonSearch record);
    
    int deleteByIds(String[] ids);
    
    List<String> getAllSearchName();
}