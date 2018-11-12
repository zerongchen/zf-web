package com.aotain.zongfen.model.echarts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Data {
	private String type;
	private String name;
	public Data(String type, String name) {
		this.type=type;
		this.name=name;
	}
	
}
