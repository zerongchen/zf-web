package com.aotain.zongfen.model.general;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClassInfo implements Serializable {

   /** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	private static final long serialVersionUID = 957650167049009471L;
    
    
    private Integer messageType;
    
    private Long messageNo;
    
    private Integer zongfenId;
    
    private String messageName;
    
    private Integer operateType;
    
}