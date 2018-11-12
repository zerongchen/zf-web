package com.aotain.zongfen.service.useranalysis;

import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.AppTraffic;
import com.aotain.zongfen.model.useranalysis.AppTrafficChartBean;
import com.aotain.zongfen.model.useranalysis.AppTrafficQueryParam;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/23
 */
public interface IAppTrafficService {

    /**
     * 获取区域编码下拉选数据
     * @return
     */
    List<ChinaArea> getAreaList();

    /**
     * 查询数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTraffic> listData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定appType和appId的列表数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTraffic> listAppIdData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取echart图表数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getChartData(AppTrafficQueryParam appTrafficQueryParam);


    /**
     * 获取指定AppType获取echart图表数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType和AppId获取echart图表数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getLineDataByAppId(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 生成echart图表格式数据
     * @return
     */
    ECharts<Series> wrapChartData(int statType,List<AppTrafficChartBean> appTrafficChartBeans,boolean secondChart);

    /**
     * 生成echart图表格式数据
     * @return
     */
    ECharts<Series> wrapLineData(AppTrafficQueryParam appTrafficQueryParam,List<AppTrafficChartBean> appTrafficChartBeans);

    /**
     * 根据appIdName和AppType获取对应的appId
     * @param queryMap
     * @return
     */
    long getAppIdByName(Map<String,Object> queryMap);

    /**
     * 根据appIdName和AppType获取对应的appId
     * @param queryMap
     * @return
     */
    int getAppTypeByName(Map<String,Object> queryMap);
}
