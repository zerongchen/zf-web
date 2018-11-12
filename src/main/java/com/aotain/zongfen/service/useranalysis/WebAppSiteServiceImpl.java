package com.aotain.zongfen.service.useranalysis;

import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.zongfen.mapper.common.CacheMapper;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.WebFlowQueryParam;
import com.aotain.zongfen.model.useranalysis.WebFlowUbas;
import com.aotain.zongfen.utils.DateUtils;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * web应用站点分析查询service
 *
 * @author daiyh@aotain.com
 * @date 2018/06/13
 */
@Service
public class WebAppSiteServiceImpl implements IWebAppSiteService{

    private static int defaultPage = 1;
    private static int defaultPageSize = 10;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(WebAppSiteServiceImpl.class);

    @Autowired
    private CacheMapper cacheMapper;

    @Override
    public Page<WebFlowUbas> listData(WebFlowQueryParam webFlowQueryParam) {
        Connection connection = null;
        Page<WebFlowUbas> pageResult = null;
        try{
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();
            String countSql = sqlGenerator(webFlowQueryParam,true);
            int total = HadoopUtils.count(countSql);

            String sql = sqlGenerator(webFlowQueryParam,false);
            logger.info("searchSql="+sql);
            ResultSet resultSet = statement.executeQuery(sql);
            pageResult = wrapResult(resultSet,true);
            pageResult.setTotal(total);

        } catch (Exception e){
            logger.error("get data error...",e);
        } finally {
            try{
                connection.close();
            } catch (Exception e){
                logger.error("close connection error",e);
            }
        }
        return pageResult;
    }

    @Override
    public Page<WebFlowUbas> listWebSiteData(WebFlowQueryParam webFlowQueryParam){
        Connection connection = null;
        Page<WebFlowUbas> pageResult = null;
        try{
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();

            String countSql = websiteSqlGenerator(webFlowQueryParam,true,false);
            int total = HadoopUtils.count(countSql);

            String sql = websiteSqlGenerator(webFlowQueryParam,false,false);
            logger.info("searchSql="+sql);
            ResultSet resultSet = statement.executeQuery(sql);
            pageResult = wrapResult(resultSet,false);
            pageResult.setTotal(total);
        } catch (Exception e){
            logger.error("get data error...",e);
        } finally {
            try{
                connection.close();
            } catch (Exception e){
                logger.error("close connection error",e);
            }

        }
        return pageResult;
    }

    /**
     * 获取echart表格数据（只取前十条）
     * @param webFlowQueryParam
     * @return
     */
    @Override
    public List<WebFlowUbas> getChartData(WebFlowQueryParam webFlowQueryParam) {
        webFlowQueryParam.setPage(defaultPage);
        webFlowQueryParam.setPageSize(defaultPageSize);
        webFlowQueryParam.setSort("sitetrafficup");
        webFlowQueryParam.setSortOrder("desc");
        Connection connection = null;
        Page<WebFlowUbas> pageResult = null;
        try{
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();
            String sql = sqlGenerator(webFlowQueryParam,false);
            logger.info("searchSql="+sql);
            ResultSet resultSet = statement.executeQuery(sql);
            pageResult = wrapResult(resultSet,true);
        } catch (Exception e){
            logger.error("get data error...",e);
        } finally {
            try{
                connection.close();
            } catch (Exception e){
                logger.error("close connection error",e);
            }

        }

        return pageResult;
    }

    @Override
    public List<WebFlowUbas> getLineData(WebFlowQueryParam webFlowQueryParam) {
        Connection connection = null;
        Page<WebFlowUbas> pageResult = null;
        try{
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();

            // 不限制记录条数
            String sql = websiteSqlGenerator(webFlowQueryParam,false,true);
            logger.info("searchSql="+sql);
            ResultSet resultSet = statement.executeQuery(sql);
            pageResult = wrapResult(resultSet,false);
        } catch (Exception e){
            logger.error("get data error...",e);
        } finally {
            try{
                connection.close();
            } catch (Exception e){
                logger.error("close connection error",e);
            }

        }
        return pageResult;
    }

