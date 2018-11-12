package com.aotain.zongfen.mapper.system;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.zongfen.model.system.OperationLog;
import com.github.abel533.mapper.Mapper;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@MyBatisDao
public interface OperationLogMapper extends Mapper<OperationLog> {

    /**
     * 分页查询操作日志
     * @param page
     * @return
     */
    List<OperationLog> listOperationLog(Page<OperationLog> page);

    /**
     * 根据id批量删除数据
     * @param ids
     * @return
     */
    int batchDelete(List<Integer> ids);
   
}
