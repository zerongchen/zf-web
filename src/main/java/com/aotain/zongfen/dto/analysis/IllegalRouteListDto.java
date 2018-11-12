package com.aotain.zongfen.dto.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IllegalRouteListDto {

	private Integer stattime;
	private Double inputflowD;
	private Double outputflowD;
	private String nodeip;
	private String cp;
	private String inputflows;
	private String outputflows;
	private Long inputflow;
	private Long outputflow;
	private String stateTime;
}
