package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DpiDynamicCpu implements Serializable {
    private Integer cpuNo;

    private Integer cpuUsage;

    private Date createTime;
    
    private String createTimeStr;

    private static final long serialVersionUID = 1L;

   
}