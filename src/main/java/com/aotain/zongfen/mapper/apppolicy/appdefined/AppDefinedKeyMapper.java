package com.aotain.zongfen.mapper.apppolicy.appdefined;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.AppDefinedKey;
import com.github.abel533.mapper.Mapper;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/10
 */
@MyBatisDao
public interface AppDefinedKeyMapper extends Mapper<AppDefinedKey> {

    /**
     * 根据quintetId删除数据
     * @param quintetId
     * @return
     */
    int deleteByQuintetId(long quintetId);
}
