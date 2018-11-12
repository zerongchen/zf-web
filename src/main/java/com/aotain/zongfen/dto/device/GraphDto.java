package com.aotain.zongfen.dto.device;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * ClassName: GraphDto
 * Description: 设备网络拓扑图
 * date: 2018年3月21日 下午2:21:41
 * 
 * @author tanzj 
 * @version  
 * @since JDK 1.8
 */
@Data
@NoArgsConstructor
public class GraphDto {
	
	private Integer id;
	
	/**
	 * 父节点id
	 */
	private Integer parentId;

	private String deviceName;
	
	/**
	 * 0-展示  1-不展示
	 */
	private int deviceFlag;
	
	/**
	 * 0-连接成功  1-连接失败
	 */
	private int connectFlag;
	
	/**
	 * 0-不存在  1-存在
	 */
	private int existVirtual;
	
	private String ip;
	
	private List<GraphDto> children;
	
}
