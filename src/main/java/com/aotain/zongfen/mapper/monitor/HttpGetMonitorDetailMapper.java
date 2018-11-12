package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.HttpGetMonitorDetailDTO;

@MyBatisDao
public interface HttpGetMonitorDetailMapper {
	/**
	 * 可以根据各种条件查询
	 * @param dto
	 * @return
	 */
	List<HttpGetMonitorDetailDTO> selectList(HttpGetMonitorDetailDTO dto);
}
