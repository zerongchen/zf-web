package com.aotain.zongfen.service.analysis;

import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.zongfen.dto.analysis.AppUserDto;
import com.aotain.zongfen.dto.common.AppUserParamVo;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.mapper.common.MultiSelectMapper;
import com.aotain.zongfen.utils.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppUserAnalysisServiceImpl implements AppUserAnalysisService{

	private static Logger LOG = LoggerFactory.getLogger(AppUserAnalysisServiceImpl.class);

	@Autowired
	private MultiSelectMapper multiSelectMapper;
	
	@Override
	public PageResult<AppUserDto> getIndexListData(AppUserParamVo params) {
		PageResult<AppUserDto> page = new PageResult<AppUserDto>();
		List<AppUserDto> appUer= new ArrayList<AppUserDto>();
		if(params.getStateTime()!=null && !"".equals(params.getStateTime())) {
			params.resetStateTime();
			StringBuffer hsql = new StringBuffer("select stat_time stattime,apptype apptypeid,appid appid,appname appname,"
					+ "COUNT(useraccount) count,area_id areaid ");
			if(params.getDateType()==1) {
				hsql.append(" from job_ubas_userapp_h where dt= '"+params.getStateTime().substring(0, 8)+"' and hour="+params.getStateTime().substring(8));
			}else if(params.getDateType()==2) {
				hsql.append(" from job_ubas_userapp_d where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==3) {
				hsql.append(" from job_ubas_userapp_w where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==4) {
				hsql.append(" from job_ubas_userapp_m where dt= '"+params.getStateTime()+"'");
			}
			if(params.getDateType()==4){
				String time= params.getStateTime();
				time=time.substring(0,6);
				hsql.append(" and probe_type=0 and stat_time = "+time);
			}else{
				hsql.append(" and probe_type=0 and stat_time = "+params.getStateTime());
			}

			if(params.getAreaCode()!=null) {
				hsql.append(" and area_id = '"+params.getAreaCode()+"'");
			}
			if(params.getAppType()!=null) {
				hsql.append(" and apptype = "+params.getAppType());
			}
			if(params.getAppName()!=null) {
				hsql.append(" and appid = "+params.getAppName());
			}
			hsql.append(" group by stat_time,apptype,appid,area_id,stat_time,appname ");
			LOG.info("count sql:"+hsql.toString());
			Long count = HadoopUtils.getCount(HadoopUtils.getConnection(),hsql.toString());
			if(count==null) {
				page.setTotal((long)0);
			}else{
				page.setTotal(count);
			}
			if(params.getOrder()!=null && params.getSort()!=null) {
				hsql.append(" order by count "+params.getOrder()+",apptype asc");
			}else {
				hsql.append(" order by apptype asc ");
			}
			if(params.getPageSize()!=null && params.getPageIndex()!=null) {
				hsql.append(" limit "+params.getPageSize()+" offset "+((params.getPageIndex()-1)*params.getPageSize()));
			}else {
				hsql.append(" limit 10 offset 0 ");
			}
			String sql =hsql.toString();
			System.out.println(sql);
			appUer = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new AppUserDto().getClass());
			if(appUer.size()>0) {
				List<Multiselect> apptypeList = multiSelectMapper.getAppType();
				for(AppUserDto temp:appUer) {
					for(Multiselect temp1:apptypeList) {
						if(temp.getApptypeid()==Integer.valueOf(temp1.getValue())) {
							temp.setApptype(temp1.getTitle());
						}
					}
				}
			}
		}else {
			page.setTotal((long)0);
		}
		page.setRows(appUer);
		return page;
	}

	@Override
	public PageResult<AppUserDto> getDetailListData(AppUserParamVo params) {
		PageResult<AppUserDto> page = new PageResult<AppUserDto>();
		List<AppUserDto> appUer= new ArrayList<AppUserDto>();
		if(params.getStateTime()!=null && !"".equals(params.getStateTime())) {
			params.resetStateTime();
			StringBuffer hsql = new StringBuffer("select usertype usertype,usagecount usagecount,"
					+ "useraccount useraccount ");
			if(params.getDateType()==1) {
				hsql.append(" from job_ubas_userapp_h where dt= '"+params.getStateTime().substring(0, 8)+"' and hour="+params.getStateTime().substring(8));
			}else if(params.getDateType()==2) {
				hsql.append(" from job_ubas_userapp_d where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==3) {
				hsql.append(" from job_ubas_userapp_w where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==4) {
				hsql.append(" from job_ubas_userapp_m where dt= '"+params.getStateTime()+"'");
			}
			hsql.append(" and probe_type=0 and stat_time = "+params.getStateTime());
			if(params.getAreaCode()!=null) {
				hsql.append(" and area_id = '"+params.getAreaCode()+"'");
			}
			if(params.getAppType()!=null) {
				hsql.append(" and apptype = "+params.getAppType());
			}
			if(params.getAppName()!=null) {
				hsql.append(" and appid = "+params.getAppName());
			}
			Long count = HadoopUtils.getCount(HadoopUtils.getConnection(),hsql.toString());
			if(count==null) {
				page.setTotal((long)0);
			}else{
				page.setTotal(count);
			}
			hsql.append(" order by usagecount desc ");
			if(params.getPageSize()!=null && params.getPageIndex()!=null) {
				hsql.append(" limit "+params.getPageSize()*params.getPageIndex()+" offset "+((params.getPageIndex()-1)*params.getPageSize()));
			}else {
				hsql.append(" limit 10 offset 0 ");
			}
			appUer = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new AppUserDto().getClass());
		}else {
			page.setTotal((long)0);
		}
		page.setRows(appUer);
		return page;
	}

	@Override
	public List<AppUserDto> getExportData(AppUserParamVo params) {
		List<AppUserDto> appUer= new ArrayList<AppUserDto>();
		params.resetStateTime();
		StringBuffer hsql = new StringBuffer("select stat_time stattime,apptype apptypeid,appid appid,appname appname,"
				+ "COUNT(useraccount) count,area_id areaid ");
		if(params.getDateType()==1) {
			hsql.append(" from job_ubas_userapp_h where dt= '"+params.getStateTime().substring(0, 8)+"' and hour="+params.getStateTime().substring(8));
		}else if(params.getDateType()==2) {
			hsql.append(" from job_ubas_userapp_d where dt= '"+params.getStateTime()+"'");
		}else if(params.getDateType()==3) {
			hsql.append(" from job_ubas_userapp_w where dt= '"+params.getStateTime()+"'");
		}else if(params.getDateType()==4) {
			hsql.append(" from job_ubas_userapp_m where dt= '"+params.getStateTime()+"'");
		}
		hsql.append(" and probe_type=0 and stat_time = "+params.getStateTime());
		if(params.getAreaCode()!=null) {
			hsql.append(" and area_id = '"+params.getAreaCode()+"'");
		}
		if(params.getAppType()!=null) {
			hsql.append(" and apptype = "+params.getAppType());
		}
		if(params.getAppName()!=null) {
			hsql.append(" and appid = "+params.getAppName());
		}
		hsql.append(" group by stat_time,apptype,appid,area_id,stat_time,appname ");
		if(params.getOrder()!=null && params.getSort()!=null) {
			hsql.append(" order by count "+params.getOrder()+",apptype asc");
		}else {
			hsql.append(" order by apptype asc ");
		}
		appUer = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new AppUserDto().getClass());
		if(appUer.size()>0) {
			List<Multiselect> apptypeList = multiSelectMapper.getAppType();
			for(AppUserDto temp:appUer) {
				for(Multiselect temp1:apptypeList) {
					if(temp.getApptypeid()==Integer.valueOf(temp1.getValue())) {
						temp.setApptype(temp1.getTitle());
					}
				}
			}
		}
		return appUer;
	
	}

	
}
