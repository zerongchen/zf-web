package com.aotain.zongfen.dto.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WlanDto {

	private int stattime;
	private String useraccount;
	private int usertype;
	private long usagecount;
	private String areaid;
	private int devicecnt;
	private String phoneNumber;
	private String devType;
//	private String[] devicelist;
}
