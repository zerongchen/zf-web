package com.aotain.zongfen.mapper.analysis;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.common.utils.impala.ImpalaUtil;
import com.aotain.zongfen.dto.analysis.ShareKWDTO;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class ShareKWDao {

    private Logger logger = LoggerFactory.getLogger(ShareKWDao.class);

    public List<ShareKWDTO> getList( ShareKWDTO dto){

        String fromSql = createFromSql(dto).toString();
        if (StringUtils.isEmpty(fromSql)) return null;
        String fieldSql = createFieldSql(dto);
        String sort = createSort(dto);
        StringBuilder sql = new StringBuilder();
        sql.append(fieldSql);
        sql.append(fromSql);
        sql.append(sort);
        Connection conn = HadoopUtils.getConnection();
        int count = 0;
        logger.debug("count sql:  select count(1) " + fromSql );
        count = HadoopUtils.count(fromSql, conn); //统计总记录数
        logger.debug("App count[" + count + "]");
        if(count>0) {
//            sql.append(" limit " + dto.getPageSize()).append(" offset " + dto.getPage());
            List<ShareKWDTO> resultList = HadoopUtils.executeQueryReturnObj(conn, sql.toString(), ShareKWDTO.class);
            return resultList;
        }else {
            return new ArrayList<ShareKWDTO>();
        }
    }

    public PageResult<ShareKWDTO> getData( ShareKWDTO dto){

        String fromSql = createFromSql(dto).toString();
        if (StringUtils.isEmpty(fromSql)) return null;
        String fieldSql = createFieldSql(dto);
        String sort = createSort(dto);
        StringBuilder sql = new StringBuilder();
        sql.append(fieldSql);
        sql.append(fromSql);
        sql.append(sort);
        Connection conn = HadoopUtils.getConnection();
        int count = 0;
        logger.debug("count sql:  select count(1) " + fromSql );
        count = HadoopUtils.count(fromSql, conn); //统计总记录数
        logger.debug("App count[" + count + "]");
        if(count>0) {
            sql.append(" limit " + dto.getPageSize()).append(" offset " + (dto.getPage()-1)*dto.getPageSize());
            logger.debug("sql is "+sql.toString());
            List<ShareKWDTO> resultList = HadoopUtils.executeQueryReturnObj(conn, sql.toString(), ShareKWDTO.class);
            PageResult<ShareKWDTO> result = new PageResult<ShareKWDTO>((long) count, resultList);
            HadoopUtils.close(conn);
            return result;
        }else {
            HadoopUtils.close(conn);
            return new PageResult<ShareKWDTO>(0l,new ArrayList<>());
        }
    }

    private String createSort( ShareKWDTO dto ) {
        StringBuilder sql = new StringBuilder("");
        if(dto.getSort() != null){
            if(dto.getOrder() != null){
                sql.append(" ORDER BY " + dto.getSort() + " " + dto.getOrder());
            }else
                sql.append(" ORDER BY " + dto.getSort() + " DESC ");
        }else {
                sql.append(" ORDER BY hostcnt DESC ");
        }
        return sql.toString();
    }

    private String createFieldSql( ShareKWDTO dto ) {

        StringBuilder sb=new StringBuilder();
        sb.append("select useraccount,userip,hostcnt," +
                "qqnocnt, " +
                "natipcnt,"+
                "cookiecnt, " +
                "devnamecnt," +
                "osnamecnt," +
                "probe_type as probetype," +
                "area_id as areaid ");
        return sb.toString();
    }

    private StringBuilder createFromSql( ShareKWDTO dto ) {
        String tableName="";
        if(dto.getDateType()==2){
            tableName="job_ubas_share_kw_d";
        }else if(dto.getDateType()==3){
            tableName="job_ubas_share_kw_w";
        }else if(dto.getDateType()==4){
            tableName="job_ubas_share_kw_m";
        }
        tableName= LocalConfig.getInstance().getHashValueByHashKey("hive.database")+"."+tableName;

        StringBuilder sb = new StringBuilder();
        sb.append(" from "+tableName + " where 1=1 ");
        if(!StringUtils.isEmpty(dto.getAreaid())){
            sb.append(" and area_id='"+dto.getAreaid()+"'");
        }
        if(dto.getProbetype()!=null){
            sb.append(" and probe_type="+dto.getProbetype()+"");
        }
        if(!StringUtils.isEmpty(dto.getUseraccount())){
            sb.append(" and useraccount like '%"+dto.getUseraccount()+"%'");
        }
        if(!StringUtils.isEmpty(dto.getUserip())){
            sb.append(" and userip like '%"+dto.getUserip()+"%'");
        }
        String date = resetDate(dto);
        if(!StringUtils.isEmpty(date)){
            sb.append(" and stat_time="+date);
        }else {
            return null;
        }
        sb.append(" and dt in (");
        sb.append(ImpalaUtil.getColumnPartDate(dto.getDateType()+1, dto.getStateTime(),null));
        sb.append(")");
//        sb.append(" GROUP BY useraccount,userip,probe_type,area_id ");


        return sb;
    }


    private String resetDate(ShareKWDTO kwdto){


        if(!StringUtils.isEmpty(kwdto.getStateTime())){
            if(kwdto.getDateType()==2){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date =null;
                try {
                    date = format.parse(kwdto.getStateTime());
                } catch (ParseException e) {
                    logger.error("parse date error ",e);
                    return null;
                }
                return DateUtils.formatDateyyyyMMdd(date);
            }else if(kwdto.getDateType()==3){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date =null;
                try {
                    date = format.parse(kwdto.getStateTime());
                } catch (ParseException e) {
                    logger.error("parse date error ",e);
                    return null;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
                if(1 == dayWeek) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                }
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
                return DateUtils.formatDateyyyyMMdd(cal.getTime()); //周一时间
            }else if(kwdto.getDateType()==4){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
                Date date =null;
                try {
                    date = format.parse(kwdto.getStateTime());
                } catch (ParseException e) {
                    logger.error("parse date error ",e);
                    return null;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
                return simpleDateFormat.format(date);
            }
        }
        return null;
    }
}
