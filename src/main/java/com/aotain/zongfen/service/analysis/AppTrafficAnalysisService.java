package com.aotain.zongfen.service.analysis;

import com.aotain.zongfen.dto.analysis.AppTrafficDetailResult;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.model.analysis.Params;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.sankey.SanKey;

import java.util.List;

public interface AppTrafficAnalysisService {

    SanKey getAreaSankey( Params params);
    List<AppTrafficResult> getMainList( Params params);

    ECharts getEchartRankingData( Params params);
    List<AppTrafficDetailResult> getTableRankingData( Params params);

    ECharts getEchartTrendData( Params params);
    List<AppTrafficDetailResult> getTableTrendData( Params params);
}
