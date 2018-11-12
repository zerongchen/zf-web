package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.MonitorTaskInfoDTO;
import com.aotain.zongfen.model.monitor.MonitorTaskInfo;

@MyBatisDao
public interface MonitorTaskInfoMapper {

    int insert(MonitorTaskInfo record);

    MonitorTaskInfoDTO select(Long monitorTaskId);

    int update(MonitorTaskInfo record);
    
    List<MonitorTaskInfoDTO> selectList(MonitorTaskInfoDTO dto);

}