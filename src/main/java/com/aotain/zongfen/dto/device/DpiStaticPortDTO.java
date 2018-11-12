package com.aotain.zongfen.dto.device;

import lombok.Data;

/**
 * 
* @ClassName: DpiStaticPortDTO 
* @Description: 静态端口信息(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年1月31日 上午11:17:04 
*
 */
@Data
public class DpiStaticPortDTO {
	
	/**
	 * @Field portNo:  本端口的编号，在本DPI设备内唯一 
	 */
	private Integer portNo;
	/**
	 * @Field portDesc:  本端口描述信息 
	 */
	private String portDesc;
	/**
	 * @Field mLinkId:  本端口监控链路编号 ,对并接设备，一个端口对应一条链路； 对串接设备，两个端口对应一条链路
	 */
	private Integer mLinkId;
	/**
	 * @Field mLinkDesc:  监控链路描述信息
	 */
	private String mLinkDesc;
}
