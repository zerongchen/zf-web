package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.AppFlowMonitorDTO;

@MyBatisDao
public interface AppFlowMonitorMapper {
	/**
	 * 可以根据各种条件查询
	 * @param dto
	 * @return
	 */
	List<AppFlowMonitorDTO> selectList(AppFlowMonitorDTO dto);
	
	/**
	 * 查询最大记录数
	 * @param dto
	 * @return
	 */
	Long selectMaxSize(AppFlowMonitorDTO dto);
}