    @Override
    public List<WebFlowUbas> getExportData(WebFlowQueryParam webFlowQueryParam){
        Connection connection = null;
        Page<WebFlowUbas> pageResult = null;
        try{
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();
            // 获取不分页sql
            StringBuilder sql = new StringBuilder(sqlGenerator(webFlowQueryParam,true));
            if (webFlowQueryParam.getSort()!=null && webFlowQueryParam.getSortOrder()!=null){
                sql.append(" order by ").append(webFlowQueryParam.getSort()).append(" "+webFlowQueryParam.getSortOrder());
            } else {
                sql.append(" order by ").append(" sitetrafficup ").append(" desc ");
            }
            logger.info("getExportDataSql="+sql.toString());
            ResultSet resultSet = statement.executeQuery(sql.toString());
            pageResult = wrapResult(resultSet,true);
        } catch (Exception e){
            logger.error("get data error...",e);
        } finally {
            try{
                connection.close();
            } catch (Exception e){
                logger.error("close connection error",e);
            }

        }
        return pageResult;
    }

    @Override
    public ECharts<Series> wrapChartData(List<WebFlowUbas> webFlowUbasList){
        ECharts<Series> eCharts = new ECharts<>();
        List<String> axis = Lists.newArrayList();
        List<Long> appTrafficUpListData = Lists.newArrayList();
        List<Long> appTrafficDnListData = Lists.newArrayList();
        for (WebFlowUbas webFlowUbas:webFlowUbasList){
            appTrafficDnListData.add(webFlowUbas.getSiteTrafficDn());
            appTrafficUpListData.add(webFlowUbas.getSiteTrafficUp());
            axis.add(webFlowUbas.getSiteTypeName()+"-"+webFlowUbas.getSiteName());
        }

        List<Series> series = Lists.newArrayList();
        Series appTrafficUpSeries = new Series("上行流量","bar",appTrafficUpListData);
        series.add(appTrafficUpSeries);
        appTrafficUpSeries.setStack("up");
//        appTrafficUpSeries.setBarMaxWidth(40);

        Series appTrafficDnSeries = new Series("下行流量","bar",appTrafficDnListData);
        series.add(appTrafficDnSeries);
        appTrafficDnSeries.setStack("up");
//        appTrafficDnSeries.setBarMaxWidth(40);

        eCharts.setAxis(axis);
        eCharts.setSeries(series);
        return eCharts;
    }

    /**
     * 生成echartt趋势图格式数据
     * @return
     */
    @Override
    public ECharts<Series> wrapLineData(WebFlowQueryParam appTrafficQueryParam,List<WebFlowUbas> webFlowUbasList){
        ECharts<Series> eCharts = new ECharts<>();
        List<String> axis = Lists.newArrayList();
        List<Series> series = Lists.newArrayList();
        List<Long> appTrafficUpListData = Lists.newArrayList();
        List<Long> appTrafficDnListData = Lists.newArrayList();

        List<Long> timeScope = getQueryTimeScope(appTrafficQueryParam);
        for (int i=0;i<timeScope.size();i++){
            boolean exist = false;
            long time = timeScope.get(i);
            String statTime = getStatTimeByTimeStamp(appTrafficQueryParam.getStatType(),time);
            for(WebFlowUbas webFlowUbas:webFlowUbasList){
                if (statTime.equals(webFlowUbas.getStatTime().toString())){
                    exist = true;
                    appTrafficUpListData.add(webFlowUbas.getSiteTrafficUp());
                    appTrafficDnListData.add(webFlowUbas.getSiteTrafficDn());
                    break;
                }
            }

            if ( !exist ){
                appTrafficUpListData.add(0L);
                appTrafficDnListData.add(0L);
            }
            axis.add(statTime);
        }

        Series appTrafficUpSeries = new Series("上行流量","line",appTrafficUpListData);
        series.add(appTrafficUpSeries);
        Series appTrafficDnSeries = new Series("下行流量","line",appTrafficDnListData);
        series.add(appTrafficDnSeries);
        appTrafficDnSeries.setXAxisIndex(1);
        appTrafficDnSeries.setYAxisIndex(1);

        eCharts.setAxis(axis);
        eCharts.setSeries(series);
        return eCharts;
    }

