package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppFlowMonitorDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1741008484807025671L;
	private Long statTime;
	private String createTime;
	private Integer createFileNum;//生成文件数
	private Double createFileSize;//目前以生成文件总大小作为页面的文件总大小
	private Long createFileRecord;//生成记录数（上报记录数）
	private String creatFileSizeUnit;
	
	private String startTime;//查询条件的开始时间
	private String endTime;//查询条件的结束时间
	/**
     * 查询升降序
     */
    private String orderBy;

	/**
	 * 时间粒度 (1:分钟，2：小时，3：天)
	 */
	private Integer dateType;
}
