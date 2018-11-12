package com.aotain.zongfen.mapper.monitor;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.UdOneDataDTO;

@MyBatisDao
public interface UdOneDataMapper {

	List<UdOneDataDTO> selectList(UdOneDataDTO dto);
	
	List<UdOneDataDTO> selectChartListByReceived(UdOneDataDTO dto);

	List<UdOneDataDTO> selectChartListBySave(UdOneDataDTO dto);
}
