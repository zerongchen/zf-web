package com.aotain.zongfen.service.apppolicy.flowmirror;

import com.aotain.common.policyapi.model.FlowMirrorStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/09
 */
public interface IFlowMirrorService {

    /**
     * 添加策略表和绑定用户信息
     * @param flowMirrorStrategy
     * @return
     */
    List<Long> addFlowMirrorAndUserBindPolicy(FlowMirrorStrategy flowMirrorStrategy);

    /**
     * 修改策略表和绑定用户信息
     * @param flowMirrorStrategy
     * @return
     */
    List<Long> modifyFlowMirrorAndUserBindPolicy(FlowMirrorStrategy flowMirrorStrategy);

    /**
     * 查询分页数据
     * @return
     */
    List<FlowMirrorStrategy> listData(Map<String,Object> queryMap);

    /**
     * 根据policyId查询记录
     * @param policyId
     * @return
     */
    FlowMirrorStrategy selectByPrimaryKey(long policyId);

    /**
     * 批量删除记录
     * @param flowMirrorStrategies
     * @return
     */
    List<Long> deletePolicys(List<FlowMirrorStrategy> flowMirrorStrategies);

    /**
     * 重发策略
     * @param topTaskId
     * @param messageNo
     * @param dpiIp
     * @param needSendBindPolicy 是否需要发送绑定策略
     * @return
     */
    List<Long> resendPolicy(long topTaskId,long messageNo, boolean needSendBindPolicy, List<String> dpiIp);
}
