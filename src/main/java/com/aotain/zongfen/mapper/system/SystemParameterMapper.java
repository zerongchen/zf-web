package com.aotain.zongfen.mapper.system;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.system.SystemParameter;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
@MyBatisDao
public interface SystemParameterMapper {

    /**
     * 根据条件查询所有的系统参数
     * @param systemParameter
     *      根据systemParameter.configValue   systemParameter.configDesc进行模糊查询
     * @return
     */
    List<SystemParameter> listSystemParameter(SystemParameter systemParameter);

    /**
     * 批量删除数据
     * @param configIds
     * @return
     */
    int batchDelete(List<Integer> configIds);

    /**
     * 新增记录
     * @param systemParameter
     * @return
     */
    int insertSelective(SystemParameter systemParameter);

    /**
     * 根据configKey查询记录是否存在
     * @param configKey
     * @return
     */
    SystemParameter selectByConfigKey(String configKey);

    /**
     * 根据configKey修改记录
     * @param systemParameter
     * @return
     */
    int updateByConfigKey(SystemParameter systemParameter);

    /**
     * 是否存在名称相同的记录
     * @param systemParameter
     * @return
     */
    SystemParameter selectByConfigDesc(SystemParameter systemParameter);

    /**
     * 根据configKey查询记录是否存在
     * @param configId
     * @return
     */
    SystemParameter selectByConfigId(int configId);

}
