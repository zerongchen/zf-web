package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.monitor.MonitorTaskFile;

@MyBatisDao
public interface MonitorTaskFileMapper {

    int insert(MonitorTaskFile record);

    MonitorTaskFile select(Long fileId);

    int update(MonitorTaskFile record);
    
    /**
     * 根据monitorTaskId、taskId、status
     * @param record
     * @return
     */
    List<MonitorTaskFile> selectList(MonitorTaskFile record);
    /**
     * 根据monitorTaskId、taskId、status
     * @param record
     * @return
     */
    int countNum(MonitorTaskFile record);

}