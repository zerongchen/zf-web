package com.aotain.zongfen.service.monitor.impl;

import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import com.aotain.zongfen.mapper.monitor.DataUploadDetailMonitorMapper;
import com.aotain.zongfen.service.monitor.DataUploadDetailMonitorService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

@Service
public class DataUploadDetailMonitorServiceImpl implements DataUploadDetailMonitorService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DataUploadDetailMonitorMapper dataUploadDetailMonitorMapper;
	
	@Override
	public PageResult<DataUploadDetailMonitorDTO> getList(Integer pageIndex, Integer pageSize,
			DataUploadDetailMonitorDTO dto) {
		PageResult<DataUploadDetailMonitorDTO> result = new PageResult<DataUploadDetailMonitorDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		initTime(dto);

		if(dto.getDetailType()==1) {
			List<DataUploadDetailMonitorDTO> list = dataUploadDetailMonitorMapper.selectList(dto);
			PageInfo<DataUploadDetailMonitorDTO> pageResult = new PageInfo<DataUploadDetailMonitorDTO>(list);
			result.setTotal(pageResult.getTotal());
			result.setRows(list);
			return result;
		
		}else {
			List<DataUploadDetailMonitorDTO> list = dataUploadDetailMonitorMapper.selectWarnList(dto);
			PageInfo<DataUploadDetailMonitorDTO> pageResult = new PageInfo<DataUploadDetailMonitorDTO>(list);
			result.setTotal(pageResult.getTotal());
			result.setRows(list);
			return result;
			
		}
	

	}
	

	private void initTime(DataUploadDetailMonitorDTO dto){
		if(!StringUtils.isEmpty(dto.getStatTime())){
			switch (dto.getDateType()){
				case 2://h
					try {
						Calendar cal = DateUtils.toCalendar(DateUtils.parseDate(dto.getStatTime(),DateFormatConstant.DATETIME_WITHOUT_HOUR));
						dto.setStartTime(cal.getTimeInMillis()/1000);
						dto.setEndTime(cal.getTimeInMillis()/1000+3600);
						break;
					} catch (ParseException e) {
						logger.error("parse state Time error"+e);
					}
				case 3:
					try {
					Calendar cal = DateUtils.toCalendar(DateUtils.parseDate(dto.getStatTime(),DateFormatConstant.DATE_CHS_HYPHEN));
					dto.setStartTime(cal.getTimeInMillis()/1000);
					dto.setEndTime(cal.getTimeInMillis()/1000+3600*24);
					break;
				} catch (ParseException e) {
					logger.error("parse state Time error"+e);
				}
			}
		}
	}
}
