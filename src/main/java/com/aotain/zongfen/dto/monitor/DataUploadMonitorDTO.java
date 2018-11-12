package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataUploadMonitorDTO {
	private Long statTime;//统计时间,UTC格式(这个时间就是上报时间)
	private String uploadTime;	
	private String receivedTime;
	private Integer uploadFileNum;//上报文件数
	private Double uploadFileSize;//上报文件大小,单位KB	
	private String uploadFileSizeStr;	
	private Integer receivedFileNum;//接收文件数
	private Long receivedFileSize;//接收文件大小,单位KB	
	private String receivedFileSizeStr;		
	//（起始至终止时间段，默认近24小时）
	private String startTime;//查询时间：起始时间
	private String endTime;//查询时间：结束时间		
	private Long createFileSize;	
	private String filecreateTime;
	private Integer dateType; //时间粒度 1为小时，2为天，默认为小时
    private Integer fileType;//文件类型:1=AAA生成文件信息,2=全业务流量生成文件信息,3=业务流量流向生成文件信息(生成文件监控统计表)，4HTTP GET数据上报监控
    private String  creatFileSizeUnit;
    private Integer abnormalFileNum;//异常文件数
    private Integer totalAbnormalFileNum;
    private String order;//排序

}
