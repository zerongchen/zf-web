package com.aotain.zongfen.service.useranalysis;

import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.mapper.useranalysis.DDoSAttackMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.DDoSAttackArea;
import com.aotain.zongfen.model.useranalysis.DDoSQueryParam;
import com.aotain.zongfen.model.useranalysis.DDoSUbas;
import com.aotain.zongfen.model.useranalysis.DDoSUbasDetail;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * DDoS异常攻击服务类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/21
 */
@Service
public class DDoSAttackServiceImpl implements IDDoSAttackService{


    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(DDoSAttackServiceImpl.class);

    private static int defaultPage = 1;
    private static int defaultPageSize = 10;

    private static String staticUnit = "M";

    @Autowired
    private DDoSAttackMapper dDoSAttackMapper;

    @Override
    public Page<DDoSUbasDetail> listData(DDoSQueryParam dDoSQueryParam) {
        Connection connection = null;
        Page<DDoSUbasDetail> pageResult = null;
        try{
            setStatPeriodPoint(dDoSQueryParam);
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();

            String countSql = sqlGenerator(dDoSQueryParam,true);
            logger.info("ddosAttack countSql:"+countSql);
            int total = HadoopUtils.count(countSql);
            String pageSql = sqlGenerator(dDoSQueryParam,false);
            logger.info("searchSql="+pageSql);
            ResultSet resultSet = statement.executeQuery(pageSql);
            pageResult = wrapResult(resultSet,true);
            pageResult.setTotal(total);
        } catch (Exception e){
            logger.error("get listData error...",e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("close connection error...",e);
            }
        }

        return pageResult;
    }

    @Override
    public Page<DDoSAttackArea> listAreaData(DDoSQueryParam dDoSQueryParam){
        Connection connection = null;
        Page<DDoSAttackArea> pageResult = null;
        try{
            setStatPeriodPoint(dDoSQueryParam);
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();

            String countSql = areaSqlGenerator(dDoSQueryParam,true);
            int total = HadoopUtils.count(countSql);

            String pageSql = areaSqlGenerator(dDoSQueryParam,false);
            logger.info("areaDataSearchSql="+pageSql);
            ResultSet resultSet = statement.executeQuery(pageSql);
            pageResult = wrapAreaResult(resultSet);
            pageResult.setTotal(total);
        } catch (Exception e){
            logger.error("get listData error...",e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("close connection error...",e);
            }
        }

        return pageResult;
    }

    @Override
    public List<DDoSUbasDetail> listExportData(DDoSQueryParam dDoSQueryParam) {
        Connection connection = null;
        List<DDoSUbasDetail> dDoSUbasDetails = null;
        try{
            setStatPeriodPoint(dDoSQueryParam);
            connection = HadoopUtils.getConnection();
            Statement statement = connection.createStatement();

            StringBuilder pageSql = new StringBuilder(sqlGenerator(dDoSQueryParam,true));
            logger.info("searchSql="+pageSql);
            if (dDoSQueryParam.getSort()!=null && dDoSQueryParam.getSortOrder()!=null){
                pageSql.append(" order by ").append(dDoSQueryParam.getSort()).append(" "+dDoSQueryParam.getSortOrder());
            } else {
                pageSql.append(" order by ").append(" appAttackTraffic ").append(" desc ");
            }
            ResultSet resultSet = statement.executeQuery(pageSql.toString());
            dDoSUbasDetails = wrapResult(resultSet,true);

        } catch (Exception e){
            logger.error("get Data error...",e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("close connection error...",e);
            }
        }

        return dDoSUbasDetails;
    }

