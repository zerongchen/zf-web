package com.aotain.zongfen.service.useranalysis;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.constant.RedisKeyConstant;
import com.aotain.zongfen.mapper.common.ChinaAreaMapper;
import com.aotain.zongfen.mapper.useranalysis.AppTrafficMapper;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.AppTraffic;
import com.aotain.zongfen.model.useranalysis.AppTrafficChartBean;
import com.aotain.zongfen.model.useranalysis.AppTrafficQueryParam;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.UnitUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/23
 */
@Service
public class AppTrafficServiceImpl implements IAppTrafficService{

    private static Integer oneHourInMs = 60*60*1000;

    @Autowired
    private ChinaAreaMapper chinaAreaMapper;

    @Autowired
    private AppTrafficMapper appTrafficMapper;

    @Override
    public List<ChinaArea> getAreaList(){
        //获取部署位置
        String deployMode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.SYSTEM_DEPLOY_MODE);
        String parentCode = LocalConfig.getInstance().getHashValueByHashKey(RedisKeyConstant.PROVINCE_AREA);
        if(parentCode==null || "".equals(parentCode)) {
            return null;
        }else {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put("type", deployMode);
            query.put("parentCode", parentCode);
            return chinaAreaMapper.getAreaList(query);
        }
    }

    /**
     * 根据前端条件确定查询表和查询条件
     * @param appTrafficQueryParam
     */
    private void generateQueryCondition(AppTrafficQueryParam appTrafficQueryParam){
        long statTime = getStatTime(appTrafficQueryParam);
        appTrafficQueryParam.setStatTime(statTime);
        // 根据不同粒度选择查询不同的表
        if (appTrafficQueryParam.getDateType()==1){
            appTrafficQueryParam.setTableName("zf_v2_ubas_appflow_h");
        } else if (appTrafficQueryParam.getDateType()==2){
            appTrafficQueryParam.setTableName("zf_v2_ubas_appflow_d");
        } else if (appTrafficQueryParam.getDateType()==3){
            appTrafficQueryParam.setTableName("zf_v2_ubas_appflow_w");
        } else if (appTrafficQueryParam.getDateType()==4){
            appTrafficQueryParam.setTableName("zf_v2_ubas_appflow_m");
        }
    }

    /**
     * 根据前端条件确定折线图查询条件(查询时间点及查询的表)
     * @param appTrafficQueryParam
     */
    private void generateLineQueryCondition(AppTrafficQueryParam appTrafficQueryParam){
        List<Long> timeScope = getQueryTimeScope(appTrafficQueryParam);
        long startTime = 0L,endTime = 0L;

        if (appTrafficQueryParam.getDateType()==1){
            startTime = Long.valueOf(DateUtils.formatDateyyyyMMddHH(new Date(timeScope.get(0)*1000)));
            endTime = Long.valueOf(DateUtils.formatDateyyyyMMddHH(new Date(timeScope.get(timeScope.size()-1)*1000)));
        } else if (appTrafficQueryParam.getDateType()==2||appTrafficQueryParam.getDateType()==3){
            startTime = Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date(timeScope.get(0)*1000)));
            endTime = Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date(timeScope.get(timeScope.size()-1)*1000)));
        } else {
            startTime = Long.valueOf(DateUtils.formatDateyyyyMM(new Date(timeScope.get(0)*1000)));
            endTime = Long.valueOf(DateUtils.formatDateyyyyMM(new Date(timeScope.get(timeScope.size()-1)*1000)));
        }

        appTrafficQueryParam.setStartTime(startTime);
        appTrafficQueryParam.setEndTime(endTime);
        generateQueryCondition(appTrafficQueryParam);
    }

    /**
     * 根据前端条件确定折线图时间点 （精确到s）
     * @param appTrafficQueryParam
     */
    private List<Long> getQueryTimeScope(AppTrafficQueryParam appTrafficQueryParam){
        List<Long> timeScope = Lists.newArrayList();
        // 查询过去24小时
        if (appTrafficQueryParam.getDateType()==1){
            timeScope = DateUtils.getLastDayHourInTimeStamp();
            //过去30天
        } else if (appTrafficQueryParam.getDateType()==2){
            timeScope = DateUtils.getLastMonthInTimeStamp();
            //过去10周
        } else if (appTrafficQueryParam.getDateType()==3){
            timeScope = DateUtils.getLastTenWeekInTimeStamp();
            //过去10月
        } else if (appTrafficQueryParam.getDateType()==4){
            timeScope = DateUtils.getLastTenMonthInTimeStamp();
        }
        return timeScope;
    }

    @Override
    public List<AppTraffic> listData(AppTrafficQueryParam appTrafficQueryParam){
        generateQueryCondition(appTrafficQueryParam);
        List<AppTraffic> list = appTrafficMapper.listData(appTrafficQueryParam);
        Long maxValue = 0l;
        for(AppTraffic m:list){
          if(m.getAppTrafficSum()>=maxValue){
              maxValue=m.getAppTrafficSum();
          }
        }
        String unit = UnitUtils.getUnit(maxValue*1.0);
        for(AppTraffic m:list){
            Long upflow = m.getAppTrafficUp();
            String upflowStr = UnitUtils.transferUnit(upflow*1.0,unit)+unit;
            m.setAppTrafficUpStr(upflowStr);

            Long downflow = m.getAppTrafficDn();
            String downflowStr = UnitUtils.transferUnit(downflow*1.0,unit)+unit;
            m.setAppTrafficDnStr(downflowStr);

            Long sumflow = m.getAppTrafficSum();
            String sumflowStr = UnitUtils.transferUnit(sumflow*1.0,unit)+unit;
            m.setAppTrafficSumStr(sumflowStr);
        }

        return list;
    }

    @Override
    public List<AppTraffic> listAppIdData(AppTrafficQueryParam appTrafficQueryParam){
        generateLineQueryCondition(appTrafficQueryParam);
      //  return appTrafficMapper.listAppIdData(appTrafficQueryParam);
        List<AppTraffic> list = appTrafficMapper.listAppIdData(appTrafficQueryParam);
        Long maxValue = 0l;
        for(AppTraffic m:list){
            if(m.getAppTrafficSum()>=maxValue){
                maxValue=m.getAppTrafficSum();
            }
        }
        String unit = UnitUtils.getUnit(maxValue*1.0);
        for(AppTraffic m:list){
            Long upflow = m.getAppTrafficUp();
            String upflowStr = UnitUtils.transferUnit(upflow*1.0,unit)+unit;
            m.setAppTrafficUpStr(upflowStr);

            Long downflow = m.getAppTrafficDn();
            String downflowStr = UnitUtils.transferUnit(downflow*1.0,unit)+unit;
            m.setAppTrafficDnStr(downflowStr);

            Long sumflow = m.getAppTrafficSum();
            String sumflowStr = UnitUtils.transferUnit(sumflow*1.0,unit)+unit;
            m.setAppTrafficSumStr(sumflowStr);
        }
        return list;
    }

    @Override
    public List<AppTrafficChartBean> getChartData(AppTrafficQueryParam appTrafficQueryParam){
        generateQueryCondition(appTrafficQueryParam);
        List<AppTrafficChartBean> result = Lists.newArrayList();
        if (appTrafficQueryParam.getStatType()==1){
            result = appTrafficMapper.getFlowChartData(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==2){
            result = appTrafficMapper.getUserChartData(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==3){
            result = appTrafficMapper.getPacketChartData(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==4){
            result = appTrafficMapper.getSessionChartData(appTrafficQueryParam);
        }
        return result;

    }

    @Override
    public List<AppTrafficChartBean> getChartDataByAppType(AppTrafficQueryParam appTrafficQueryParam){
        generateQueryCondition(appTrafficQueryParam);
        List<AppTrafficChartBean> result = Lists.newArrayList();
        if (appTrafficQueryParam.getStatType()==1){
            result = appTrafficMapper.getFlowChartDataByAppType(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==2){
            result = appTrafficMapper.getUserChartDataByAppType(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==3){
            result = appTrafficMapper.getPacketChartDataByAppType(appTrafficQueryParam);
        } else if (appTrafficQueryParam.getStatType()==4){
            result = appTrafficMapper.getSessionChartDataByAppType(appTrafficQueryParam);
        }
        return result;

    }

    @Override
    public List<AppTrafficChartBean> getLineDataByAppId(AppTrafficQueryParam appTrafficQueryParam){
        generateLineQueryCondition(appTrafficQueryParam);
        List<AppTrafficChartBean> result = Lists.newArrayList();
        result = appTrafficMapper.getLineDataByAppId(appTrafficQueryParam);
        return result;
    }

    @Override
    public ECharts<Series> wrapChartData(int statType,List<AppTrafficChartBean> appTrafficChartBeans,boolean secondChart){
        ECharts<Series> eCharts = new ECharts<>();
        List<String> axis = Lists.newArrayList();
        List<Double> appTrafficUpListData = Lists.newArrayList();
        List<Double> appTrafficDnListData = Lists.newArrayList();
        List<Long> appUserNumListData = Lists.newArrayList();
        List<Long> appPacketsNumListData = Lists.newArrayList();
        List<Long> appSessionsNumListData = Lists.newArrayList();
        List<Long> appNewSessionNumListData = Lists.newArrayList();

        Long maxValue = 0l;
        if(statType==1){
            for(AppTrafficChartBean m:appTrafficChartBeans){
                if(m.getAppTrafficDn()>=maxValue){
                    maxValue=m.getAppTrafficDn();
                }
                if(m.getAppTrafficUp()>=maxValue){
                    maxValue=m.getAppTrafficUp();
                }
            }
        }
        String unit = UnitUtils.getUnit(maxValue*1.0);

        for (int i =0;i<appTrafficChartBeans.size();i++){
            AppTrafficChartBean appTrafficChartBean = appTrafficChartBeans.get(i);
            if ( !secondChart){
                axis.add(appTrafficChartBean.getAppNameOfType());
            } else {
                axis.add(appTrafficChartBean.getAppNameOfId().toString());
            }
            if(statType==1){
                Double upFlow = UnitUtils.transferUnit(appTrafficChartBean.getAppTrafficUp()*1.0,unit);
                Double downFlow = UnitUtils.transferUnit(appTrafficChartBean.getAppTrafficDn()*1.0,unit);
                appTrafficUpListData.add(upFlow);
                appTrafficDnListData.add(downFlow);
            }
            appUserNumListData.add(appTrafficChartBean.getAppUserNum());
            appPacketsNumListData.add(appTrafficChartBean.getAppPacketsNum());
            appSessionsNumListData.add(appTrafficChartBean.getAppSessionsNum());
            appNewSessionNumListData.add(appTrafficChartBean.getAppNewSessionNum());
        }
        List<Series> series = Lists.newArrayList();

        if (statType==1){
            Series appTrafficUpSeries = new Series(appTrafficUpListData);
            series.add(appTrafficUpSeries);

            Series appTrafficDnSeries = new Series(appTrafficDnListData);
            series.add(appTrafficDnSeries);
        } else if (statType==2){
            Series appUserNumSeries = new Series(appUserNumListData);
            series.add(appUserNumSeries);
        } else if (statType==3){
            Series appPacketsNumSeries = new Series(appPacketsNumListData);
            series.add(appPacketsNumSeries);
        } else if (statType==4){
            Series appSessionsNumSeries = new Series(appSessionsNumListData);
            series.add(appSessionsNumSeries);

            Series appNewSessionNumSeries = new Series(appNewSessionNumListData);
            series.add(appNewSessionNumSeries);
        }

        eCharts.setAxis(axis);
        eCharts.setSeries(series);
        eCharts.setUnit(unit);
        return eCharts;
    }

    @Override
    public ECharts<Series> wrapLineData(AppTrafficQueryParam appTrafficQueryParam,List<AppTrafficChartBean> appTrafficChartBeans){
        ECharts<Series> eCharts = new ECharts<>();
        List<String> axis = Lists.newArrayList();
        List<Series> series = Lists.newArrayList();
        List<Double> appTrafficUpListData = Lists.newArrayList();
        List<Double> appTrafficDnListData = Lists.newArrayList();
        List<Long> appUserNumListData = Lists.newArrayList();
        List<Long> appPacketsNumListData = Lists.newArrayList();
        List<Long> appSessionsNumListData = Lists.newArrayList();
        List<Long> appNewSessionNumListData = Lists.newArrayList();

        Long maxValue = 0l;
        if(appTrafficQueryParam.getStatType()==1){
            for(AppTrafficChartBean m:appTrafficChartBeans){
                if(m.getAppTrafficDn()>=maxValue){
                    maxValue=m.getAppTrafficDn();
                }
                if(m.getAppTrafficUp()>=maxValue){
                    maxValue=m.getAppTrafficUp();
                }
            }
        }
        String unit = UnitUtils.getUnit(maxValue*1.0);

        List<Long> timeScope = getQueryTimeScope(appTrafficQueryParam);
        for (int i=0;i<timeScope.size();i++){
            boolean exist = false;
            long time = timeScope.get(i);
            String statTime = getStatTimeByTimeStamp(appTrafficQueryParam.getDateType(),time);
            for(AppTrafficChartBean appTrafficChartBean:appTrafficChartBeans){
                if (Long.valueOf(statTime).equals(appTrafficChartBean.getStatTime())){
                    exist = true;

                    if(appTrafficQueryParam.getStatType()==1){
                        Double upFlow = UnitUtils.transferUnit(appTrafficChartBean.getAppTrafficUp()*1.0,unit);
                        Double downFlow = UnitUtils.transferUnit(appTrafficChartBean.getAppTrafficDn()*1.0,unit);
                        appTrafficUpListData.add(upFlow);
                        appTrafficDnListData.add(downFlow);
                    }
/*                    appTrafficUpListData.add(appTrafficChartBean.getAppTrafficUp());
                    appTrafficDnListData.add(appTrafficChartBean.getAppTrafficDn());*/
                    appUserNumListData.add(appTrafficChartBean.getAppUserNum());
                    appPacketsNumListData.add(appTrafficChartBean.getAppPacketsNum());
                    appSessionsNumListData.add(appTrafficChartBean.getAppSessionsNum());
                    appNewSessionNumListData.add(appTrafficChartBean.getAppNewSessionNum());
                    break;
                }
            }

            if ( !exist ){
                appTrafficUpListData.add(0d);
                appTrafficDnListData.add(0d);
                appUserNumListData.add(0L);
                appPacketsNumListData.add(0L);
                appSessionsNumListData.add(0L);
                appNewSessionNumListData.add(0L);
            }
            axis.add(statTime);
        }

        if (appTrafficQueryParam.getStatType()==1){
            Series appTrafficUpSeries = new Series(appTrafficUpListData);
            series.add(appTrafficUpSeries);

            Series appTrafficDnSeries = new Series(appTrafficDnListData);
            series.add(appTrafficDnSeries);
        } else if (appTrafficQueryParam.getStatType()==2){
            Series appUserNumSeries = new Series(appUserNumListData);
            series.add(appUserNumSeries);
        } else if (appTrafficQueryParam.getStatType()==3){
            Series appPacketsNumSeries = new Series(appPacketsNumListData);
            series.add(appPacketsNumSeries);
        } else if (appTrafficQueryParam.getStatType()==4){
            Series appSessionsNumSeries = new Series(appSessionsNumListData);
            series.add(appSessionsNumSeries);

            Series appNewSessionNumSeries = new Series(appNewSessionNumListData);
            series.add(appNewSessionNumSeries);
        }

        eCharts.setAxis(axis);
        eCharts.setSeries(series);
        eCharts.setUnit(unit);
        return eCharts;
    }

    /**
     * 根据选择时间粒度和时间戳转化为与数据库对应的统计时间格式
     * @param dateType
     * @param timeStamp
     * @return
     */
    private String getStatTimeByTimeStamp(int dateType,long timeStamp){
        if(dateType==1){
            return DateUtils.formatDateyyyyMMddHH(new Date(timeStamp*1000));
        } else if (dateType==2||dateType==3){
            return DateUtils.formatDateyyyyMMdd(new Date(timeStamp*1000));
        } else {
            return DateUtils.formatDateyyyyMM(new Date(timeStamp*1000));
        }
    }

    @Override
    public long getAppIdByName(Map<String,Object> queryMap){
        return appTrafficMapper.getAppIdByName(queryMap);
    }

    @Override
    public int getAppTypeByName(Map<String,Object> queryMap){
        return appTrafficMapper.getAppTypeByName(queryMap);
    }

    /**
     * 根据时间粒度确定查询时间点
     * @param appTrafficQueryParam
     * @return
     */
    private static Long getStatTime(AppTrafficQueryParam appTrafficQueryParam){
        String statTime = null;
        if (appTrafficQueryParam.getStartTime()==null){
            appTrafficQueryParam.setDateType(2);
            return Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date()));
        }
        Date paramDate = new Date(appTrafficQueryParam.getStartTime());
        if (appTrafficQueryParam.getDateType()==1){
            statTime = DateUtils.formatDateyyyyMMddHH(paramDate);
        } else if (appTrafficQueryParam.getDateType()==2){

            statTime = DateUtils.formatDateyyyyMMdd(paramDate);

        } else if (appTrafficQueryParam.getDateType()==3){

//            Calendar calendar = DateUtils.toCalendar(paramDate);
//            calendar.set(Calendar.DAY_OF_WEEK,1);
            // 获取第一天
            Date firstDayofWeek = DateUtils.getThisWeekMonday(paramDate);
            Calendar calendar = DateUtils.toCalendar(firstDayofWeek);
            statTime = DateUtils.formatDateyyyyMMdd(calendar.getTime());
        } else if (appTrafficQueryParam.getDateType()==4){
            Calendar calendar = DateUtils.toCalendar(paramDate);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            statTime = DateUtils.formatDateyyyyMM(calendar.getTime());
        }
        return Long.valueOf(statTime);
    }

    public static void main(String[] args) {
        AppTrafficQueryParam appTrafficQueryParam = new AppTrafficQueryParam();
        appTrafficQueryParam.setDateType(4);
        appTrafficQueryParam.setStartTime(1524535200L*1000);
        long statTime = getStatTime(appTrafficQueryParam);
        System.out.println(statTime);


        System.out.println(DateUtils.parse2TimesTamp(DateUtils.formatCurrDateyyyyMMddHH(),"yyyy-MM-dd HH"));
    }

}
