package com.aotain.zongfen.service.system;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.zongfen.model.system.OperationLog;


import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/28
 */
public interface IOperationLogService {

    /**
     * 分页查询操作日志数据
     * @param page
     * @return
     */
    List<OperationLog> listData(Page<OperationLog> page);

    /**
     * 批量删除
     * @param ids
     */
    int batchDelete(List<Integer> ids);
    
    int addLog(OperationLog record);

}
