package com.aotain.zongfen.model.policy;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PolicyStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer messageType;

	private long messageNo;

	private String dpiIp;
	
	private Integer status;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	
	/**
	 * 主策略成功异常数
	 */
	private String mainCount;
	
	/**
	 * 绑定策略成功异常数
	 */
	private String bindCount;

}