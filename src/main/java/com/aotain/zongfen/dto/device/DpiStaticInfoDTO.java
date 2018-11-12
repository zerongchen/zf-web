package com.aotain.zongfen.dto.device;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
* @ClassName: DpiStatusQueryDTO 
* @Description: 设备查询页面的静态信息(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年1月31日 上午10:37:29 
*
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DpiStaticInfoDTO {
	/**
	 *@Field probeType:  0x00：代表DPI设备 0x01：代表EU设备
	 */
	private Integer probeType;
	
	/**
	 *@Field deviceName: 设备名称
	 */
	private String deviceName;
	
	/**
	 *@Field depolySiteName: 部署站点名称
	 */
	private String depolySiteName;
	
	/**
	 *@Field softVersion: 软件版本
	 */
	private String softVersion;
	
	/**
	 * @Field depolyMode: =0x01:并行单向上行 
	 *        =0x02:并行单向下行 
	 *        =0x03:并行双向 
	 *        =0x04:串行单向上行 
	 *        =0x05:串行单向下行
	 *        =0x06:串行双向
	 */
	private Integer depolyMode;
	
	/**
	 *@Field totalCapability: DPI设备总分析能力(以Gbps为单位)，单向
	 */
	private Integer totalCapability;
	/**
	 * @Field soltNum: DPI设备总槽位数 
	 */
	private Integer soltNum;
	
	/**
	 * @Field preProcSlotNum: DPI设备预处理模块占用槽位数  
	 */
	private Integer preProcSlotNum;
	
	/**
	 * @Field analysisSlotNum: DPI设备分析模块占用槽位数  
	 */
	private Integer analysisSlotNum;
	
	/**
	 * @Field gpSlotNum: DPI通用(General Purpose)模块占用槽位数
	 */
	private Integer gpSlotNum;
	
	/**
	 * @Field portsTypeNum: 本DPI设备所配备的端口类型数 
	 */
	private Integer portsTypeNum;
	
	private List<DpiStaticPortDTO> portInfoList;
}
