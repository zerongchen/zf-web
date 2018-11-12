package com.aotain.zongfen.model.device;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.aotain.zongfen.utils.PageResult;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
* @ClassName: DpiStatic 
* @Description: DPI 设备状态查询回应-设备静态信息(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年3月2日 下午3:59:24 
*
 */
@Data
@NoArgsConstructor
public class DpiStatic implements Serializable {
	
    private String deploysitename;//部署站点名

    private Integer softwareversion;//DPI 软件版本号

    private Integer probeType;//DPI采集类型

    private String idcHouseid;//部署机房

    private Integer deployMode;//部署模式

    private Integer totalCapability;//DPI设备总分析能力( 以Gbps为单位)，单向

    private Integer slotnum;//DPI设备总槽位数

    private Integer preprocslotnum;//DPI设备预处理模块占用槽位数

    private Integer analysisslotnum;//DPI设备分析模块占用槽位数

    private Integer gpslotnum;//DPI通用(General Purpose)模块占用槽位数

    private Date modifyTime;//上报时间
    
    private Integer portNum;//端口总数

    private static final long serialVersionUID = 1L;
    
}