    /**
     * 根据条件查询对应数据
     * 生成对应的sql语句
     * @return
     */
    private String sqlGenerator(WebFlowQueryParam webFlowQueryParam,boolean count){
        // 根据前端传递的startTime确定统计时间
        long statTime = getStatTime(webFlowQueryParam);
        webFlowQueryParam.setStatTime(statTime);

        StringBuilder sql = new StringBuilder("");
        int page,pageSize;
        if (webFlowQueryParam.getPage()==null){
            page = defaultPage;
        } else {
            page = webFlowQueryParam.getPage();
        }
        if (webFlowQueryParam.getPageSize()==null){
            pageSize = defaultPageSize;
        } else {
            pageSize = webFlowQueryParam.getPageSize();
        }

        String tableName = "";
        StringBuilder stringBuilder = new StringBuilder("");
        // 根据统计时间粒度确定查询表
        if (webFlowQueryParam.getStatType()==null){
            logger.info("the statType is null,so set to the default value");
            webFlowQueryParam.setStatType(2);
        }
        if (webFlowQueryParam.getStatType()==1){
            tableName = "job_ubas_webflow_h ";
            if ( webFlowQueryParam.getStatTime() != null ) {
                String day = webFlowQueryParam.getStatTime().toString().substring(0,8);
                int hour = Integer.valueOf(webFlowQueryParam.getStatTime().toString().substring(8,10));
                stringBuilder.append(" and dt = ").append("'").append(day).append("'").append(" and hour = ").append(hour);
            }
        } else if (webFlowQueryParam.getStatType()==2){
            tableName = "job_ubas_webflow_d ";
            if ( webFlowQueryParam.getStatTime() != null ) {
                stringBuilder.append(" and dt = ").append("'").append(webFlowQueryParam.getStatTime()).append("'");
            }
        } else if (webFlowQueryParam.getStatType()==3){
            tableName = "job_ubas_webflow_w ";
            if ( webFlowQueryParam.getStatTime() != null ) {
                stringBuilder.append(" and dt = ").append("'").append(webFlowQueryParam.getStatTime()).append("'");
            }
        } else if (webFlowQueryParam.getStatType()==4){
            tableName = "job_ubas_webflow_m ";
            if ( webFlowQueryParam.getStatTime() != null ) {
                stringBuilder.append(" and dt = ").append("'").append(webFlowQueryParam.getStatTime()).append("'");
            }
        }

        if ( !count ) {
            sql.append("select sitetype,sitename,sum(sitehitfreq) AS sitehitfreqsum,sum(sitetraffic_up) AS sitetrafficup,sum(sitetraffic_dn)  AS sitetrafficdn,sum(sitetraffic_up)+sum(sitetraffic_dn) AS sitetrafficsum from ").append(tableName);
        } else {
            sql.append("select sitetype,sitename from ").append(tableName);
        }
        sql.append(" where 1=1 ");
        // 分区查询条件
        sql.append(stringBuilder.toString());

        if (webFlowQueryParam.getProbeType() != null) {
            sql.append(" and probe_type = ").append(webFlowQueryParam.getProbeType());
        }

        if ( webFlowQueryParam.getAreaId() != null && !"-1".equals(webFlowQueryParam.getAreaId())) {
            sql.append(" and area_id = ").append(webFlowQueryParam.getAreaId());
        }
        if ( webFlowQueryParam.getUserGroupNo() != null && webFlowQueryParam.getUserGroupNo() != -1) {
            sql.append(" and usergroupno = ").append(webFlowQueryParam.getUserGroupNo());
        }
        if ( webFlowQueryParam.getSiteType() != null && webFlowQueryParam.getSiteType() != -1) {
            sql.append(" and sitetype = ").append(webFlowQueryParam.getSiteType());
        }
        if (StringUtils.isNotEmpty(webFlowQueryParam.getSiteName()) ) {
            sql.append(" and sitename like ").append("'%").append(webFlowQueryParam.getSiteName()).append("%'");
        }
        sql.append(" group by sitetype,sitename ");

        // count=true 统计sql，不需要order by，limit
        if ( !count ){
            if (webFlowQueryParam.getSort()!=null && webFlowQueryParam.getSortOrder()!=null){
                if ("siteHitFreq".equals(webFlowQueryParam.getSort())){
                    sql.append(" order by ").append("sitehitfreqsum").append(" "+webFlowQueryParam.getSortOrder());
                }else {
                    sql.append(" order by ").append(webFlowQueryParam.getSort()).append(" "+webFlowQueryParam.getSortOrder());
                }
            } else {

                sql.append(" order by ").append(" sitetrafficup ").append(" desc ");
            }
            // count= false 分页sql
            sql.append(" limit ").append(pageSize);
            sql.append(" offset ").append((page-1)*pageSize);
        }
        return sql.toString();
    }

