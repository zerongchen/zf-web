package com.aotain.zongfen.service.system;

import com.aotain.zongfen.model.system.SystemParameter;

import java.util.List;

/**
 * 系统参数服务接口类
 *
 * @author daiyh@aotain.com
 * @date 2018/02/26
 */
public interface IParameterSettingService {

    /**
     * 根据条件查询所有的系统参数
     * @param systemParameter
     *      根据systemParameter.configValue   systemParameter.configDesc进行模糊查询
     * @return
     */
    List<SystemParameter> listSystemParameter(SystemParameter systemParameter);

    /**
     * 批量删除数据
     * @param configId
     * @return
     */
    int batchDelete(List<Integer> configId);

    /**
     * 是否存在特征值对应的记录
     * @param systemParameter
     * @return
     */
    boolean existRecord(SystemParameter systemParameter);

    /**
     * 新增或修改记录
     * @param systemParameter
     * @return
     */
    void addOrUpdate(SystemParameter systemParameter);

    /**
     * 是否存在名称相同的记录
     * @param systemParameter
     * @return
     */
    boolean existSameNameRecord(SystemParameter systemParameter);

    /**
     * 根据configKey查询对应的Id
     * @param configKey
     * @return
     */
    SystemParameter selectByConfigKey(String configKey);

}
