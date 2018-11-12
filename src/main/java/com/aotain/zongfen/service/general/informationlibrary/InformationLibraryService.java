package com.aotain.zongfen.service.general.informationlibrary;

import com.aotain.zongfen.dto.general.TriggerHostDTO;
import com.aotain.zongfen.dto.general.TriggerHostListDTO;
import com.aotain.zongfen.dto.general.TriggerKWDTO;
import com.aotain.zongfen.dto.general.TriggerKWListDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.TriggerHost;
import com.aotain.zongfen.model.general.TriggerHostList;
import com.aotain.zongfen.model.general.TriggerKW;
import com.aotain.zongfen.model.general.TriggerKWList;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface InformationLibraryService {

    /**
     * 获取网站列表
     * @param triggerHost
     * @return
     */
    List<TriggerHostDTO> getTriggerHost( TriggerHost triggerHost);

    /**
     * 获取网站、白名单详情列表
     * @param triggerHostListDTO
     * @return
     */
    List<TriggerHostListDTO> getTriggerHostList( TriggerHostListDTO triggerHostListDTO);

    /**
     * 获取关键字列表
      * @param triggerKW
     * @return
     */
    List<TriggerKWDTO> getTriggerKW( TriggerKW triggerKW);

    /**
     * 获取关键字详情列表
     * @param triggerKWListDTO
     * @return
     */
    List<TriggerKWListDTO> getTriggerKWList( TriggerKWListDTO triggerKWListDTO);

    /**
     * 导入
     * @param request
     * @param baseEntity  传入公共属性
     * @throws ImportException
     */
    boolean handleImport(HttpServletRequest request, BaseEntity baseEntity) throws ImportException;

    /**
     * 删除列表数据
     * @param infoType
     * @param hostIds
     */
    void deleteByType(Integer infoType,String[] hostIds);

    /**
     * 删除详情列表数据
     * @param infoType
     * @param ids
     */
    void deleteList(Integer infoType,String[] ids,Long chooseId);

    /**
     * 新增或者更新详情列表数据 白名单或者网站
     * @param triggerHostList
     * @return
     */
    ResponseResult insertOrUpdateHostList( TriggerHostList triggerHostList);

    /**
     * 新增或者更新详情列表数据 关键字
     * @param triggerKWList
     * @return
     */
    ResponseResult insertOrUpdateKwList( TriggerKWList triggerKWList);

    /**
     * 处理生成文件并下发
     * @param infoType
     * @param id
     */
    void handReIssuedBaseTypeAndId(Integer infoType,Integer id);

    /**
     * 重发策略
     * @param infoType   重发策略类型
     * @param topTaskId
     * @param messageNo
     * @param ips
     */
    void resendPolicy(int infoType,long topTaskId,long messageNo,List<String> ips);

    /**
     * 查询是否存在同名记录
     * @param infoType
     * @param name
     * @return
     */
    boolean existSameNameRecord(int infoType,String name);

    /**
     * 查询是否存在hostListName相同的记录
     * @param triggerHostList
     * @return
     */
    int countHostName(TriggerHostList triggerHostList);

    /**
     * 查询是否存在kwListName相同的记录
     * @param triggerKWList
     * @return
     */
    int countkwName(TriggerKWList triggerKWList);

    boolean sendPolicyAfterUpdateOrDelete(int infoType,long chooseId);

}
