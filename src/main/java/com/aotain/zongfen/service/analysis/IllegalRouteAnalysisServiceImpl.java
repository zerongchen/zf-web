package com.aotain.zongfen.service.analysis;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aotain.zongfen.utils.ColorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.zongfen.dto.analysis.IllegalRouteListDto;
import com.aotain.zongfen.dto.common.IllegalRouteParamVo;
import com.aotain.zongfen.mapper.analysis.IllegalRouteMapper;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

@Service
public class IllegalRouteAnalysisServiceImpl implements IllegalRouteAnalysisService{
	
    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(IllegalRouteAnalysisServiceImpl.class);
	
	@Autowired
	private IllegalRouteMapper illegalRouteMapper;
	
	@Override
	public PageResult<IllegalRouteListDto> getIndexListData(IllegalRouteParamVo params) {
		PageResult<IllegalRouteListDto> page = new PageResult<IllegalRouteListDto>();
		List<IllegalRouteListDto> illegalRoute= new ArrayList<IllegalRouteListDto>();
		if(params.getStateTime()!=null && !"".equals(params.getStateTime())) {
		params.resetStateTime();
		dateSet(params);
		StringBuffer hsql = new StringBuffer("select stat_time stattime,sum(nodeintraffic) inputflow,sum(nodeouttraffic) outputflow,nodeip nodeip ");
		if(params.getDateType()==2) {
			hsql.append(" from job_ubas_illegalroutes_d where dt in ("+getDateStr(params)+")");
		}else if(params.getDateType()==3) {
			hsql.append(" from job_ubas_illegalroutes_w where dt in ("+getDateStr(params)+")");
		}else if(params.getDateType()==4) {
			hsql.append(" from job_ubas_illegalroutes_m where dt in ("+getDateStr(params)+")");
		}
		hsql.append(" and probe_type=0 ");
		if(params.getAreaCode()!=null) {
			hsql.append(" and area_id = '"+params.getAreaCode()+"'");
		}
		if(params.getCarrieroperators()!=null && !"".equals(params.getCarrieroperators())) {
			hsql.append(" and cp = '"+params.getCarrieroperators()+"'");
		}
		hsql.append(" group by stat_time,nodeip ");
		Long count = HadoopUtils.getCount(HadoopUtils.getConnection(),hsql.toString());
		Map<String,Long> maxs = executeQuery(HadoopUtils.getConnection(), " select max(t.inputflow),max(t.outputflow) from ("+hsql.toString()+") t ");
		if(count==null) {
			page.setTotal((long)0);
		}else{
			page.setTotal(count);
		}
		if(params.getOrder()!=null && params.getSort()!=null) {
			hsql.append(" order by "+params.getSort().substring(0, params.getSort().length()-1)+" "+params.getOrder());
		}else {
			hsql.append(" order by stat_time desc ");
		}
		if(params.getPageSize()!=null && params.getPageIndex()!=null) {
			hsql.append(" limit "+params.getPageSize()+" offset "+((params.getPageIndex()-1)*params.getPageSize()));
		}else {
			hsql.append(" limit 10 offset 0 ");
		}
		illegalRoute = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new IllegalRouteListDto().getClass());
		transferUnitTable(illegalRoute,getUnit(maxs.get("output")),getUnit(maxs.get("input")));
		}else {
			page.setTotal((long)0);
		}
		page.setRows(illegalRoute);
		return page;
	}
	
	public Map<String,Long> executeQuery(Connection conn, String sql){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String,Long> max = new HashMap<String,Long>();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			max.put("input", rs.getLong(1));
			max.put("output", rs.getLong(2));
		} catch (SQLException e) {
			logger.error("",e);
		}finally {
			HadoopUtils.close(ps, rs);
		}
		return max;
	}
	
	public void transferUnitTable(List<IllegalRouteListDto> list,String unit,String units){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(IllegalRouteListDto dto : list) {
				if(dto.getOutputflow() != null) {
					if( "KB".equals(unit)) {
						dto.setOutputflows(decimalFormat.format(dto.getOutputflow()/Long.parseLong("1024"))+"KB");
					}else if( "M".equals(unit)) {
						dto.setOutputflows(decimalFormat.format(((double)dto.getOutputflow()/Long.parseLong("1048576")))+"M");
					}else if( "G".equals(unit)) {
						dto.setOutputflows(decimalFormat.format(((double)dto.getOutputflow()/Long.parseLong("1073741824")))+"G");
					}else if( "T".equals(unit)) {
						dto.setOutputflows(decimalFormat.format(((double)dto.getOutputflow()/Long.parseLong("1099511627776")))+"T");
					}
				}
				if(dto.getInputflow() != null) {
					if( "KB".equals(units)) {
						dto.setInputflows(decimalFormat.format((double)dto.getInputflow()/Long.parseLong("1024"))+"KB");
					}else if( "M".equals(units)) {
						dto.setInputflows(decimalFormat.format(((double)dto.getInputflow()/Long.parseLong("1048576")))+"M");
					}else if( "G".equals(units)) {
						dto.setInputflows(decimalFormat.format(((double)dto.getInputflow()/Long.parseLong("1073741824")))+"G");
					}else if( "T".equals(units)) {
						dto.setInputflows(decimalFormat.format(((double)dto.getInputflow()/Long.parseLong("1099511627776")))+"T");
					}
				}
			}
		}
	}

	@Override
	public List<IllegalRouteListDto> getExportData(IllegalRouteParamVo params) throws ParseException {
		List<IllegalRouteListDto> illegalRoute= new ArrayList<IllegalRouteListDto>();
		params.resetStateTime();
		StringBuffer hsql = new StringBuffer("select stat_time stattime,sum(nodeintraffic) inputflow,sum(nodeouttraffic) outputflow,nodeip nodeip ");
		if(params.getDateType()==2) {
			hsql.append(" from job_ubas_illegalroutes_d where dt in ("+getDateStr(params)+")");
		}else if(params.getDateType()==3) {
			hsql.append(" from job_ubas_illegalroutes_w where dt in ("+getDateStr(params)+")");
		}else if(params.getDateType()==4) {
			hsql.append(" from job_ubas_illegalroutes_m where dt in ("+getDateStr(params)+")");
		}
		hsql.append(" and probe_type=0 ");
		if(params.getAreaCode()!=null) {
			hsql.append(" and area_id = '"+params.getAreaCode()+"'");
		}
		if(params.getCarrieroperators()!=null && !"".equals(params.getCarrieroperators())) {
			hsql.append(" and cp = '"+params.getCarrieroperators()+"'");
		}
		hsql.append(" group by stat_time,nodeip ");
		if(params.getOrder()!=null && params.getSort()!=null) {
			hsql.append(" order by "+params.getSort().substring(0, params.getSort().length()-1)+" "+params.getOrder());
		}else {
			hsql.append(" order by stat_time desc ");
		}
		illegalRoute = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new IllegalRouteListDto().getClass());
		for(IllegalRouteListDto temp:illegalRoute) {
			temp.setStateTime(DateUtils.formatDateDB(DateUtils.parseDateSql(temp.getStattime().toString(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG)));
		}
		return illegalRoute;
	}
	
	public String getDateStr(IllegalRouteParamVo params) {
		String dateStr = "";
        try {
            switch (params.getDateType()){
                case 2:
                    Calendar cal2 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    for(int i=1;i<7;i++) {
                    	if("".equals(dateStr)) {
                    		dateStr="'"+DateUtils.formatDateyyyyMMdd(cal2.getTime())+"'";
                    	}else {
                    		dateStr = dateStr +",'"+DateUtils.formatDateyyyyMMdd(cal2.getTime())+"'";
                    	}
                    	cal2.add(Calendar.DATE, -1);
                    }
                    break;
                case 3:
                    Calendar cal3 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    params.setEndTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal3.getTime())));
                    for(int i=1;i<7;i++) {
                    	if("".equals(dateStr)) {
                    		dateStr="'"+DateUtils.formatDateyyyyMMdd(cal3.getTime())+"'";
                    	}else {
                    		dateStr = dateStr +",'"+DateUtils.formatDateyyyyMMdd(cal3.getTime())+"'";
                    	}
                    	cal3.add(Calendar.DATE, -7);
                    }
                    break;
                case 4:
                	Calendar cal = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    for(int i=1;i<7;i++) {
                    	if("".equals(dateStr)) {
                    		dateStr="'"+DateUtils.formatDateyyyyMMdd(cal.getTime())+"'";
                    	}else {
                    		dateStr = dateStr +",'"+DateUtils.formatDateyyyyMMdd(cal.getTime())+"'";
                    	}
                    	cal.add(Calendar.MONTH, -1);
                    }
                    break;
            }
        }catch (Exception e){
            logger.error("resetStateTime error"+e);
        }
		return dateStr;
	}

	@Override
	public ECharts<Series> getChart(IllegalRouteParamVo params) {
		List<String> legend = new ArrayList<String>();
		//横坐标
		List<String> xAxis = new ArrayList<String>();
		Set<String> timeSet = new LinkedHashSet<String>();
		//数据集合
		List<Series> seriesList = new ArrayList<Series>();
		params.resetStateTime();
		dateSet(params);
		//流入、流出总流量
		List<IllegalRouteListDto> list1 = illegalRouteMapper.getDataList(params);
		if(list1!=null && list1.size()>0) {
			Long  maxSize1 = illegalRouteMapper.getMaxSize(params); 
			List<Double> inList = new ArrayList<Double>();
			List<Double> outList = new ArrayList<Double>();
			List<String> axis = new ArrayList<String>();
			for(IllegalRouteListDto dto : list1) {
				inList.add(dto.getInputflowD());
				outList.add(dto.getOutputflowD());
				try {
					timeSet.add(DateUtils.formatDateDB(DateUtils.parseDateSql(dto.getStattime().toString(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG)));
				} catch (ParseException e) {
					logger.error("date transfer fail",e);
				}
				axis.add(dto.getStattime().toString());
			}
			inList=transferUnit(inList,getUnit(maxSize1));
			legend.add("流入总流量");
			seriesList.add(new Series(legend.get(0), "line",inList));
			outList=transferUnit(outList,getUnit(maxSize1));
			legend.add("流出总流量");
			seriesList.add(new Series(legend.get(1), "line",outList));
			
			//归属运营商
			List<String> cpList = new ArrayList<String>();
			if(params.getCarrieroperators()==null || "".equals(params.getCarrieroperators())) {
				cpList = illegalRouteMapper.getCarrieroperators();
			}else {
				cpList.add(params.getCarrieroperators());
			}
			
			Long maxSize2 = illegalRouteMapper.getAllDataMaxSize(params);
			List<IllegalRouteListDto> list2 = illegalRouteMapper.getAllDataList(params);
			int i = 2;
			boolean flag = true;
			for(String temp : cpList) {
				params.setCloum(temp);
				legend.add(temp);
				List<Double> tmpList = new ArrayList<Double>();
				
				for(String str:axis) {
					for(IllegalRouteListDto sto2:list2) {
						if(str.equals(sto2.getStattime().toString())&&temp.equals(sto2.getCp())) {
							tmpList.add(sto2.getOutputflowD());
							flag = false;
						}
					}
					if(flag==true) {
						tmpList.add((double)0);
					}
					flag = true;
				}
				tmpList=transferUnit(tmpList,getUnit(maxSize2));

				Map<String,Object> itemStyle=new HashMap<>();
				Map<String,String> color=new HashMap<>();
				color.put("color", ColorUtils.colorArr[i]);
				itemStyle.put("normal",color);

				seriesList.add(new Series(legend.get(i), "bar",tmpList,1,itemStyle));
				i++;
			}
			xAxis.addAll(timeSet);
			
			DataZoom dataRoom = new DataZoom();
			dataRoom.setShow(true);
			dataRoom.setZoomLock(false);
			int start = 0;
			int end = 100;
			dataRoom.setStart(start);
			dataRoom.setEnd(end);
			
			ECharts<Series> echarts = new ECharts<Series>(legend,xAxis, seriesList, dataRoom);
			echarts.setUnit(getUnit(maxSize1)+"|"+getUnit(maxSize2));
			return echarts;
		}else {
			DataZoom dataRoom = new DataZoom();
			dataRoom.setShow(true);
			dataRoom.setZoomLock(false);
			int start = 0;
			int end = 100;
			dataRoom.setStart(start);
			dataRoom.setEnd(end);
			ECharts<Series> echarts = new ECharts<Series>(legend,xAxis, seriesList, dataRoom);
			echarts.setUnit(" | ");
			return echarts;
		}
		
		
	}
	
	public void dateSet(IllegalRouteParamVo params) {
        try {
            switch (params.getDateType()){
                case 2:
                    Calendar cal2 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    params.setEndTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal2.getTime())));
                    cal2.add(Calendar.DATE, -6);
                    params.setStartTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal2.getTime())));
                    break;
                case 3:
                    Calendar cal3 = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    params.setEndTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal3.getTime())));
                    cal3.add(Calendar.DATE, -7);
                    params.setStartTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal3.getTime())));
                    break;
                case 4:
                	Calendar cal = DateUtils.toCalendar(DateUtils.parseDate(params.getStateTime(),DateFormatConstant.DATE_WITHOUT_SEPARATOR_LONG));
                    cal.set(Calendar.DAY_OF_MONTH,1);
                    params.setEndTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal.getTime())));
                    cal.add(Calendar.MONTH, -7);
                    params.setStartTime(Integer.valueOf(DateUtils.formatDateyyyyMMdd(cal.getTime())));
                    break;
            }
        }catch (Exception e){
            logger.error("resetStateTime error"+e);
        }
    }
	
	public String getUnit(Long maxSize) {
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
	
	public List<Double> transferUnit(List<Double> list,String unit){
		List<Double> result = new ArrayList<Double>();
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");  
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			String realSize = "";
			for(Double dto : list) {
				if(dto != null) {
					if( "KB".equals(unit)) {
						realSize = decimalFormat.format(dto/Long.parseLong("1024"));
					}else if( "M".equals(unit)) {
						realSize = decimalFormat.format((dto/Long.parseLong("1048576")));
					}else if( "G".equals(unit)) {
						realSize = decimalFormat.format((dto/Long.parseLong("1073741824")));
					}else if( "T".equals(unit)) {
						realSize = decimalFormat.format((dto/Long.parseLong("1099511627776")));
					}
				}
				result.add(Double.valueOf(realSize));
			}
		}
		return result;
	}
	
}
