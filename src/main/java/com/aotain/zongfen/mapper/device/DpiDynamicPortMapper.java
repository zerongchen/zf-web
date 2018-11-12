package com.aotain.zongfen.mapper.device;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiDynamicPort;

@MyBatisDao
public interface DpiDynamicPortMapper {
    int insert(DpiDynamicPort record);

    int insertSelective(DpiDynamicPort record);
    
    List<DpiDynamicPort> selectPort();
    
    List<DpiDynamicPort> selectByPort(@Param("portno")Integer portno);
}