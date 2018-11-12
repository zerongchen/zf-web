package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpGetMonitorDetailDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3701051293226899213L;
	
	private Long statTime;//统计时间
	private Byte tableType;

	private String fileName;//文件名称

    private String uploadIp;//上报服务器IP

    private String receivedIp;//接收服务器IP
    
    private String dpiIp;//DPI服务器IP

    private String filecreateTime;//文件生成时间,UTC格式

    private String filereceivedTime;//文件接收时间,UTC格式

    private Long fileSize;//文件大小,单位KB

    private Byte isSuccess;//是否成功，1=success, 0=fail
    
	//（起始至终止时间段，默认近24小时）
	private Long startTime;//查询时间：起始时间
	private Long endTime;//查询时间：结束时间
	
	private String fileuploadTime;//文件上报时间(用来比较的时间)
	  
	private String areaId;//区域ID(作为查询条件)
	
	private Byte probeType;
	
	private String softwareProvider;//软件厂家编号

}
