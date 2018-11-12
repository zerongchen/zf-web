package com.aotain.zongfen.mapper.device;


import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.utils.PageResult;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface DpiUpgradeMapper {

    List<Map<String,String>> getDpiUpgrade(Map<String, String> params);

    List<Map<String,String>> getDpiUpgradePort(Map<String, String> params);

    List<Map<String,String>> getDpiUpgradePortDetail(Map<String, String> params);
}
