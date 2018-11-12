package com.aotain.zongfen.model.monitor;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonitorTaskFile implements Serializable {
    private Long fileId;

    private Long monitorTaskId;

    private Long taskId;

    private Integer recordNum1;

    private Integer recordNum2;

    private Integer recordNum3;

    private Integer recordNum4;

    private String serverIp;

    private String fileName;
    /**
     * 文件状态：0=生成文件，1=上报失败，2=上报成功
     */
    private Short status;
    /**
     * 超时标记：0-未超时，1-超时
     */
    private Short timeoutFlag;

    private Date createTime;

    private Date modifyTime;
    
    /**
     * 超时更新时间
     */
    private Date timeoutTime;
    
    private String createTimeStr;

    private String modifyTimeStr;
    
    private String timeoutTimeStr;

    private static final long serialVersionUID = 1L;

}