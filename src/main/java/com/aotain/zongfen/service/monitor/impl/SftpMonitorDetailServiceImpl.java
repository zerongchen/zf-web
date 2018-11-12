package com.aotain.zongfen.service.monitor.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.dto.monitor.SftpMonitorDetailDTO;
import com.aotain.zongfen.mapper.monitor.SftpMonitorDetailMapper;
import com.aotain.zongfen.service.monitor.SftpMonitorDetailService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class SftpMonitorDetailServiceImpl implements SftpMonitorDetailService {

	private static Logger LOG = LoggerFactory.getLogger(SftpMonitorDetailServiceImpl.class);

	@Autowired
	private SftpMonitorDetailMapper sftpMonitorDetailMapper;
	
	@Override
	public PageResult<SftpMonitorDetailDTO> getList(Integer pageIndex, Integer pageSize, SftpMonitorDetailDTO dto) {
		PageResult<SftpMonitorDetailDTO> result = new PageResult<SftpMonitorDetailDTO>();
		PageHelper.startPage(pageIndex, pageSize);
		List<SftpMonitorDetailDTO> list = null;
		try {
			list = sftpMonitorDetailMapper.selectList(dto);
		} catch (Exception e) {
			LOG.error("",e);
		}
		transferUnitTable(list,"");
		PageInfo<SftpMonitorDetailDTO> pageResult = new PageInfo<SftpMonitorDetailDTO>(list);
		result.setTotal(pageResult.getTotal());
		result.setRows(list);
		return result;
	}

	public void transferUnitTable(List<SftpMonitorDetailDTO> list,String unit){
		if(list.size()>0) {
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			for(SftpMonitorDetailDTO dto : list) {
				if(dto.getFileSize() != null) {
					if( dto.getFileSize()>Long.parseLong("1099511627776")) {
						dto.setFileSizeStr(decimalFormat.format((dto.getFileSize()/Long.parseLong("1099511627776")))+"T");
					}
					else if( dto.getFileSize()>Long.parseLong("1024")) {
						dto.setFileSizeStr(decimalFormat.format(dto.getFileSize()/Long.parseLong("1024")));
					}
				}
			}
		}
	}
}
