package com.aotain.zongfen.mapper.monitor;
import java.util.List;

import com.aotain.zongfen.dto.monitor.DataUploadMonitorDTO;
import com.aotain.common.config.annotation.MyBatisDao;

@MyBatisDao
public interface DataUploadMonitorMapper {
	List<DataUploadMonitorDTO>  selectList(DataUploadMonitorDTO dto);
	Double selectMaxSize(DataUploadMonitorDTO dto);
	Integer selectAbnormalNum(DataUploadMonitorDTO dto);

}

