package com.aotain.zongfen.mapper.general;

import java.util.List;
import java.util.Map;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.GenIPAddress;

@MyBatisDao
public interface GenIPAddressMapper {
    int deleteByPrimaryKey(Long ipId);

    int insert(GenIPAddress record);

    int insertSelective(GenIPAddress record);

    GenIPAddress selectByPrimaryKey(Long ipId);

    int updateByPrimaryKeySelective(GenIPAddress record);

    int updateByPrimaryKey(GenIPAddress record);
    
    int insertList(List<GenIPAddress> list);
    
    List<GenIPAddress> getIndexList(Map<String,String> query);
    
    int deleteAll();
    
    List<GenIPAddress> selectIpV4();
    
    List<GenIPAddress> selectIpV6();
}