package com.aotain.zongfen.model.echarts;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkLine {
	private List<Data> data = new ArrayList<Data>();
	
	public MarkLine(List<Data> data) {
		this.data = data;
	}
}
