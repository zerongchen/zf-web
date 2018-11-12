package com.aotain.zongfen.model.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)  
public class CommonTree {
	private String id;
	private String name;
	private String pid;
	private int level;//0=root节点，1=dpi/eu,2=areaid或机房id，3=厂家
	private Byte probeType;//设备类型:0=DPI,1=EU,-1=dpi+eu
	private Boolean isParent;
}
