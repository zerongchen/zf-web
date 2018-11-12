package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.CommonThreshold;

@MyBatisDao
public interface CommonThresholdMapper {
    int deleteByPrimaryKey(Integer commonId);

    int insert(CommonThreshold record);

    int insertSelective(CommonThreshold record);

    CommonThreshold selectByPrimaryKey(Integer commonId);

    int updateByPrimaryKeySelective(CommonThreshold record);

    int updateByPrimaryKey(CommonThreshold record);
    
    CommonThreshold getThreshold();
}