package com.aotain.zongfen.service.apppolicy.appdefined;

import com.aotain.common.policyapi.model.AppDefinedStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/10
 */
public interface IAppDefinedService {

    /**
     * 增加AppDefinedStrategy和用户绑定策略
     * @param appDefinedStrategy
     */
	List<Long> addAppDefinedAndUserBindPolicy(AppDefinedStrategy appDefinedStrategy);

    /**
     * 修改AppDefinedStrategy和用户绑定策略
     * @param appDefinedStrategy
     */
	List<Long> modifyAppDefinedAndUserBindPolicy(AppDefinedStrategy appDefinedStrategy);

    /**
     * 查询数据
     * @param queryMap
     * @return
     */
    List<AppDefinedStrategy> listData(Map<String,Object> queryMap);

    /**
     * 重发策略
     * @param topTaskId
     * @param messageNo
     * @param dpiIp
     * @param needSendBindPolicy 是否需要发送绑定策略
     * @return
     */
    List<Long> resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp);

    /**
     * 根据主键查询记录
     * @param definedId
     * @return
     */
    AppDefinedStrategy selectByPrimaryKey(long definedId);

    /**
     * 批量删除记录
     * @param appDefinedStrategies
     * @return
     */
    List<Long> deletePolicys(List<AppDefinedStrategy> appDefinedStrategies);
}
