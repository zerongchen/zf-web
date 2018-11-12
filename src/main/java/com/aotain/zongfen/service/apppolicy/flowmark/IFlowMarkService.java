package com.aotain.zongfen.service.apppolicy.flowmark;

import com.aotain.common.policyapi.model.FlowSignStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/03
 */
public interface IFlowMarkService {

    /**
     * 根据主键查询记录
     * @param messageNo
     * @return
     */
    FlowSignStrategy selectByPrimaryKey(long messageNo);

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
     * 增加流量标记和相关用户绑定策略
     * @param flowSignStrategy
     * @return
     */
    List<Long> addFlowSignAndUserBindPolicy(FlowSignStrategy flowSignStrategy);

    /**
     * 修改流量标记和相关用户绑定策略
     * @param flowSignStrategy
     * @return
     */
    List<Long> modifyFlowSignAndUserBindPolicy(FlowSignStrategy flowSignStrategy);

    /**
     * 批量删除记录
     * @param flowSignStrategies
     * @return
     */
    List<Long> deletePolicys(List<FlowSignStrategy> flowSignStrategies);

    /**
     * 是否存在策略名称相同的记录
     * @param flowSignStrategy
     * @return
     */
    boolean existSameNameRecord(FlowSignStrategy flowSignStrategy);

    /**
     * 查询分页数据
     * @return
     */
    List<FlowSignStrategy> listData(Map<String,Object> queryMap);
}
