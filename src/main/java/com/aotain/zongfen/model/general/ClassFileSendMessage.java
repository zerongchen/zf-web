package com.aotain.zongfen.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author DongBoye
 * 分类库的一些下发的用户名密码等信息
 */
@Data
@NoArgsConstructor
public class ClassFileSendMessage {
	private String ip;
	private Integer port;
	private String username;
	private String password;
	
}
