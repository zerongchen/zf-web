package com.aotain.zongfen.service.system.impl;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.zongfen.mapper.system.OperationLogMapper;
import com.aotain.zongfen.model.system.OperationLog;
import com.aotain.zongfen.service.system.IOperationLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/28
 */
@Service
public class OperationLogServiceImpl implements IOperationLogService{

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> listData(Page<OperationLog> page){
        return operationLogMapper.listOperationLog(page);
    }

    @Override
    public int batchDelete(List<Integer> ids){
        return operationLogMapper.batchDelete(ids);
    }

	@Override
	public int addLog(OperationLog record) {
		return operationLogMapper.insert(record);
	}
}
