package com.aotain.zongfen.dto.apppolicy;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoipFlowDTO {

	public String policyId;
	
	public String policyName;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	public Date createTime;
	
	public String sataus;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	public Date updataTime;
	
	public String userNo;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	public Date startTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	public Date endTime;
	
	public Integer timeBar;
	
	public Integer voipFlowId;
	
	public Integer interfereType;
	
	public Integer interfereDir;
}
