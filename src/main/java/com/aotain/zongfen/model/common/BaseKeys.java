package com.aotain.zongfen.model.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)  
public class BaseKeys {
	private Integer messageType;//0=流量上报策略的时候，就用 = messageType * 100000 + packetType * 1000 + packetSubtype
	private Long id;
	private Long messageNo;
	private String messageName;
	private Integer operType;//操作类型， 参考com.aotain.zongfen.log.constant.OperationType
	private String message;//例如：appId=xx,appName=xx,time=xxx
	private Long[] bindMessageNo;
	private Long[] messageNos;
	
	private String fileName;//文件名
	private Integer packetType;
	private Integer packetSubtype;
	
	private Integer dataType;//1=策略的操作（1-4），2=文件操作（5-8），0=流量上报策略
	
	private String idName;//id对应的字段名称
	
	
}
