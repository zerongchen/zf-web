package com.aotain.zongfen.service.analysis;

import com.aotain.zongfen.dto.analysis.AppTrafficDetailResult;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.mapper.analysis.AppTrafficMapper;
import com.aotain.zongfen.model.analysis.Params;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.sankey.Links;
import com.aotain.zongfen.model.sankey.Nodes;
import com.aotain.zongfen.model.sankey.SanKey;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import com.aotain.zongfen.utils.basicdata.TrafficDataConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

@Service
public class AppTrafficAnalysisServiceImpl implements AppTrafficAnalysisService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("appTrafficAnalysisMapper")
    private AppTrafficMapper appTrafficMapper;



    @Override
    public SanKey getAreaSankey( Params params ) {
        List<AppTrafficResult> appTrafficResults = getTrafficResult(params);
        List<Links> links =new ArrayList<>();
        Set<Nodes> nodes = new HashSet<>();
        if (appTrafficResults==null || appTrafficResults.isEmpty()) return null;
        appTrafficResults.forEach(new Consumer<AppTrafficResult>() {
            @Override
            public void accept( AppTrafficResult result ) {
                links.add(new Links(result.getSourceVal(),result.getSource(),result.getTargetVal(),result.getTarget(),result.getTotalFlow()));
                nodes.add(new Nodes(result.getTarget()));
                nodes.add(new Nodes(result.getSource()));
            }
        });
        return new SanKey(new ArrayList<>(nodes),links);
    }

    @Override
    public List<AppTrafficResult> getMainList( Params params ) {
        return getTrafficResult(params);
    }


    @Override
    public ECharts getEchartRankingData( Params params ) {
        setTimePeriod(params);
        params.setTable(TrafficDataConstant.getValueByTypeAndRefer(1,params.getDateType()));
        //省
        if(params.getAreaType()==1){
            //省内--省外区域
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartRankingAreaData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                if (("".equals(detailResult.getAppName()))) {
                    axis.add("unknow");
                } else {
                    axis.add(detailResult.getAppName());
                }
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"bar","1",seriesData1));
            series.add(new Series<>(legend.get(1),"bar","1",seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==4){
            //省内--省外区域
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartRankingOperateData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){

                axis.add(detailResult.getDstAreasub3());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"bar","1",seriesData1));
            series.add(new Series<>(legend.get(1),"bar","1",seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==30){
            //省->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartRankingIDCData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                if (("".equals(detailResult.getAppName()))) {
                    axis.add("unknow");
                }else {
                    axis.add(detailResult.getAppName());
                }
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"bar","1",seriesData1));
            series.add(new Series<>(legend.get(1),"bar","1",seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==31){
            //运营商->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartRankingIDCOperateData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                axis.add(detailResult.getDstAreasub3());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"bar","1",seriesData1));
            series.add(new Series<>(legend.get(1),"bar","1",seriesData2));
            return new ECharts(legend,axis,series,null);
        }
        //省内电信--省外运营商
        return null;
    }

    @Override
    public List<AppTrafficDetailResult> getTableRankingData( Params params ) {
        setTimePeriod(params);
        params.setTable(TrafficDataConstant.getValueByTypeAndRefer(1,params.getDateType()));
        //省内--省外区域
        if(params.getAreaType()==1) {
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            return appTrafficMapper.getTableRankingAreaData(params);
        }else if(params.getAreaType()==4){
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            return appTrafficMapper.getTableRankingOperateData(params);
        }else if(params.getAreaType()==30){
            //省->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            return appTrafficMapper.getTableRankingIDCData(params);
        }else if(params.getAreaType()==31){
            //运营商->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            return appTrafficMapper.getTableRankingIDCOperateData(params);
        }
        return null;
    }

    @Override
    public ECharts getEchartTrendData( Params params ) {
        setTimePeriod(params);
        params.setTable(TrafficDataConstant.getValueByTypeAndRefer(1,params.getDateType()));
        if(params.getAreaType()==1){
            //省内--省外区域
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartTrendAreaData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                axis.add(detailResult.getStateTime());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"line",8,false,seriesData1));
            series.add(new Series<>(legend.get(1),"line",1,1,8,false,seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==4){
            //省内--省外区域
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartTrendOperateData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                axis.add(detailResult.getStateTime());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"line",8,false,seriesData1));
            series.add(new Series<>(legend.get(1),"line",1,1,8,false,seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==30){
            //省->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartTrendIDCData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                axis.add(detailResult.getStateTime());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"line",8,false,seriesData1));
            series.add(new Series<>(legend.get(1),"line",1,1,8,false,seriesData2));
            return new ECharts(legend,axis,series,null);
        }else if(params.getAreaType()==31){
            //运营商->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            List<AppTrafficDetailResult> list = appTrafficMapper.getEchartTrendIDCOperateData(params);
            List<String> legend = new ArrayList<>();
            List<String> axis = new ArrayList<>();
            List<Double> seriesData1 = new ArrayList<Double>();
            List<Double> seriesData2 = new ArrayList<Double>();
            List<Series<Double>> series = new ArrayList<Series<Double>>();
            legend.add("上行流量");
            legend.add("下行流量");
            for (AppTrafficDetailResult detailResult:list){
                axis.add(detailResult.getStateTime());
                seriesData1.add(detailResult.getFlowUp());
                seriesData2.add(detailResult.getFlowDn());
            }
            series.add(new Series<>(legend.get(0),"line",8,false,seriesData1));
            series.add(new Series<>(legend.get(1),"line",1,1,8,false,seriesData2));
            return new ECharts(legend,axis,series,null);
        }

        return null;
    }

    @Override
    public List<AppTrafficDetailResult> getTableTrendData( Params params ) {
        setTimePeriod(params);
        params.setTable(TrafficDataConstant.getValueByTypeAndRefer(1,params.getDateType()));
        //省内--省外区域
        if(params.getAreaType()==1) {
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            return appTrafficMapper.getTableTrendAreaData(params);
        }else if(params.getAreaType()==4){
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            return appTrafficMapper.getTableTrendOperateData(params);
        }else if(params.getAreaType()==30){
            //省->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            return appTrafficMapper.getTableTrendIDCData(params);
        }else if(params.getAreaType()==31){
            //运营商->IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            return appTrafficMapper.getTableTrendIDCOperateData(params);
        }
        return null;
    }

    private List<AppTrafficResult> getTrafficResult( Params params){
        initParamsForArea(params);
        if(resetStateTime(params)){
            if (params.getPageType()==1){
                return appTrafficMapper.getLinks(params);
            }else {
                return appTrafficMapper.getIDCLinks(params);
            }
        }
        return null;
    }

    //格式化日期
    private boolean resetStateTime(Params params) {
        if(StringUtils.isEmpty(params.getStateTime())) return true;

        String stateFormula = null;
        try {
            switch (params.getDateType()){
                case 1: //H
                    Calendar cal1 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(), DateFormatConstant.DATETIME_WITHOUT_HOUR));
                    stateFormula = DateUtils.formatDateyyyyMMddHH(cal1.getTime());
                    break;
                case 2:
                    Calendar cal2 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                    stateFormula = DateUtils.formatDateyyyyMMdd(cal2.getTime());
                    break;
                case 3:
                    Calendar cal3 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                    int dayWeek = cal3.get(Calendar.DAY_OF_WEEK);
                    if(1 == dayWeek) {
                        cal3.add(Calendar.DAY_OF_MONTH, -1);
                    }
                    cal3.setFirstDayOfWeek(Calendar.MONDAY);
                    int day = cal3.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                    cal3.add(Calendar.DATE, cal3.getFirstDayOfWeek()-day);
                    stateFormula = DateUtils.formatDateyyyyMMdd(cal3.getTime());
                    break;
                case 4:
                    Date date = DateUtils.parse(DateFormatConstant.DATE_CHS_MONTH,params.getStateTime());
                    Calendar cal = DateUtils.toCalendar(date);
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    stateFormula = DateUtils.formatDateyyyyMM(cal.getTime());
                    break;
            }
            params.setStateTime(stateFormula);
        }catch (Exception e){
            logger.error("resetStateTime error"+e);
            return false;
        }
        return true;
    }

    private boolean setTimePeriod(Params params) {

        try {
            if(StringUtils.isEmpty(params.getStartTime())){
                Calendar cur = Calendar.getInstance();
                Calendar start = Calendar.getInstance();
                switch (params.getDateType()){
                    case 1://H
                        start.set(Calendar.HOUR,start.get(Calendar.HOUR)-24);
                        params.setStartTime(DateUtils.formatDateyyyyMMddHH(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMddHH(cur.getTime()));
                        break;
                    case 2://D
                        start.set(Calendar.DATE,start.get(Calendar.DATE)-30);
                        params.setStartTime(DateUtils.formatDateyyyyMMdd(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMdd(cur.getTime()));
                        break;
                    case 3://W
                        start.set(Calendar.WEEK_OF_YEAR,start.get(Calendar.WEEK_OF_YEAR)-10);
                        int dayWeek = start.get(Calendar.DAY_OF_WEEK);
                        if(1 == dayWeek) {
                            start.add(Calendar.DAY_OF_MONTH, -1);
                        }
                        start.setFirstDayOfWeek(Calendar.MONDAY);
                        int day = start.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                        start.add(Calendar.DATE, start.getFirstDayOfWeek()-day);
                        params.setStartTime(DateUtils.formatDateyyyyMMdd(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMdd(cur.getTime()));
                        break;
                    case 4://M
                        start.set(Calendar.MONTH,start.get(Calendar.MONTH)-10);
                        params.setStartTime(DateUtils.formatDateyyyyMM(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMM(cur.getTime()));
                        break;
                }
            }else {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                switch (params.getDateType()){
                    case 1://H
                        start.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATETIME_WITHOUT_HOUR));
                        if (params.getType()==1){//趋势图有结束时间，统计图没有
                            end.setTime(DateUtils.parseDate(params.getEndTime(), DateFormatConstant.DATETIME_WITHOUT_HOUR));
                        }else {
                            end.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATETIME_WITHOUT_HOUR));
                        }
                        params.setStartTime(DateUtils.formatDateyyyyMMddHH(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMddHH(end.getTime()));
                        break;
                    case 2://D
//                        start.set(Calendar.DATE,start.get(Calendar.DATE)-30);
                        start.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        if (params.getType()==1){//趋势图有结束时间，统计图没有
                            end.setTime(DateUtils.parseDate(params.getEndTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        }else {
                            end.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        }
                        params.setStartTime(DateUtils.formatDateyyyyMMdd(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMdd(end.getTime()));
                        break;
                    case 3://W
//                        start.set(Calendar.WEEK_OF_YEAR,start.get(Calendar.WEEK_OF_YEAR)-10);
                        start.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        if (params.getType()==1){//趋势图有结束时间，统计图没有
                            end.setTime(DateUtils.parseDate(params.getEndTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        }else {
                            end.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_HYPHEN));
                        }

                        int dayWeek = start.get(Calendar.DAY_OF_WEEK);
                        if(1 == dayWeek) {
                            start.add(Calendar.DAY_OF_MONTH, -1);
                        }
                        start.setFirstDayOfWeek(Calendar.MONDAY);
                        int day = start.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                        start.add(Calendar.DATE, start.getFirstDayOfWeek()-day);

                        params.setStartTime(DateUtils.formatDateyyyyMMdd(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMMdd(end.getTime()));
                        break;
                    case 4://M
                        start.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_MONTH));
                        if (params.getType()==1){//趋势图有结束时间，统计图没有
                            end.setTime(DateUtils.parseDate(params.getEndTime(), DateFormatConstant.DATE_CHS_MONTH));
                        }else {
                            end.setTime(DateUtils.parseDate(params.getStartTime(), DateFormatConstant.DATE_CHS_MONTH));
                        }
                        params.setStartTime(DateUtils.formatDateyyyyMM(start.getTime()));
                        params.setEndTime(DateUtils.formatDateyyyyMM(end.getTime()));
                        break;
                }
            }
        }catch (Exception e){
            logger.error("set time period error "+e);
            return false;
        }
        return true;
    }

    //主页
    private void initParamsForArea( Params params){

        //选择表
        params.setTable(TrafficDataConstant.getValueByTypeAndRefer(1,params.getDateType()));
        params.setCurrentAreaCode(SpringUtil.getSysUserAreaId());
        if(StringUtils.isEmpty(params.getStateTime())){
            params.setStateTime(null);
        }
        if(params.getPageType()==1){
            //左图 电信网内省 -> 电信网内省
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());

            //右图 电信网内省 -> 互联互通运营商
            params.setSrcUbasDataTypeRight(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeRight(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setDstUbasDataTypeRight(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
        }else if(params.getPageType()==2){
            //左图 内省 -> IDC
            params.setSrcUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setSrcAreaTypeLeft(TrafficDataConstant.INNER_NET_AREA.getValue());
            params.setDstUbasDataTypeLeft(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeLeft(TrafficDataConstant.INNER_NET_IDC.getValue());
            //右图 运营商 -> IDC
            params.setSrcUbasDataTypeRight(TrafficDataConstant.INTERWORKING_NET_DATASOURCE.getValue());
            params.setDstUbasDataTypeRight(TrafficDataConstant.INNER_NET_DATASOURCE.getValue());
            params.setDstAreaTypeRight(TrafficDataConstant.INNER_NET_IDC.getValue());
        }
    }
}
