package com.aotain.zongfen.model.general;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenIPAddress implements Serializable {
    private Long ipId;
   
    private String ipType;
    
    private String startIp;

    private String endIp;
    
    private String areaName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date updateTime;
    
    private String ipSegment;

    private static final long serialVersionUID = 1L;
    
    //省份名称
    private String provinceName;
    
    private String areaId;//2进制字符串

    @Override
    public String toString() {
        return "GenIPAddress{" +
                "ipId=" + ipId +
                ", ipType='" + ipType + '\'' +
                ", startIp='" + startIp + '\'' +
                ", endIp='" + endIp + '\'' +
                ", areaName='" + areaName + '\'' +
                ", updateTime=" + updateTime +
                ", ipSegment='" + ipSegment + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", areaId='" + areaId + '\'' +
                '}';
    }

    public String getErrorMsg(){
        return provinceName+"|"+areaName+"|"+areaId+"|"+startIp+"|"+endIp;
    }
}