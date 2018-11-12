package com.aotain.zongfen.model.monitor;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitorTaskDetail implements Serializable {
    private Long taskId;

    private Long monitorTaskId;

    private String taskName;

    private String monitorParams;

    private Long dataId;

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
     * 处理状态,3=失败，4=成功，5=超时
     */
    private Short status;

    private Date createtime;

    private Date completetime;

    private String taskParams;
    
    private String createtimeStr;

    private String completetimeStr;
    
    private Integer count;//数据库的limit参数
    /**
     * taskType=2,只有failNum和successNum
     * taskType=5，createNum、failNum和successNum
     */
    private Integer createNum;//生成记录数
    private Integer failNum;//失败记录数
    private Integer successNum;//成功记录数
    
    private String title;
    private String param;
    private String content;
    
    private static final long serialVersionUID = 1L;

}