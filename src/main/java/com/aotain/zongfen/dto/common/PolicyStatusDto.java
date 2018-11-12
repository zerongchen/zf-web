package com.aotain.zongfen.dto.common;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略下发详情
 */
@Data
@NoArgsConstructor
public class PolicyStatusDto {

	private static final long serialVersionUID = 1L;

	private Integer messageType;

	private Long messageNo;
	
	private Long bindMessageNo;

	private String dpiIp;
	
	private Integer status;
	
	private String areaName;
	
	private Integer userType;
	
	private String userGroupName;
	
	private String deviceName;
	
	private Integer pageSize;
	
	private Integer pageIndex;
	
	private Integer searchType;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
}
