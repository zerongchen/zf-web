package com.aotain.zongfen.mapper.monitor;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;
import com.aotain.zongfen.model.monitor.RadiusPcapDetail;

import java.util.List;

@MyBatisDao
public interface RadiusPcapDetailMapper {
    List<RadiusPcapDetail> selectListByPrimaryKey( RadiusParamDTO key );
}