    /**
     * 根据统计粒度和startTime确定查询时间点
     * @param webFlowQueryParam
     * @return
     */
    private static Long getStatTime(WebFlowQueryParam webFlowQueryParam){
        String statTime = null;
        if (webFlowQueryParam.getStatType() ==null){
            webFlowQueryParam.setStatType(2);
            return Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date()));
        }
        Date paramDate = new Date(webFlowQueryParam.getStartTime());
        if (webFlowQueryParam.getStatType()==1){
            statTime = DateUtils.formatDateyyyyMMddHH(paramDate);
        } else if (webFlowQueryParam.getStatType()==2){
            statTime = DateUtils.formatDateyyyyMMdd(paramDate);
        } else if (webFlowQueryParam.getStatType()==3){
            Date firstDayofWeek = DateUtils.getThisWeekMonday(paramDate);
            Calendar calendar = DateUtils.toCalendar(firstDayofWeek);
            statTime = DateUtils.formatDateyyyyMMdd(calendar.getTime());
        } else if (webFlowQueryParam.getStatType()==4){
            Calendar calendar = DateUtils.toCalendar(paramDate);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            statTime = DateUtils.formatDateyyyyMMdd(calendar.getTime());
        }
        return Long.valueOf(statTime);
    }

    /**
     * 生成对应的sql语句
     * @return
     */
    private String websiteSqlGenerator(WebFlowQueryParam webFlowQueryParam,boolean count,boolean range){
        // 范围查询
        generateLineQueryCondition(webFlowQueryParam);

        StringBuilder sql = new StringBuilder("");
        int page,pageSize;
        if (webFlowQueryParam.getPage()==null){
            page = defaultPage;
        } else {
            page = webFlowQueryParam.getPage();
        }
        if (webFlowQueryParam.getPageSize()==null){
            pageSize = defaultPageSize;
        } else {
            pageSize = webFlowQueryParam.getPageSize();
        }

        String tableName = "";
        StringBuilder sb = new StringBuilder("");
        // 根据统计时间粒度确定查询表
        if (webFlowQueryParam.getStatType()==null){
            logger.info("the statType is null,so set to the default value");
            webFlowQueryParam.setStatType(2);
        }
        if (webFlowQueryParam.getStatType()==1){

            tableName = "job_ubas_webflow_h ";
            if ( webFlowQueryParam.getStartTime() != null ) {
                String day = webFlowQueryParam.getStartTime().toString().substring(0,8);
                int hour = Integer.valueOf(webFlowQueryParam.getStartTime().toString().substring(8,10));
                sb.append(" and (dt = ").append("'").append(day).append("'").append(" and hour >= ").append(hour).append(")");
            }
            if ( webFlowQueryParam.getEndTime() != null ) {
                String day = webFlowQueryParam.getEndTime().toString().substring(0,8);
                int hour = Integer.valueOf(webFlowQueryParam.getEndTime().toString().substring(8,10));
                sb.append(" or (dt = ").append("'").append(day).append("'").append(" and hour <= ").append(hour).append(")");
            }
        } else if (webFlowQueryParam.getStatType()==2){
            tableName = "job_ubas_webflow_d ";
            if ( webFlowQueryParam.getStartTime() != null ) {
                sb.append(" and dt >= ").append("'").append(webFlowQueryParam.getStartTime()).append("'");
            }
            if ( webFlowQueryParam.getEndTime() != null ) {
                sb.append(" and dt <= ").append("'").append(webFlowQueryParam.getEndTime()).append("'");
            }
        } else if (webFlowQueryParam.getStatType()==3){
            tableName = "job_ubas_webflow_w ";
            if ( webFlowQueryParam.getStartTime() != null ) {
                sb.append(" and dt >= ").append("'").append(webFlowQueryParam.getStartTime()).append("'");
            }
            if ( webFlowQueryParam.getEndTime() != null ) {
                sb.append(" and dt <= ").append("'").append(webFlowQueryParam.getEndTime()).append("'");
            }
        } else if (webFlowQueryParam.getStatType()==4) {
            tableName = "job_ubas_webflow_m ";
            if ( webFlowQueryParam.getStartTime() != null ) {
                sb.append(" and dt >= ").append("'").append(webFlowQueryParam.getStartTime()).append("'");
            }
            if ( webFlowQueryParam.getEndTime() != null ) {
                sb.append(" and dt <= ").append("'").append(webFlowQueryParam.getEndTime()).append("'");
            }

        }
        sql.append("select stat_time AS stattime,sum(sitehitfreq) AS sitehitfreqsum,sum(sitetraffic_up) AS sitetrafficup,sum(sitetraffic_dn)  AS sitetrafficdn,sum(sitetraffic_up)+sum(sitetraffic_dn) AS sitetrafficsum from ").append(tableName);
        sql.append(" where 1=1 ");
        if (webFlowQueryParam.getProbeType() != null) {
            sql.append(" and probe_type = ").append(webFlowQueryParam.getProbeType());
        }

        sql.append(sb.toString());
//        if ( webFlowQueryParam.getStartTime() != null ) {
//            sql.append(" and dt >= ").append("'").append(webFlowQueryParam.getStartTime()).append("'");
//        }
//        if ( webFlowQueryParam.getEndTime() != null ) {
//            sql.append(" and dt <= ").append("'").append(webFlowQueryParam.getEndTime()).append("'");
//        }
        if ( webFlowQueryParam.getAreaId() != null && !"-1".equals(webFlowQueryParam.getAreaId())) {
            sql.append(" and area_id = ").append(webFlowQueryParam.getAreaId());
        }
        if ( webFlowQueryParam.getUserGroupNo() != null && webFlowQueryParam.getUserGroupNo() != -1) {
            sql.append(" and usergroupno = ").append(webFlowQueryParam.getUserGroupNo());
        }
        if ( webFlowQueryParam.getSiteType() != null && webFlowQueryParam.getSiteType() != -1) {
            sql.append(" and sitetype = ").append(webFlowQueryParam.getSiteType());
        }
        if (StringUtils.isNotEmpty(webFlowQueryParam.getSiteName()) ) {
            sql.append(" and sitename like ").append("'%").append(webFlowQueryParam.getSiteName()).append("%'");
        }
        sql.append(" group by stat_time ");

        if ( !count ){
            if (webFlowQueryParam.getSort()!=null && webFlowQueryParam.getSortOrder()!=null){
                if ("siteHitFreq".equals(webFlowQueryParam.getSort())){
                    sql.append(" order by ").append("sitehitfreqsum").append(" "+webFlowQueryParam.getSortOrder());
                }else {
                    sql.append(" order by ").append(webFlowQueryParam.getSort()).append(" "+webFlowQueryParam.getSortOrder());
                }
//                sql.append(" order by ").append(webFlowQueryParam.getSort()).append(" "+webFlowQueryParam.getSortOrder());
            } else {
                sql.append(" order by ").append(" sitetrafficup ").append(" desc ");
            }
            if ( !range ){
                sql.append(" limit ").append(pageSize);
                sql.append(" offset ").append((page-1)*pageSize);
            }

        }
        return sql.toString();
    }

    /**
     * 封装结果集
     * @param resultSet
     * @return
     */
    private Page<WebFlowUbas> wrapResult(ResultSet resultSet,boolean firstTable) throws SQLException {
        Page<WebFlowUbas> page = new Page<>();
        if (resultSet==null){
            return null;
        }
        while(resultSet.next()){
            WebFlowUbas webFlowUbas = new WebFlowUbas();
            if (firstTable){
                // 主页面需要获取的字段
                webFlowUbas.setSiteType(resultSet.getInt("sitetype"));
                webFlowUbas.setSiteName(resultSet.getString("sitename"));
                webFlowUbas.setSiteHitFreq(resultSet.getLong("sitehitfreqsum"));
                webFlowUbas.setSiteTrafficUp(resultSet.getLong("sitetrafficup"));
                webFlowUbas.setSiteTrafficDn(resultSet.getLong("sitetrafficdn"));
                webFlowUbas.setSiteTrafficSum(resultSet.getLong("sitetrafficsum"));
            } else {
                webFlowUbas.setStatTime(resultSet.getInt("stattime"));
                webFlowUbas.setSiteHitFreq(resultSet.getLong("sitehitfreqsum"));
                webFlowUbas.setSiteTrafficUp(resultSet.getLong("sitetrafficup"));
                webFlowUbas.setSiteTrafficDn(resultSet.getLong("sitetrafficdn"));
                webFlowUbas.setSiteTrafficSum(resultSet.getLong("sitetrafficsum"));
            }

            page.add(webFlowUbas);
        }
        return page;
    }

    /**
     * 根据前端条件确定折线图查询条件(查询时间点)
     * @param webFlowQueryParam
     */
    private void generateLineQueryCondition(WebFlowQueryParam webFlowQueryParam){
        List<Long> timeScope = getQueryTimeScope(webFlowQueryParam);
        long startTime = 0L,endTime = 0L;

        if (webFlowQueryParam.getStatType()==1){
            startTime = Long.valueOf(DateUtils.formatDateyyyyMMddHH(new Date(timeScope.get(0)*1000)));
            endTime = Long.valueOf(DateUtils.formatDateyyyyMMddHH(new Date(timeScope.get(timeScope.size()-1)*1000)));
        } else {
            startTime = Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date(timeScope.get(0)*1000)));
            endTime = Long.valueOf(DateUtils.formatDateyyyyMMdd(new Date(timeScope.get(timeScope.size()-1)*1000)));
        }

        webFlowQueryParam.setStartTime(startTime);
        webFlowQueryParam.setEndTime(endTime);
    }

    /**
     * 根据时间粒度确定折查询时间范围(时间点)
     * @param webFlowQueryParam
     */
    private List<Long> getQueryTimeScope(WebFlowQueryParam webFlowQueryParam){
        List<Long> timeScope = Lists.newArrayList();
        // 查询过去24小时
        if (webFlowQueryParam.getStatType()==1){
            timeScope = DateUtils.getLastDayHourInTimeStamp();
            //过去30天
        } else if (webFlowQueryParam.getStatType()==2){
            timeScope = DateUtils.getLastMonthInTimeStamp();
            //过去10周
        } else if (webFlowQueryParam.getStatType()==3){
            timeScope = DateUtils.getLastTenWeekInTimeStamp();
            //过去10月
        } else if (webFlowQueryParam.getStatType()==4){
            timeScope = DateUtils.getLastTenMonthInTimeStamp();
        }
        return timeScope;
    }

    /**
     * 根据选择时间粒度和时间戳转化为与数据库对应的统计时间格式
     * @param statType
     * @param timeStamp
     * @return
     */
    private String getStatTimeByTimeStamp(int statType,long timeStamp){
        if(statType==1){
            return DateUtils.formatDateyyyyMMddHH(new Date(timeStamp*1000));
        } else {
            return DateUtils.formatDateyyyyMMdd(new Date(timeStamp*1000));
        }
    }

    @Override
    public int getSiteTypeByName(Map<String,Object> queryMap){
        return cacheMapper.getSiteTypeByName(queryMap);
    }

    @Override
    public boolean createExportTask(Map<String, Object> params) {
        return false;
    }

    public static void main(String[] args) {
        System.out.println("2018062608".substring(0,8));
        System.out.println("2018062608".substring(8,10));
    }
}
