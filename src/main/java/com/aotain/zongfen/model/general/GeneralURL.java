package com.aotain.zongfen.model.general;

import java.io.Serializable;
import java.util.Date;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ExpSheet(name="WEB分类库")
public class GeneralURL implements Serializable {
    private Long urlId;

    @Export(title="web 域名", id=3)
    private String hostName;

    @Export(title="web 分类ID", id=1)
    private String webTypes;

    @Export(title="web 分类名称", id=2)
    private String webName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date updateTime;

    private Integer webType;
    
    private static final long serialVersionUID = 1L;
    
    public int hashCode() {
    	return (this.hostName + this.webTypes).hashCode();  
    }
    
    public boolean equals(Object obj) {
    	if(obj instanceof GeneralURL) {
    		GeneralURL url = (GeneralURL) obj;
    		return this.hostName.equals(url.getHostName()) && this.webTypes.equals(url.getWebTypes());
    	} else {
    		return false;
    	}
    }
}