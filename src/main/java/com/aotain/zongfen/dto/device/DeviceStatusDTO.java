package com.aotain.zongfen.dto.device;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * ClassName: DeviceStatusDTO
 * Description:设备详情页面的设备运行状态
 * date: 2017年12月19日 下午3:44:11
 * 
 * @version  
 * @since JDK 1.8
 */

public class DeviceStatusDTO implements Serializable {
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/  
	private static final long serialVersionUID = 1L;

	private String policyName;
	
    private Integer operateType;
    
    private Integer status;
    
    private Date createTime;

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
    
    
    
}
