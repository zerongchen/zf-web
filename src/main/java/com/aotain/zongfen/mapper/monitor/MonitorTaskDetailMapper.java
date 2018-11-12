package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.monitor.MonitorTaskDetail;

@MyBatisDao
public interface MonitorTaskDetailMapper {

    int insert(MonitorTaskDetail record);

    MonitorTaskDetail select(Long taskId);

    int update(MonitorTaskDetail record);
    
    long count(@Param("monitorTaskId")Long monitorTaskId);
    
    List<MonitorTaskDetail> selectList(@Param("monitorTaskId")Long monitorTaskId, @Param("count")Integer count);

}