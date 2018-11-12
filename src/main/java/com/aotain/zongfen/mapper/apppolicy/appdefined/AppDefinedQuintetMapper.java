package com.aotain.zongfen.mapper.apppolicy.appdefined;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.AppDefinedQuintet;
import com.github.abel533.mapper.Mapper;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/10
 */
@MyBatisDao
public interface AppDefinedQuintetMapper  {
    /**
     * 插入数据
     * @param appDefinedQuintet
     * @return
     */
    int insertSelective(AppDefinedQuintet appDefinedQuintet);

    /**
     * 根据definedId删除数据
     * @param definedId
     * @return
     */
    int deleteByDefinedId(Long definedId);

    /**
     * 根据definedId查询所有的多元组记录
     * @param definedId
     * @return
     */
    List<AppDefinedQuintet> selectByDefinedId(Long definedId);
}
