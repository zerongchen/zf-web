package com.aotain.zongfen.mapper.device;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.DpiDeviceInfoStrategy;

@MyBatisDao
public interface DPIRecDeviceMapper {
    int deleteByPrimaryKey(Integer dpiId);

    int insert(DpiDeviceInfoStrategy record);

    int insertSelective(DpiDeviceInfoStrategy record);

    DpiDeviceInfoStrategy selectByPrimaryKey(Integer dpiId);

    int updateByPrimaryKeySelective(DpiDeviceInfoStrategy record);

    int updateByPrimaryKey(DpiDeviceInfoStrategy record);
    
    /**
     * 
    * @Title: getSamIPCount
    * @Description: 相同ip地址的设备个数 
    * @param @param queryIp
    * @param @return
    * @return int
    * @throws
     */
    int getSamIPCount(Map<String,Object> queryIp);
    
    /**
     * 
    * @Title: getSamNameCount
    * @Description: 存在相同名称的设备的数量
    * @param @param query
    * @param @return
    * @return int
    * @throws
     */
    int getSamNameCount(Map<String,Object> query);
    
    /**
     * 
    * @Title: getInfoByName
    * @Description: 根据设备名称查询DPI接收数据设备
    * @param @param deviceName
    * @param @return
    * @return List<DpiDeviceInfoStrategy>
    * @throws
     */
    List<DpiDeviceInfoStrategy> getInfoByName(@Param("deviceName")String deviceName);
    
    /**
     * 
    * @Title: getInfoById
    * @Description: 根据设备ID查询DPI接收数据设备
    * @param @param deviceId
    * @param @return
    * @return DpiDeviceInfoStrategy
    * @throws
     */
    DpiDeviceInfoStrategy getInfoById(Integer deviceId);
}