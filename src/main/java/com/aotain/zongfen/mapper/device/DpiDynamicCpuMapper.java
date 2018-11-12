package com.aotain.zongfen.mapper.device;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiDynamicCpu;

@MyBatisDao
public interface DpiDynamicCpuMapper {
    int insert(DpiDynamicCpu record);

    int insertSelective(DpiDynamicCpu record);
    
    List<DpiDynamicCpu> selectCPU();
    
    List<DpiDynamicCpu> selectByCpu(@Param("cpuNo")Integer cpuNo);
}