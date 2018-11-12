package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.model.AppUserStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/05
 */
@MyBatisDao
public interface AppUserMapper {

    /**
     * 分页查询数据
     * @param queryMap
     * @return
     */
    List<AppUserStrategy> listData(Map<String,Object> queryMap);

    /**
     * 根据messageNos批量删除数据
     * @param messageNos
     * @return
     */
    int batchDelete(List<Long> messageNos);

    /**
     * 根据主键查询记录
     * @param appUserStrategy
     * @return
     */
    AppUserStrategy selectByPrimaryKey(AppUserStrategy appUserStrategy);

    /**
     * 插入数据
     * @param appUserStrategy
     * @return
     */
    int insertSelective(AppUserStrategy appUserStrategy);

    /**
     * 根据主键删除数据
     * @param messageNo
     * @return
     */
    int deleteByPrimaryKey(long messageNo);

    /**
     * 根据主键修改数据
     * @param appUserStrategy
     * @return
     */
    int updateByPrimaryKeySelective(AppUserStrategy appUserStrategy);
}
