package com.aotain.zongfen.dto.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDto {

	private int stattime;
	private int apptypeid;
	private String apptype;
	private String appname;
	private int appid;
	private int usertype;
	private long usagecount;
	private String useraccount;
	private String areaid;
	private long count;
}
