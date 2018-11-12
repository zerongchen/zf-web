package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DpiUploadDevice implements Serializable {
    /** 
	* @Fields serialVersionUID 
	*/  
	private static final long serialVersionUID = 1L;

	private Integer dpiId;

    private String startIp;

    private String endIp;

    private Integer probeType;

    private String areaId;

    private String softwareProvider;
    
    private String createOper;

    private String modifyOper;

    private Date createTime;

    private Date modifyTime;

    /**
     * 页面展示
     */
    private String areaName;
    
    /**
     * 页面展示
     */
    private String factoryName;
    
    private String[] startIps;
    
    private String[] endIps;
}