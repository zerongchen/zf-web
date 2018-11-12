package com.aotain.zongfen.model.device;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZongFenRel implements Serializable {
    /** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/  
	private static final long serialVersionUID = 1L;

	private Integer zongfenId;

    private String ip;

}