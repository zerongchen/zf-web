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
public class GeneralApp implements Serializable {

	@Export(title="App ID", id=3)
    private String appIds;

	@Export(title="App 中文名称", id=4)
    private String appZHName;
    
    private String appName;
    
    @Export(title="App 英文名称", id=5)
    private String appENName;

	@Export(title="类型ID", id=1)
    private String appTypes;

	@Export(title="App 类型", id=2)
    private String appTypeName;

	@Export(title="是否主流应用", id=7)
	private String mainApp;
	
    private int isMainApp;
    
	@Export(title="备注", id=6)
    private String remark;
    
    private Integer appType;
    
    private Integer appId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public int hashCode() {
    	return (this.appIds + this.appTypes).hashCode();  
    }
    
    public boolean equals(Object obj) {
    	if(obj instanceof GeneralApp) {
    		GeneralApp url = (GeneralApp) obj;
    		return this.appTypes.equals(url.getAppTypes()) && this.appIds.equals(url.getAppIds());
    	} else {
    		return false;
    	}
    }
}