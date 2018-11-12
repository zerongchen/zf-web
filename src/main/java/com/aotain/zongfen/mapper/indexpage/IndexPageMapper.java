package com.aotain.zongfen.mapper.indexpage;


import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.model.useranalysis.AppTraffic;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface IndexPageMapper {

    List<AppTrafficResult> getIdcTraffic(Map<String, String> paramMap);

    List<AppTrafficResult> getDpiTraffic(Map<String, String> paramMap);

    List<AppTraffic> getDpiAppflow(Map<String, String> paramMap);

    List<AppTraffic> getAppIdAppflow(Map<String, String> paramMap);
}
