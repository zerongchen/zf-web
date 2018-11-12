package com.aotain.zongfen.dto.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZongFenDevDTO {
	private Long zongfenId;

	private String zongfenIp;

	private Long zongfenPort;

	private String zongfenName;

	private String zongfenMac;

}
