package com.aotain.zongfen.service.device;

import com.aotain.zongfen.model.device.IdcHouse;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/02
 */
public interface IdcHouseService {

    /**
     * 查询所有符合条件的idcHouse    未分页
     * @param idcHouse
     * @return
     */
    List<IdcHouse> listIdcHouse(IdcHouse idcHouse);

    /**
     * 查询是否存在此houseId相应的记录
     * @param idcHouse
     * @return
     */
    boolean existRecord(IdcHouse idcHouse);

    /**
     * 新增后者修改记录
     * @param idcHouse
     * @return
     */
    int addOrUpdate(IdcHouse idcHouse);

    /**
     * 根据houseId批量删除记录
     * @param houseIds
     * @return
     */
    int batchDelete(List<String> houseIds);

    /**
     * 是否存在名称一样的记录
     * @param idcHouse
     * @return
     */
    boolean existSameRecord(IdcHouse idcHouse);

    /**
     * 是否存在id或者名称一样的记录
     * @param idcHouse
     * @return
     */
    boolean existSameIdOrNameRecord(IdcHouse idcHouse);
}
