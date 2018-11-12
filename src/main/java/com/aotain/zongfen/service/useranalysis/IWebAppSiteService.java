package com.aotain.zongfen.service.useranalysis;

import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.WebFlowQueryParam;
import com.aotain.zongfen.model.useranalysis.WebFlowUbas;
import com.github.pagehelper.Page;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/06/13
 */
public interface IWebAppSiteService {

    /**
     * 查询分页数据（table1数据）
     * @param webFlowQueryParam
     * @return
     */
    Page<WebFlowUbas> listData(WebFlowQueryParam webFlowQueryParam) ;

    /**
     * 查询分页数据（table2数据）
     * @param webFlowQueryParam
     * @return
     * @throws SQLException
     */
    Page<WebFlowUbas> listWebSiteData(WebFlowQueryParam webFlowQueryParam) ;

    /**
     * 获取echart表格数据(不分页只取前10条)
     * @param webFlowQueryParam
     * @return
     */
    List<WebFlowUbas> getChartData(WebFlowQueryParam webFlowQueryParam) ;

    /**
     * 获取echart趋势图表格数据(不分页不限制条数)
     * @param webFlowQueryParam
     * @return
     */
    List<WebFlowUbas> getLineData(WebFlowQueryParam webFlowQueryParam) ;

    /**
     * 获取第一个页面导出数据
     * @param webFlowQueryParam
     * @return
     */
    List<WebFlowUbas> getExportData(WebFlowQueryParam webFlowQueryParam);

    /**
     * 生成echart图表格式数据
     * @return
     */
    ECharts<Series> wrapChartData(List<WebFlowUbas> webFlowUbasList);

    /**
     * 生成echart趋势图格式数据
     * @return
     */
    ECharts<Series> wrapLineData(WebFlowQueryParam appTrafficQueryParam,List<WebFlowUbas> webFlowUbasList);

    /**
     * 根据siteTypeName获取siteType
     * @param queryMap
     * @return
     */
    int getSiteTypeByName(Map<String,Object> queryMap);

    boolean createExportTask(Map<String, Object> params);
}
