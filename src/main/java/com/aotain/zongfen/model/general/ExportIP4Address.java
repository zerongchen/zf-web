package com.aotain.zongfen.model.general;

import java.io.Serializable;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ExpSheet(name="地址-IPv4")
public class ExportIP4Address implements Serializable {
    /** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	private static final long serialVersionUID = -1885231131801081307L;
    //省份名称
    @Export(title="省份", id=1)
    private String provinceName;
    
    @Export(title="区域名称", id=2)
    private String areaName;
    
    @Export(title="区域ID", id=3)
    private String areaId;//2进制字符串
    
    @Export(title="起始地址", id=4)
    private String startIp;

    @Export(title="终止地址", id=5)
    private String endIp;
    
    private String ipType;
    
}