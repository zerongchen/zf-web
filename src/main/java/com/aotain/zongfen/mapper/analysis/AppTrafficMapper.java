package com.aotain.zongfen.mapper.analysis;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.analysis.AppTrafficDetailResult;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.model.sankey.Nodes;
import com.aotain.zongfen.model.analysis.Params;

import java.util.List;

@MyBatisDao("appTrafficAnalysisMapper")
public interface AppTrafficMapper {


    /**
     * 城域网 桑基图
     * @param params
     * @return
     */
    List<AppTrafficResult> getLinks( Params params);

    /**
     * 城域网 省->省 排序图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartRankingAreaData( Params params);
    /**
     * 城域网 省->运营商 排序图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartRankingOperateData( Params params);
    /**
     * 城域网 省->省 排序 boostrap表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableRankingAreaData( Params params);
    /**
     * 城域网 省->运营商 排序 boostrap表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableRankingOperateData( Params params);
    /**
     * 城域网 省->省 趋势图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartTrendAreaData( Params params);
    /**
     * 城域网 省->运营商 趋势图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartTrendOperateData( Params params);
    /**
     * 城域网 省->省 趋势 boostrap表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableTrendAreaData( Params params);
    /**
     * 城域网 省->运营商 趋势 boostrap表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableTrendOperateData( Params params);

    /**
     * IDC 桑基图
     * @param params
     * @return
     */
    List<AppTrafficResult> getIDCLinks( Params params);

    /**
     * IDC 省->IDC 排序图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartRankingIDCData( Params params);
    /**
     * IDC 运营商->IDC 排序图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartRankingIDCOperateData( Params params);
    /**
     * IDC 省->IDC 排序 boostrap 表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableRankingIDCData( Params params);
    /**
     * IDC 运营商->IDC 排序 boostrap 表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableRankingIDCOperateData( Params params);
    /**
     * IDC 省->IDC 趋势图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartTrendIDCData( Params params);
    /**
     * IDC 运营商->IDC 趋势图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getEchartTrendIDCOperateData( Params params);

    /**
     * IDC 省->IDC 趋势图
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableTrendIDCData( Params params);
    /**
     * IDC 运营商->IDC 趋势boostrap表
     * @param params
     * @return
     */
    List<AppTrafficDetailResult> getTableTrendIDCOperateData( Params params);

}