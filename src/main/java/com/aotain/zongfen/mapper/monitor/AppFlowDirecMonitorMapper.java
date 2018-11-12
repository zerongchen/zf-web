package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.AppFlowDirecMonitorDTO;

@MyBatisDao
public interface AppFlowDirecMonitorMapper {
	/**
	 * 可以根据各种条件查询
	 * @param dto
	 * @return
	 */
	List<AppFlowDirecMonitorDTO> selectList(AppFlowDirecMonitorDTO dto);
	
	
	/**
	 * 查询最大记录数
	 * @param dto
	 * @return
	 */
	Long selectMaxSize(AppFlowDirecMonitorDTO dto);
}
