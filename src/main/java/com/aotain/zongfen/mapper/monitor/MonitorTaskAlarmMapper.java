package com.aotain.zongfen.mapper.monitor;

import java.util.List;
import java.util.Map;

import com.aotain.zongfen.utils.PageResult;
import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.model.monitor.MonitorTaskAlarm;

@MyBatisDao
public interface MonitorTaskAlarmMapper {

    int insert(MonitorTaskAlarm record);

    MonitorTaskAlarmDTO select(Long monitorTaskId);

    int update(MonitorTaskAlarm record);
    
    List<MonitorTaskAlarmDTO> selectList(MonitorTaskAlarmDTO dto);
    /**
     * 取出处理方案列表
     * @param taskType
     * @param taskSubtype
     * @return
     */
    List<MonitorTaskAlarmDTO> selectDealSolution(@Param("taskType")Integer taskType,@Param("taskSubtype")Integer taskSubtype);


    List<Map<String,String>> selectCreateException(Map<String, String> params);

    List<Map<String,String>> selectReceivedException(Map<String, String> params);

    List<Map<String,String>> selectUploadException(Map<String, String> params);
}