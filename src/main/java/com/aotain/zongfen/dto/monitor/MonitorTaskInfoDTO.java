package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitorTaskInfoDTO implements Serializable {
    private Long monitorTaskId;

    private String monitorName;

    private String monitorParams;

    /**
     * 2=主动上报,3=导出任务,4=DPI策略,5=文件上报,6=Azkaban任务,99=系统消息
     */
    private Integer taskType;
    /**
     * 主动上报:2000=全业务流量分析上报,2001=应用流量流向信息上报
     * 导出任务:3000
     * DPI策略:MessageType
     * 文件上报:6000
     * Azkaban任务:7000
     * 系统消息:
     */
    private Integer taskSubtype;
    /**
     * 处理状态,0=正常，1=异常
     */
    private Short status;

    private String createTime;

    private String modifyTime;

    private String taskParams;
    
    private String startTime;
    
    private String endTime;
    
    private String taskTitle;

    private static final long serialVersionUID = 1L;

}