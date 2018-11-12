package com.aotain.zongfen.log.constant;

import lombok.Getter;
import lombok.Setter;

public enum DataType {
		UPLOAD(0,"流量上报数据"),
		POLICY(1,"策略数据"),
		FILE(2,"文件数据"),
		OTHER(3,"其他");
		
		@Getter
		@Setter
		private Integer type;
		
		@Getter
		@Setter
		private String description;
		
		DataType(Integer type, String description){
			this.type=type;
			this.description=description;
		}
		
}
