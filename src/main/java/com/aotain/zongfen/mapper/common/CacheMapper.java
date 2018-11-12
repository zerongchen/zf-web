package com.aotain.zongfen.mapper.common;

import com.aotain.common.config.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;

@MyBatisDao
public interface CacheMapper {

    @Cacheable(cacheNames = "userGroupCache",key = "#p0.toString()")
    String getUserGroupName( @Param("id") Long id);

    String getAppName( Map<String ,Integer> map);

    String getAppTypeName(Integer appType);

    @Cacheable(cacheNames = "dictCache",key = "#p0.toString()")
    String getWebTypeName(Integer webType);

    int getSiteTypeByName(Map<String,Object> queryMap);
}
