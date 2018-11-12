package com.aotain.zongfen.mapper.apppolicy.appdefined;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.AppDefinedStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/10
 */
@MyBatisDao
public interface AppDefinedMapper {

    /**
     * 插入数据
     * @param appDefinedStrategy
     * @return
     */
    int insertSelective(AppDefinedStrategy appDefinedStrategy);

    /**
     * 更新数据
     * @param appDefinedStrategy
     * @return
     */
    int updateSelective(AppDefinedStrategy appDefinedStrategy);

    /**
     * 查询数据
     * @param queryMap
     * @return
     */
    List<AppDefinedStrategy> listData(Map<String,Object> queryMap);

    /**
     * 根据主键查询记录
     * @param definedId
     * @return
     */
    AppDefinedStrategy selectByPrimaryKey(long definedId);

    /**
     * 根据主键删除记录
     * @param appDefinedStrategy
     * @return
     */
    int deleteData(AppDefinedStrategy appDefinedStrategy);
}
