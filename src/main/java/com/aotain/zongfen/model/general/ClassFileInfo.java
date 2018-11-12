package com.aotain.zongfen.model.general;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassFileInfo extends ClassFileInfoKey {
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	private static final long serialVersionUID = -6124101459121571631L;

	private String classFileName;
	
	private Long versionNo;
	
	private String createOper;
	
	private String modifyOper;
	
	private Date createTime;
	
	private Date modifyTime;
}