    private String sqlGenerator(DDoSQueryParam dDoSQueryParam,boolean count){

        StringBuilder sb = new StringBuilder("");
        sb.append(" select attack_starttime AS attackStartTime,attack_endtime AS attackEndTime,pusergrougno AS userGroupNo," +
                "appattacktype AS appAttackType,appattacktraffic AS appAttackTraffic,appattackrate AS appAttackRate,attackareanum AS attackAreaNum");
        sb.append(" from zf.job_ubas_ddos where 1 =1 ");

        sb.append(" and dt >= ").append("'").append(dDoSQueryParam.getSearchStart()).append("'");
        sb.append(" and dt <= ").append("'").append(dDoSQueryParam.getSearchEnd()).append("'");

//        sb.append(" and attack_starttime >= ").append(dDoSQueryParam.getStartTime()/1000);
//        sb.append(" and attack_endtime <= ").append(dDoSQueryParam.getEndTime()/1000+60*60*24-1);
        if (dDoSQueryParam.getAppAttackType()!=null&&dDoSQueryParam.getAppAttackType()!=-1){
            sb.append(" and appattacktype = ").append(dDoSQueryParam.getAppAttackType());
        }
        if (dDoSQueryParam.getUserGroupNo()!=null&&dDoSQueryParam.getUserGroupNo()!=-1){
            sb.append(" and pusergrougno = ").append(dDoSQueryParam.getUserGroupNo());
        }

        int page,pageSize;
        if (dDoSQueryParam.getPage()==null){
            page = defaultPage;
        } else {
            page = dDoSQueryParam.getPage();
        }
        if (dDoSQueryParam.getPageSize()==null){
            pageSize = defaultPageSize;
        } else {
            pageSize = dDoSQueryParam.getPageSize();
        }
        // count=true 统计sql，不需要order by，limit
        if ( !count ){
            if (dDoSQueryParam.getSort()!=null && dDoSQueryParam.getSortOrder()!=null){
                sb.append(" order by ").append(dDoSQueryParam.getSort()).append(" "+dDoSQueryParam.getSortOrder());
            } else {
                sb.append(" order by ").append(" appAttackTraffic ").append(" desc ");
            }
            // count= false 分页sql
            sb.append(" limit ").append(pageSize);
            sb.append(" offset ").append((page-1)*pageSize);
        }
        return sb.toString();
    }

    /**
     * 生成区域详情查询sql
     * @param dDoSQueryParam
     * @param count
     * @return
     */
    private String areaSqlGenerator(DDoSQueryParam dDoSQueryParam,boolean count){
        StringBuilder sb = new StringBuilder("");
        sb.append(" select attackareaname AS attackAreaName,attacktraffic AS attackTraffic,attacknum AS attackNum,sourceip_num AS sourceIpNum");
        sb.append(" from job_ubas_ddos_area where 1 =1 ");

        sb.append(" and dt = ").append("'").append(dDoSQueryParam.getSearchStart()).append("'");
//        sb.append(" and dt <= ").append("'").append(dDoSQueryParam.getSearchEnd()).append("'");

        sb.append(" and attack_starttime >= ").append(dDoSQueryParam.getStartTime()/1000);
        sb.append(" and attack_endtime <= ").append(dDoSQueryParam.getEndTime()/1000);
        if (dDoSQueryParam.getAppAttackType()!=null&&dDoSQueryParam.getAppAttackType()!=-1){
            sb.append(" and appattacktype = ").append(dDoSQueryParam.getAppAttackType());
        }
        if (dDoSQueryParam.getUserGroupNo()!=null&&dDoSQueryParam.getUserGroupNo()!=-1){
            sb.append(" and pusergrougno = ").append(dDoSQueryParam.getUserGroupNo());
        }

        int page,pageSize;
        if (dDoSQueryParam.getPage()==null){
            page = defaultPage;
        } else {
            page = dDoSQueryParam.getPage();
        }
        if (dDoSQueryParam.getPageSize()==null){
            pageSize = defaultPageSize;
        } else {
            pageSize = dDoSQueryParam.getPageSize();
        }
        // count=true 统计sql，不需要order by，limit
        if ( !count ){
            if (dDoSQueryParam.getSort()!=null && dDoSQueryParam.getSortOrder()!=null){
                sb.append(" order by ").append(dDoSQueryParam.getSort()).append(" "+dDoSQueryParam.getSortOrder());
            } else {
                sb.append(" order by ").append(" attackTraffic ").append(" desc ");
            }
            // count= false 分页sql
            sb.append(" limit ").append(pageSize);
            sb.append(" offset ").append((page-1)*pageSize);
        }
        return sb.toString();
    }

