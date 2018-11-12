package com.aotain.zongfen.model.apppolicy;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebFlowManageWebType implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer webflowId;
	
	private Integer webType;
	
	private String webTypeName;
}
