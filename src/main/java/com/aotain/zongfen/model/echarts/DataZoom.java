package com.aotain.zongfen.model.echarts;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataZoom {
	private Boolean show;
	private Integer start;
	private Integer end;
	private String type;
	private Boolean zoomLock;

}
