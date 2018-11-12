package com.aotain.zongfen.service.device.impl;

import com.aotain.zongfen.mapper.device.IdcHouseMapper;
import com.aotain.zongfen.model.device.IdcHouse;
import com.aotain.zongfen.service.device.IdcHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/02
 */
@Service
public class IdcHouseServiceImpl implements IdcHouseService{

    @Autowired
    private IdcHouseMapper idcHouseMapper;

    @Override
    public List<IdcHouse> listIdcHouse(IdcHouse idcHouse){
        return idcHouseMapper.getConditionIdcHouseList(idcHouse);
    }

    @Override
    public boolean existRecord(IdcHouse idcHouse) {
        IdcHouse result = idcHouseMapper.selectByPrimaryKey(idcHouse.getHouseId());
        return result==null?false:true;
    }

    @Override
    public int addOrUpdate(IdcHouse idcHouse) {
        int updateRecord = 0;
        if (existRecord(idcHouse)){
            updateRecord = idcHouseMapper.updateByPrimaryKey(idcHouse);
        } else {
            updateRecord = idcHouseMapper.insertSelective(idcHouse);
        }
        return updateRecord;
    }

    @Override
    public int batchDelete(List<String> houseIds) {
        return idcHouseMapper.batchDelete(houseIds);
    }

    @Override
    public boolean existSameRecord(IdcHouse idcHouse){

        return idcHouseMapper.selectByName(idcHouse)==null?false:true;
    }

    @Override
    public boolean existSameIdOrNameRecord(IdcHouse idcHouse){
        return idcHouseMapper.selectByIdOrName(idcHouse)==null?false:true;
    }
}
