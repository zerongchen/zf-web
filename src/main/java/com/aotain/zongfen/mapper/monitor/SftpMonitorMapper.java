package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.dto.monitor.UdOneDataDTO;

@MyBatisDao
public interface SftpMonitorMapper {
	/**
	 * 可以根据各种条件查询
	 * @param dto
	 * @return
	 */
	List<SftpMonitorDTO> selectList(SftpMonitorDTO dto);

	List<SftpMonitorDTO> selectChartList(SftpMonitorDTO dto);

	/**
	 * 获取下拉列表
	 * @return
	 */
	List<SftpMonitorDTO> selectFileType();
}
