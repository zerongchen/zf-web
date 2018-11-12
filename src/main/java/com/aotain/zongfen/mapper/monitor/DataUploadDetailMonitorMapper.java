package com.aotain.zongfen.mapper.monitor;
import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;

@MyBatisDao
public interface DataUploadDetailMonitorMapper {
	List<DataUploadDetailMonitorDTO> selectList(DataUploadDetailMonitorDTO dto);
	List<DataUploadDetailMonitorDTO> selectWarnList(DataUploadDetailMonitorDTO dto);

}
