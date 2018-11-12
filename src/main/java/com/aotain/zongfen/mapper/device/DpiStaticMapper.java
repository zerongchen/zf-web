package com.aotain.zongfen.mapper.device;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiStatic;

@MyBatisDao
public interface DpiStaticMapper {
    int deleteByPrimaryKey(String deploysitename);

    int insert(DpiStatic record);

    int insertSelective(DpiStatic record);

    DpiStatic selectByPrimaryKey(String deploysitename);

    int updateByPrimaryKeySelective(DpiStatic record);

    int updateByPrimaryKey(DpiStatic record);
    
    List<DpiStatic> selectList(DpiStatic record);
}