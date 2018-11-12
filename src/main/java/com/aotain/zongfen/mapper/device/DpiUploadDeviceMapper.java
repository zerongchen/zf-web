package com.aotain.zongfen.mapper.device;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiUploadDevice;

@MyBatisDao
public interface DpiUploadDeviceMapper {
    int deleteByPrimaryKey(Integer dpiId);

    int insert(DpiUploadDevice record);

    int updateByPrimaryKeySelective(DpiUploadDevice record);
    
    int insertList(List<DpiUploadDevice> list);
    
    /**
     * 
    * @Title: getUplDpiList
    * @Description: 查询数据上报设备
    * @param @param query
    * @param @return
    * @return List<DpiUploadDevice>
    * @throws
     */
    List<DpiUploadDevice> getUplDpiList(Map<String, String> query);
    
    /**
     * 
    * @Title: getUplDpiInfoById
    * @Description: 根据ID查询上报设备信息
    * @param @param dpiId
    * @param @return
    * @return DpiUploadDevice
    * @throws
     */
    DpiUploadDevice getUplDpiInfoById(@Param("dpiId")Integer dpiId);
    /**
     * 获取所有的数据信息,主要用来构建树形节点
     * @return
     */
    List<DpiUploadDevice> selectAllTree();
}