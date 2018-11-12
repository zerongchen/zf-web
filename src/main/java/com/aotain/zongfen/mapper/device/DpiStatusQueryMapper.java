package com.aotain.zongfen.mapper.device;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.DpiDeviceStatusStrategy;

@MyBatisDao
public interface DpiStatusQueryMapper {
	
    int insert(DpiDeviceStatusStrategy record);

    int insertSelective(DpiDeviceStatusStrategy record);
    
    void update(DpiDeviceStatusStrategy record);
    
    List<DpiDeviceStatusStrategy> get();
    
    DpiDeviceStatusStrategy getOne(@Param("rType")Integer rType);
}