    /**
     * 封装结果集
     * @param resultSet
     * @return
     */
    private Page<DDoSUbasDetail> wrapResult(ResultSet resultSet,boolean firstTable) throws SQLException {
        Page<DDoSUbasDetail> page = new Page<>();
        if (resultSet==null){
            return null;
        }
        while(resultSet.next()){
            DDoSUbasDetail dDoSUbasDetail = new DDoSUbasDetail();
            if (firstTable){
                // 主页面需要获取的字段
                dDoSUbasDetail.setAttackStartTime(resultSet.getLong("attackStartTime"));
                dDoSUbasDetail.setAttackEndTime(resultSet.getLong("attackEndTime"));
                dDoSUbasDetail.setUserGroupNo(resultSet.getLong("userGroupNo"));
                dDoSUbasDetail.setAppAttackType(resultSet.getInt("appAttackType"));
                dDoSUbasDetail.setAppAttackTraffic(resultSet.getLong("appAttackTraffic"));
                dDoSUbasDetail.setAppAttackRate(resultSet.getLong("appAttackRate"));
                dDoSUbasDetail.setAttackAreaNum(resultSet.getInt("attackAreaNum"));
            } else {

            }

            page.add(dDoSUbasDetail);
        }
        return page;
    }

    private Page<DDoSAttackArea> wrapAreaResult(ResultSet resultSet) throws SQLException {
        Page<DDoSAttackArea> page = new Page<>();
        if (resultSet==null){
            return null;
        }
        while(resultSet.next()){
            DDoSAttackArea dDoSAttackArea = new DDoSAttackArea();

            // 主页面需要获取的字段

            dDoSAttackArea.setAttackAreaName(resultSet.getString("attackAreaName"));
            dDoSAttackArea.setAttackTraffic(resultSet.getLong("attackTraffic"));
            dDoSAttackArea.setAttackNum(resultSet.getInt("attackNum"));
            dDoSAttackArea.setSourceIpNum(resultSet.getInt("sourceIpNum"));

            page.add(dDoSAttackArea);
        }
        return page;
    }

    @Override
    public void setStatPeriodPoint(DDoSQueryParam dDoSQueryParam) {
        String startTime = null;
        String endTime = null;
        // 确定查询的开始时间和结束时间
        if ( dDoSQueryParam.getStartTime()==null && dDoSQueryParam.getEndTime()==null ){
            endTime = DateUtils.formatDateyyyyMMdd(new Date());
            Date startDate = DateUtils.addDateOfMonth(new Date(),-29);
            startTime = DateUtils.formatDateyyyyMMdd(startDate);
        } else if ( dDoSQueryParam.getStartTime()!=null && dDoSQueryParam.getEndTime()==null ){
            startTime = DateUtils.formatDateyyyyMMdd(new Date(dDoSQueryParam.getStartTime()));
            // 结束时间设置为当前时间后一天
            endTime = DateUtils.formatDateyyyyMMdd(DateUtils.addDateOfMonth(new Date(),1));
        } else if ( dDoSQueryParam.getStartTime()==null && dDoSQueryParam.getEndTime()!=null ){
            endTime = DateUtils.formatDateyyyyMMdd(new Date(dDoSQueryParam.getEndTime()));
            // 开始时间为结束时间前三十天
            Date startDate = DateUtils.addDateOfMonth(new Date(dDoSQueryParam.getEndTime()),-29);
            startTime = DateUtils.formatDateyyyyMMdd(startDate);
        } else if ( dDoSQueryParam.getStartTime()!=null && dDoSQueryParam.getEndTime()!=null ){
            endTime = DateUtils.formatDateyyyyMMdd(new Date(dDoSQueryParam.getEndTime()));
            startTime = DateUtils.formatDateyyyyMMdd(new Date(dDoSQueryParam.getStartTime()));
        }
        dDoSQueryParam.setSearchStart(Long.valueOf(startTime));
        dDoSQueryParam.setSearchEnd(Long.valueOf(endTime));
    }

    @Override
    public Map<String,Map<String,Double>> getChartData(DDoSQueryParam dDoSQueryParam) {
        Map<String,Map<String,Double>> resultMap = Maps.newHashMap();
        setStatPeriodPoint(dDoSQueryParam);
        List<DDoSUbas> result = dDoSAttackMapper.getChartData(dDoSQueryParam);

        List<Long> timePoint = DateUtils.getDaysBetweenDay(dDoSQueryParam.getStartTime(),dDoSQueryParam.getEndTime());

        resultMap = specifyData2(result,timePoint);
        return resultMap;
    }

