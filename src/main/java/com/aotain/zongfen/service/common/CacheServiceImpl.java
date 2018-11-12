package com.aotain.zongfen.service.common;

import com.aotain.zongfen.mapper.common.CacheMapper;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.model.device.ZongFenDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheMapper cacheMapper;

    @Autowired
    private ZongFenDeviceMapper zongFenDeviceMapper;

    @Override
    public String getUserGroupName( Long userId ) {
        return cacheMapper.getUserGroupName(userId);
    }

    @Override
    public ZongFenDevice getZongfenDev( Integer zongfenId ) {
        return zongFenDeviceMapper.getZongfenDevByPrimary(zongfenId);
    }

    @Override
    public String getAppName( Map<String, Integer> map ) {
        return null;
    }

    @Override
    public String getAppTypeName( Integer id ) {
        return cacheMapper.getAppTypeName(id);
    }

    @Override
    public String getWebTypeName(Integer id){
        return cacheMapper.getWebTypeName(id);
    }
}
