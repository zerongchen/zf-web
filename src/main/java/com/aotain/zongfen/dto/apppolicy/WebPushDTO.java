package com.aotain.zongfen.dto.apppolicy;

import java.util.Date;

import com.aotain.common.policyapi.model.base.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebPushDTO extends BaseVO {


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
	
	public Integer webPushId;
	
	public Short advType;

	public Long advWhitehostlistid;
	
	public String advframeUrl;

	public Long advToken;

	public Long advDelay;
	
	public String triggerHostlistid;
	
	public String triggerKwlistid; 
}
