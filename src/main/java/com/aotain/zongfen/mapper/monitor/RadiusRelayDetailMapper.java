package com.aotain.zongfen.mapper.monitor;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;
import com.aotain.zongfen.model.monitor.RadiusRelayDetail;
import com.aotain.zongfen.model.monitor.RadiusRelayDetailKey;

import java.util.List;

@MyBatisDao
public interface RadiusRelayDetailMapper {

    List<RadiusRelayDetail> selectListByPrimaryKey( RadiusParamDTO key);
}