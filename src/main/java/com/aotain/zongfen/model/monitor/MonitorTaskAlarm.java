package com.aotain.zongfen.model.monitor;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitorTaskAlarm implements Serializable {
    private Integer alarmId;

    private Long monitorTaskId;

    private Long taskId;

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

    private String alarmContent;

    private String alarmParams;

    private Date alarmTime;
    /**
     * 处理状态,0=未处理，1=已处理
     */
    private Short dealStatus;

    private String dealSolution;

    private String dealUser;

    private Date dealTime;

    private static final long serialVersionUID = 1L;

}