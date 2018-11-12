package com.aotain.zongfen.dto.monitor;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UdOneDataDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -3729626884654536986L;
	
	private Long statTime;
	
	private String receivedTime;

    private Integer packetsubtype;
    
    private String packetsubname;
	
	private Long receivednum;//接收记录数
	
	private Long savenum;//保存记录数

    private Long validrecordernum;//有效记录数

    private Long invalidrecordernum;//无效记录数

    private Long writerkafkanum;//写入kafka记录数  对应页面显示属性为解析记录数

    private Byte probeType;

    private String areaId;

    private String softwareProvider;
    
    private Integer level;//0=root 全省，1=dpi/eu,2=areaid或机房id，3=厂家
	
	private Byte tableType;//表格类型，1=分钟表格，2=小时表格，3=天表格（默认为2）
	//（起始至终止时间段，默认近24小时）
	private String startTime;//查询时间：起始时间
	private String endTime;//查询时间：结束时间
	
	private int chartType;//1=receved(接收记录图)、2=save(保存记录图)


}
