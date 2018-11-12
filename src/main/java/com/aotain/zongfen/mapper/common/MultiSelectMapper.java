package com.aotain.zongfen.mapper.common;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.common.Multiselect;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface MultiSelectMapper {

    List<Multiselect> getWSOption();
    List<Multiselect> getKWOption();
    List<Multiselect> getWLOption();

    List<Multiselect> getAppType();
    List<Multiselect> getAppByType(@Param("appType") Integer appType);
    List<Multiselect> getUserGroup();

    List<Multiselect> getClassFileInfo(@Param("classType") Integer classType);

    List<Multiselect> getZongfenDev( Map<String,Object> map);

    List<Multiselect> getWebType();
    
    List<Multiselect> selectAreaGroupPolicy();

    List<Multiselect> selectDdosManagePolicy();
    
    List<Multiselect> getWebPushManagePolicy();

    List<Multiselect> getMessageTypeName();

    /**
     * 获取指定应用用户统计策略相关的select下拉选信息
     * @return
     */
    List<Multiselect> getAppUserPolicy();
    
    List<Multiselect> getAreaList(Map<String,Object> query);
    /**
     * IDC机房
     * @return
     */
    List<Multiselect> getIdcHouseList();

    List<Multiselect> getCarrieroperators();
}
