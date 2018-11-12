package com.aotain.zongfen.service.common;

import com.aotain.zongfen.model.device.ZongFenDevice;

import java.util.Map;

public interface CacheService {

    String getUserGroupName(Long userId);

    ZongFenDevice getZongfenDev(Integer zongfenId);

    String getAppName(Map<String,Integer> map);

    String getAppTypeName(Integer id);

    String getWebTypeName(Integer id);
}