    /**
     * 根据attackType将查询结果分类
     * @param result
     * @return
     */
    private Map<Integer,List<DDoSUbas>> specifyData(List<DDoSUbas> result){
        Map<Integer,List<DDoSUbas>> resultMap = Maps.newHashMap();
        for(DDoSUbas dDoSUbas:result){
            List<DDoSUbas> exist = resultMap.get(dDoSUbas.getAppAttackType());
            if (exist!=null){
                exist.add(dDoSUbas);
                resultMap.put(dDoSUbas.getAppAttackType(),exist);
            } else {
                List<DDoSUbas> dDoSUbas1 = Lists.newArrayList();
                dDoSUbas1.add(dDoSUbas);
                resultMap.put(dDoSUbas.getAppAttackType(),dDoSUbas1);
            }
        }
        return resultMap;
    }

    /**
     * 根据attackType将查询结果分类
     * @param result
     * @return
     */
    private Map<String,Map<String,Double>> specifyData2(List<DDoSUbas> result,List<Long> timePoint){

        Long maxValue=0l;
        for(DDoSUbas model:result){
            if(maxValue<=model.getAppAttackTraffic()){
                maxValue=model.getAppAttackTraffic();
            }
        }
        String unit=getUnit(maxValue*1.0);
        staticUnit=unit;
        Map<String,Map<String,Double>> resultMap1 = Maps.newHashMap();

        Map<String,Double>  date_sries = new HashMap<>();
        for(Long date:timePoint){
            date_sries.put(date+"",0.0);
        }

        for(DDoSUbas dDoSUbas:result){
            Map<String,Double> exist = resultMap1.get(dDoSUbas.getAppAttackType()+"");
            if (exist!=null){
                Double  traffic = dDoSUbas.getAppAttackTraffic()*1.0;
                exist.put(dDoSUbas.getStatTime()+"",transferUnit(traffic,unit));
            } else {
                Double  traffic = dDoSUbas.getAppAttackTraffic()*1.0;
                date_sries.put(dDoSUbas.getStatTime()+"",transferUnit(traffic,unit));
                resultMap1.put(dDoSUbas.getAppAttackType()+"",date_sries);
            }
        }
        return resultMap1;
    }
    /**
     * 根据查询时间填充每个时间点的数据
     * @param dDoSQueryParam
     * @param dDoSUbasList
     */
    private List<Long> fillDataByTimeScope(DDoSQueryParam dDoSQueryParam,List<DDoSUbas> dDoSUbasList){
        List<Long> dataPoint = Lists.newArrayList();
        List<Long> timePoint = DateUtils.getDaysBetweenDay(dDoSQueryParam.getStartTime(),dDoSQueryParam.getEndTime());

        if (dDoSUbasList==null){
            for(int i=0;i<timePoint.size();i++){
                dataPoint.add(0L);
            }
            return dataPoint;
        }
        for(Long time:timePoint){
            for(DDoSUbas dDoSUbas:dDoSUbasList){
                if (time == dDoSUbas.getStatTime().longValue()){
                    dataPoint.add(dDoSUbas.getAppAttackTraffic());
                }
            }
        }
        for(int i=0;i<timePoint.size();i++){
            Long time = timePoint.get(i);
        }
        return dataPoint;
    }

