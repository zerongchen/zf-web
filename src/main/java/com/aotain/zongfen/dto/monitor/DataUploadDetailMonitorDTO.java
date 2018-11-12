package com.aotain.zongfen.dto.monitor;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataUploadDetailMonitorDTO {
	private static final long serialVersionUID = -1815675501703992122L;
	private String statTime;//统计时间
	private String fileName;
	private String dpiIp;
	private String  factoryFrom;  //来源厂家
	private String createFileSize;
	private String recieveFileSize;
	private String uploadFileSize;
	private String uploadIp;
	private String   filerecieveTime;//接收时间
	private String fileuploadTime;
	private String filecreateTime;
	private String   description;//异常的描述
	private String   warnType;//异常的描述
    //（起始至终止时间段，默认近24小时）
	private Long startTime;//查询时间：起始时间
	private Long endTime;//查询时间：结束时间
	private Integer dateType;
	private Integer fileType;
	private Integer detailType;
	private String order;
	private String sort;

}
