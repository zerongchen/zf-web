package com.aotain.zongfen.service.analysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aotain.common.utils.hadoop.HadoopUtils;
import com.aotain.zongfen.dto.analysis.WlanDto;
import com.aotain.zongfen.dto.common.WlanParamVo;
import com.aotain.zongfen.mapper.analysis.WlanMapper;
import com.aotain.zongfen.mapper.common.MultiSelectMapper;
import com.aotain.zongfen.utils.PageResult;

@Service
public class WlanServiceImpl implements WlanAnalysisService{
	
    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(IllegalRouteAnalysisServiceImpl.class);
	
	@Autowired
	private MultiSelectMapper multiSelectMapper;
	
	@Autowired
	private WlanMapper wlanMapper;
	
	@Override
	public PageResult<WlanDto> getIndexListData(WlanParamVo params) {
		PageResult<WlanDto> page = new PageResult<WlanDto>();
		List<WlanDto> wlan= new ArrayList<WlanDto>();
		if(params.getStateTime()!=null && !"".equals(params.getStateTime())) {
			params.resetStateTime();
			StringBuffer hsql = new StringBuffer("select stat_time stattime,useraccount useraccount,devicecnt devicecnt,area_id areaid ");
			if(params.getDateType()==2) {
				hsql.append(" from job_ubas_wlan_d where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==3) {
				hsql.append(" from job_ubas_wlan_w where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==4) {
				hsql.append(" from job_ubas_wlan_m where dt= '"+params.getStateTime()+"'");
			}
			if(params.getDateType()==4){
				String stat_time=params.getStateTime();
				stat_time=stat_time.substring(0,6);
				hsql.append(" and probe_type=0 and stat_time = "+stat_time);
			}else{
				hsql.append(" and probe_type=0 and stat_time = "+params.getStateTime());
			}

			if(params.getAreaCode()!=null) {
				hsql.append(" and area_id = '"+params.getAreaCode()+"'");
			}
			if(params.getAccounts()!=null) {
				if(params.getUserGroups()!=null) {
					Map<String,String> query = new HashMap<String, String>();
					query.put("userGroup", params.getUserGroups().toString());
					query.put("userAccount", params.getAccounts());
					if(wlanMapper.getCountUser(query)!=null && wlanMapper.getCountUser(query)>0) {
						hsql.append(" and useraccount like '%"+params.getAccounts()+"%'");
					}else {
						page.setTotal((long)0);
						return page;
					}
				}else {
					hsql.append(" and useraccount like '%"+params.getAccounts()+"%'");
				}
			}else if(params.getAccounts()==null && params.getUserGroups()!=null) {
				List<String> acountList = wlanMapper.getAcountList(params.getUserGroups());
				if(acountList!=null && acountList.size()>0) {
					String tempStr = "";
					for(String str:acountList) {
						if("".equals(tempStr)) {
							tempStr = "'"+str+"'";
						}else {
							tempStr = tempStr+",'"+str+"'";
						}
					}
					hsql.append(" and useraccount in ("+tempStr+")");
				}
			}
			logger.info("count sql:"+hsql.toString());
			Long count = HadoopUtils.getCount(HadoopUtils.getConnection(),hsql.toString());
			if(count==null) {
				page.setTotal((long)0);
			}else{
				page.setTotal(count);
			}
			if(params.getOrder()!=null && params.getSort()!=null) {
				hsql.append(" order by devicecnt "+params.getOrder()+",useraccount asc");
			}else {
				hsql.append(" order by useraccount asc ");
			}
			if(params.getPageSize()!=null && params.getPageIndex()!=null) {
				hsql.append(" limit "+params.getPageSize()+" offset "+((params.getPageIndex()-1)*params.getPageSize()));
			}else {
				hsql.append(" limit 10 offset 0 ");
			}
			logger.info("get data sql:"+hsql.toString());
			wlan = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new WlanDto().getClass());
		}else {
			page.setTotal((long)0);
		}
		page.setRows(wlan);
		return page;
	}

	@Override
	public List<WlanDto> getDetailListData(WlanParamVo params) throws SQLException {
		List<WlanDto> wlan= new ArrayList<WlanDto>();
		if(params.getStateTime()!=null && !"".equals(params.getStateTime())) {
			params.resetStateTime();
			StringBuffer hsql = new StringBuffer("select t2.devicelist ");
			if(params.getDateType()==2) {
				hsql.append(" from job_ubas_wlan_d t2 where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==3) {
				hsql.append(" from job_ubas_wlan_w t2 where dt= '"+params.getStateTime()+"'");
			}else if(params.getDateType()==4) {
				hsql.append(" from job_ubas_wlan_m t2 where dt= '"+params.getStateTime()+"'");
			}
			hsql.append(" and t2.probe_type=0 ");
			if(params.getAreaCode()!=null) {
				hsql.append(" and t2.area_id = '"+params.getAreaCode()+"'");
			}
			hsql.append(" and t2.useraccount = '"+params.getAccounts()+"'");
			List<String> reset = executeQuery(HadoopUtils.getHiveConnection(), hsql.toString());
			for(String str : reset) {
				WlanDto dto = new WlanDto();
				dto.setPhoneNumber(str.split(":")[1]);
				dto.setDevType(str.split(":")[0]);
				wlan.add(dto);
			}
		}
		return wlan;
	}

	public List<String> executeQuery(Connection conn, String sql){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
	            int count = rsmd.getColumnCount();
	            String deviceStr = rs.getString(1);
	            result.addAll(JSONObject.parseArray(deviceStr, String.class));
			}
		} catch (SQLException e) {
			logger.error("",e);
		}finally {
			HadoopUtils.close(ps, rs);
		}
		return result;
	}
	
