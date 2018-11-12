package com.aotain.zongfen.service.indexpage;

import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.model.useranalysis.AppTraffic;

import java.util.List;
import java.util.Map;

public interface IndexPageService {
    List<AppTrafficResult> getIdcTraffic(Map<String, String> paramMap);

    List<AppTrafficResult> getDpiTraffic(Map<String, String> paramMap);

    List<AppTraffic> getDpiAppflow(Map<String, String> paramMap);

    List<AppTraffic> getAppIdAppflow(Map<String, String> paramMap);
}
