package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppFlowMonitorDetailDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1815675501703992122L;
	private Long statTime;//统计时间
	private String fileName;
	private String uploadfileSize;
	private String createfileSize;
	private String uploadIp;
	private String filecreateTime;
	private String fileuploadTime;
//（起始至终止时间段，默认近24小时）
	private Long startTime;//查询时间：起始时间
	private Long endTime;//查询时间：结束时间
	private String dpiIp;
	private String order;
	private String sort;
}
