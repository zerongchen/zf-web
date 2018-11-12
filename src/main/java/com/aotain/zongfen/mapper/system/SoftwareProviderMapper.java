package com.aotain.zongfen.mapper.system;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.zongfen.model.system.OperationLog;
import com.github.abel533.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 */
@MyBatisDao
public interface SoftwareProviderMapper {


    List<Map<String,String>> getInitTable(Map<String, String> params);


    List<Map<String,String>> getSoftwareProvider();

    int addProvider(List<Map<String, String>> pMapList);

    void updateProvider(Map<String, String> pMap);

    void deleteProvider(@Param("providerShort") String providerShort);
}
