package com.aotain.zongfen.mapper.monitor;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.RadiusFileDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;

import java.util.List;

@MyBatisDao
public interface RadiusFileDetailMapper {

    List<RadiusFileDetailDTO> getFileDetailList( RadiusParamDTO recode);

}