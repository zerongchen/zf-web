package com.aotain.zongfen.mapper.device;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiRecHouse;

@MyBatisDao
public interface DpiRecHouseMapper {
    int insert(DpiRecHouse record);

    int insertSelective(DpiRecHouse record);
    
    int insertList(List<DpiRecHouse> list);
    
    /**
     * 
    * @Title: deleteByDpiId
    * @Description: 根据设备ID删除关联的机房
    * @param @param id
    * @param @return
    * @return int
    * @throws
     */
    int deleteByDpiId(Integer id);
    
    /**
     * 
    * @Title: getListByDpiId
    * @Description: 根据设备ID查询关联的机房
    * @param @param id
    * @param @return
    * @return List<String>
    * @throws
     */
    List<String> getListByDpiId(Integer id);
}