    @Override
    public ECharts<Series> wrapChartData(DDoSQueryParam dDoSQueryParam,Map<String,Map<String,Double>> ddosUbasMap) {

        Map<String,String>  typeMap = new HashMap<>();
        typeMap.put("1","ICMP Redirection");
        typeMap.put("2","ICMP Unreacheble");
        typeMap.put("3","TCP anomaly Connection");
        typeMap.put("4","DNS Query Application Attack");
        typeMap.put("5","DNS Reply Application Attack");
        typeMap.put("6","SIP Application Attack");
        typeMap.put("7","HTTPS Application Attack");
        typeMap.put("8","HTTP Get Application Attack");
        typeMap.put("9","Challenge Collapsar Attack");

        Double maxValue=0.0;

        ECharts<Series> eCharts = new ECharts<>();
        List<Series> seriesList = Lists.newArrayList();
        List<String> axis = Lists.newArrayList();
        List<String> legend = new ArrayList<String>();

        for(Map.Entry<String,Map<String,Double>> k :ddosUbasMap.entrySet()){
            String k1 = k.getKey();
            Map<String,Double> v1 = k.getValue();

            for(Double d:v1.values()){
                if(d>=maxValue){
                    maxValue=d;
                }
            }

            Map<String, Double> sortMap = new TreeMap<String, Double>(
                    new Comparator<String>(){
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
            sortMap.putAll(v1);

            List data = Arrays.asList(sortMap.values().toArray());
            Series series1 = new Series(typeMap.get(k1+""),"line",typeMap.get(k1+""), data);
            seriesList.add(series1);
            legend.add(typeMap.get(k1+""));
        }
        List<Long> timeScope = DateUtils.getDaysBetweenDay(dDoSQueryParam.getStartTime(),dDoSQueryParam.getEndTime());
        for (Long time:timeScope){
            axis.add(time.toString());
        }
        //单位
        String unit=getUnit(maxValue);

        DataZoom dataRoom = new DataZoom();
        dataRoom.setZoomLock(false);
        dataRoom.setShow(true);
        int start = 0;
        int end = 100;
        dataRoom.setStart(start);
        dataRoom.setEnd(end);

        eCharts.setAxis(axis);
        eCharts.setSeries(seriesList);
        eCharts.setLegend(legend);
        eCharts.setDataZoom(dataRoom);
        eCharts.setUnit(staticUnit);
        return eCharts;
    }

    public static void main(String[] args) {
        DDoSAttackServiceImpl dDoSAttackService = new DDoSAttackServiceImpl();
        DDoSQueryParam dDoSQueryParam = new DDoSQueryParam();
        dDoSQueryParam.setStartTime(System.currentTimeMillis()-1000*60*60*24*15);
        dDoSQueryParam.setEndTime(System.currentTimeMillis());
        dDoSAttackService.setStatPeriodPoint(dDoSQueryParam);
        System.out.println(dDoSQueryParam);
    }

    public String getUnit(Double maxSize) {
        if(maxSize == null || maxSize< Long.parseLong("1048576")) {
            return "KB";
        }else if(Long.parseLong("1073741824") > maxSize && maxSize >= Long.parseLong("1048576")) {
            return "M";
        }else if( Long.parseLong("1099511627776") > maxSize && maxSize>= Long.parseLong("1073741824")) {
            return "G";
        }else if(maxSize>= Long.parseLong("1099511627776")) {
            return "T";
        }
        return "KB";
    }

    public Double transferUnit(Double flowValue, String unit) {
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String realSize = "";

        if (flowValue != null) {
            Long ut = Long.parseLong("1024");
            if ("KB".equals(unit)) {
                realSize = decimalFormat.format(flowValue / ut.longValue());
            } else if ("M".equals(unit)) {
                ut = Long.parseLong("1048576");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("G".equals(unit)) {
                ut = Long.parseLong("1073741824");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("T".equals(unit)) {
                ut = Long.parseLong("1099511627776");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else {
                realSize = flowValue + "";
            }
        }
        if (realSize != "") {
            return Double.valueOf(realSize);
        } else {
            return null;
        }
    }

    public List<Map<String,String>> loadExportTask(int pageIndex,int pageSize,Map<String,String> params) {

        try {
            PageHelper.startPage(pageIndex,pageSize);
            List<Map<String,String>> list = dDoSAttackMapper.loadExportTask(params);
            return list;
        } catch (Exception e) {
           logger.error(" ",e);
        }
        return null;
    }

    @Override
    public boolean createExportTask(Map<String, Object> params) {
        try {
            int b =dDoSAttackMapper.createExportTask(params);
            if(b>0){
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error(" ",e);
        }
        return false;
    }

    @Override
    public boolean deleteExportTask(Map<String, Object> params) {
        try {
            int b =dDoSAttackMapper.deleteExportTask(params);
            if(b>0){
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error(" ",e);
        }
        return false;
    }

    @Override
    public void updateDownloadFile(Map<String, String> params) {
        dDoSAttackMapper.updateDownloadFile(params);
    }
}
