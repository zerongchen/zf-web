package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitorTaskDetailAndFileDTO implements Serializable {
	/***********************detail********************/
    private Long taskId;

    private Long monitorTaskId;

    private String taskName;

    private String monitorParams;
    /**
     * 任务对应表主键 如果是发策略的话，就是messageNo
     */
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
     * 这个很关键 *
     * 文件状态：0=生成文件，1=上报失败，2=上报成功
     * 处理状态,3=失败，4=成功，5=超时
     */
    private Short status;

    private String createTime;

    private String modifyTime;
    /**
     * 任务完成时间
     */
    private String completetime;

    private String taskParams;
    
    /***********************file*************************/
    private Long fileId;
    
    private Integer recordNum1;

    private Integer recordNum2;

    private Integer recordNum3;

    private Integer recordNum4;

    private String serverIp;

    private String fileName;
    /**
     * 超时标记：0-未超时，1-超时
     */
    private Short timeoutFlag;
    
    /**
     * 超时更新时间
     */
    private String timeoutTime;
    
    private Integer countNum;//查询的记录数

    private static final long serialVersionUID = 1L;

}