package com.aotain.zongfen.service.useranalysis;

import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.DDoSAttackArea;
import com.aotain.zongfen.model.useranalysis.DDoSQueryParam;
import com.aotain.zongfen.model.useranalysis.DDoSUbasDetail;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * DDoS异常攻击服务类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/21
 */
public interface IDDoSAttackService{
    /**
     * 根据查询时间确定统计时间段的开始结束时间点
     * @param dDoSQueryParam
     * @return
     */
    void setStatPeriodPoint(DDoSQueryParam dDoSQueryParam);

    /**
     * 根据条件获取分页数据
     * @param dDoSQueryParam
     * @return
     */
    Page<DDoSUbasDetail> listData(DDoSQueryParam dDoSQueryParam);

    /**
     * 查询区域详情分页数据
     * @param dDoSQueryParam
     * @return
     */
    Page<DDoSAttackArea> listAreaData(DDoSQueryParam dDoSQueryParam);

    /**
     * 根据条件获取导出的数据（不分页）
     * @param dDoSQueryParam
     * @return
     */
    List<DDoSUbasDetail> listExportData(DDoSQueryParam dDoSQueryParam);

    /**
     * 根据统计时间查询对应的图标数据（按天聚合）
     * @param dDoSQueryParam
     * @return
     */
    Map<String,Map<String,Double>> getChartData(DDoSQueryParam dDoSQueryParam);

    /**
     * 根据list数据封装对应的echart数据
     * @param ddosUbasMap
     * @return
     */
    ECharts<Series> wrapChartData(DDoSQueryParam dDoSQueryParam,Map<String,Map<String,Double>> ddosUbasMap);

    List<Map<String,String>> loadExportTask(int pageIndex,int pageSize,Map<String,String> params);

    boolean createExportTask(Map<String, Object> params);

    boolean deleteExportTask(Map<String, Object> params);

    void updateDownloadFile(Map<String, String> params);
}
