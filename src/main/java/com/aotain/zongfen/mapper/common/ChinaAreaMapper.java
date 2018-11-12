package com.aotain.zongfen.mapper.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.common.ChinaArea;

@MyBatisDao
public interface ChinaAreaMapper {
    int insert(ChinaArea record);

    int insertSelective(ChinaArea record);
    
    ChinaArea getAreaByCode(@Param("areaCode")Long areaCode);
    
    List<ChinaArea> getAreaList(Map<String,Object> query);
    /**
     * 根据父类节点的id把整个树查出来
     * @param parent
     * @return
     */
    List<ChinaArea> getList(@Param("parent")Long parent);
    
    List<ChinaArea> getIndexList(Map<String,Object> query);
    
    int isDuplicateArea(List<ChinaArea> areaList);
    
    int insertList(List<ChinaArea> areaList);
    
    int isSameArea(ChinaArea area);
    
    int update(ChinaArea area);
    
    int deleteByIds(List<Integer> ids);
    
    List<ChinaArea> getProvinceList();
}