package com.aotain.zongfen.service.common;

import com.aotain.zongfen.dto.common.Multiselect;

import java.util.List;
import java.util.Map;

public interface MultiSelectService {

    /**
     * 信息推送库 网站
     * @return
     */
    List<Multiselect> getWSOption();

    /**
     * 信息推送库 关键字
     * @return
     */
    List<Multiselect> getKWOption();

    /**
     * 信息推送库 白名单
     * @return
     */
    List<Multiselect> getWLOption();

    /**
     * 大类应用
     * @return
     */
    List<Multiselect> getAppType();

    /**
     * 小类应用
     * @param appType
     * @return
     */
    List<Multiselect> getAppByType(Integer appType);

    /**
     * 获取用户组
     * @return
     */
    List<Multiselect> getUserGroup();

    /**
     * 获取分类库文件
     * @param  classType 分类库类别
     * @return
     */
    List<Multiselect> getClassFileInfo(Integer classType);

    /**
     * 获取综分设备
     * @param map
     * @return
     */
    List<Multiselect> getZongfenDev(Map<String,Object> map);

    /**
     * 
    * @Title: getWebType
    * @Description: 获取web分类
    * @param @return
    * @return List<Multiselect>
    * @throws
     */
    List<Multiselect> getWebType();
    /**
     * 获取流量流向策略
     * @return
     */
    List<Multiselect> getAreaGroupPolicy();

    List<Multiselect> getDdosManagePolicy();
    
    /**
     * 
    * @Title: getWebPushManagePolicy
    * @Description: web信息推送管理策略
    * @param @return
    * @return List<Multiselect>
    * @throws
     */
    List<Multiselect> getWebPushManagePolicy();

    List<Multiselect> getMessageTypeName();

    /**
     * 获取指定应用用户统计策略相关的select下拉选信息
     * @return
     */
    List<Multiselect> getAppUserPolicy();
    
    
    List<Multiselect> getAreaList();
    List<Multiselect> getIdcHouseList();
    List<Multiselect> getCarrieroperators();
}
