package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpGetMonitorDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1099404110479839889L;

	private Long statTime;//统计时间,UTC格式(这个时间就是上报时间)
	
	private String uploadTime;
	
	private String receivedTime;

	private Byte probeType;//设备类型:0=DPI,1=EU

	private String softwareProvider;//软件厂家编号

	private Integer uploadFileNum;//上报文件数

	private Double uploadFileSize;//上报文件大小,单位KB
	
	private String uploadFileSizeStr;
	
	private Integer receivedFileNum;//接收文件数

	private Double receivedFileSize;//接收文件大小,单位KB
	
	private String receivedFileSizeStr;
	
	private Integer level;//0=root 全省，1=dpi/eu,2=areaid或机房id，3=厂家
	
	private Byte tableType;//表格类型，1=分钟表格，2=小时表格，3=天表格（默认为2）
	//（起始至终止时间段，默认近24小时）
	private String startTime;//查询时间：起始时间
	private String endTime;//查询时间：结束时间
	
	private String areaId;//区域ID(作为查询条件)
	
	private String order;//排序顺序：ASC、DESC
}
