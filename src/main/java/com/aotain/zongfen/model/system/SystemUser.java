package com.aotain.zongfen.model.system;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)  
public class SystemUser {
	private String username;
	private String password;
	private Long areaCode;//区域ID
}