	@Override
	public List<WlanDto> getExportData(WlanParamVo params) {
		List<WlanDto> wlan= new ArrayList<WlanDto>();
		params.resetStateTime();
		StringBuffer hsql = new StringBuffer("select stat_time stattime,useraccount useraccount,area_id areaid,devicecnt devicecnt ");
		if(params.getDateType()==2) {
			hsql.append(" from job_ubas_wlan_d where dt= '"+params.getStateTime()+"'");
		}else if(params.getDateType()==3) {
			hsql.append(" from job_ubas_wlan_w where dt= '"+params.getStateTime()+"'");
		}else if(params.getDateType()==4) {
			hsql.append(" from job_ubas_wlan_m where dt= '"+params.getStateTime()+"'");
		}
		hsql.append(" and probe_type=0 and stat_time = "+params.getStateTime());
		if(params.getAreaCode()!=null) {
			hsql.append(" and area_id = '"+params.getAreaCode()+"'");
		}
		if(params.getAccounts()!=null) {
			if(params.getUserGroups()!=null) {
				Map<String,String> query = new HashMap<String, String>();
				query.put("userGroup", params.getUserGroups().toString());
				query.put("userAccount", params.getAccounts());
				if(wlanMapper.getCountUser(query)!=null && wlanMapper.getCountUser(query)>0) {
					hsql.append(" and useraccount like '%"+params.getAccounts()+"%'");
				}else {
					return null;
				}
			}else {
				hsql.append(" and useraccount like '%"+params.getAccounts()+"%'");
			}
		}else if(params.getAccounts()==null && params.getUserGroups()!=null) {
			List<String> acountList = wlanMapper.getAcountList(params.getUserGroups());
			if(acountList!=null && acountList.size()>0) {
				String tempStr = "";
				for(String str:acountList) {
					if("".equals(tempStr)) {
						tempStr = "'"+str+"'";
					}else {
						tempStr = tempStr+",'"+str+"'";
					}
				}
				hsql.append(" and useraccount in ("+tempStr+")");
			}
		}
		if(params.getOrder()!=null && params.getSort()!=null) {
			hsql.append(" order by devicecnt "+params.getOrder()+",useraccount asc");
		}else {
			hsql.append(" order by useraccount asc ");
		}
		wlan = HadoopUtils.executeQueryReturnObj(HadoopUtils.getConnection(), hsql.toString(), new WlanDto().getClass());
		return wlan;
	
	}

	
}
