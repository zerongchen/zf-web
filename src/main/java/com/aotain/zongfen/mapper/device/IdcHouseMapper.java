package com.aotain.zongfen.mapper.device;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.IdcHouse;

@MyBatisDao
public interface IdcHouseMapper {
    int deleteByPrimaryKey(String houseId);

    int insert(IdcHouse record);

    int insertSelective(IdcHouse record);

    IdcHouse selectByPrimaryKey(String houseId);

    int updateByPrimaryKeySelective(IdcHouse record);

    int updateByPrimaryKey(IdcHouse record);
    
    List<IdcHouse> getIdcHouseList();
    
    List<String> getIdcHouseByIds(String[] array);

    /**
     * 根据条件查询所有的机房信息
     * @param idcHouse
     * @return
     */
    List<IdcHouse> getConditionIdcHouseList(IdcHouse idcHouse);

    /**
     * 根据houseId批量删除数据
     * @param houseIds
     * @return
     */
    int batchDelete(List<String> houseIds);

    /**
     * 是否存在名称一样的记录
     * @param idcHouse
     * @return
     */
    IdcHouse selectByName(IdcHouse idcHouse);

    /**
     * 是否存在id或者名称一样的记录
     * @param idcHouse
     * @return
     */
    IdcHouse selectByIdOrName(IdcHouse idcHouse);
}