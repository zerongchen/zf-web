package com.aotain.zongfen.mapper.useranalysis;

import com.aotain.common.config.annotation.MyBatisDao;
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
@MyBatisDao
public interface AppTrafficMapper {

    /**
     * 根据条件查询分页数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTraffic> listData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 根据指定appType和appId分页数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTraffic> listAppIdData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getFlowChartData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getUserChartData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getPacketChartData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getSessionChartData(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getFlowChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getUserChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getPacketChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType获取chart数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getSessionChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 获取指定AppType和AppId获取chartLine数据
     * @param appTrafficQueryParam
     * @return
     */
    List<AppTrafficChartBean> getLineDataByAppId(AppTrafficQueryParam appTrafficQueryParam);

    /**
     * 根据appIdName和AppType获取对应的appId
     * @param queryMap
     * @return
     */
    long getAppIdByName(Map<String,Object> queryMap);

    /**
     * 根据AppTypeName获取appType
     * @param queryMap
     * @return
     */
    int getAppTypeByName(Map<String,Object> queryMap);

}
