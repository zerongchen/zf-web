package com.aotain.zongfen.service.apppolicy.voipflowmanage;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.model.VoipFlowStrategy;

import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/15
 */
public interface IVoipFlowManageService{

    /**
     * 添加指定应用用户策略和用户绑定策略
     * @param voipFlowStrategy
     * @return
     */
    boolean addVoipFlowManageAndUserBindPolicy(VoipFlowStrategy voipFlowStrategy);

    /**
     * 修改指定应用用户策略和用户绑定策略
     * @param voipFlowStrategy
     * @return
     */
    boolean modifyVoipFlowManageAndUserBindPolicy(VoipFlowStrategy voipFlowStrategy);

    /**
     * 查询分页数据
     * @param page
     */
    List<VoipFlowStrategy> listData(Page<VoipFlowStrategy> page);

    /**
     * 根据主键查询记录
     * @param voipFlowStrategy
     * @return
     */
    VoipFlowStrategy selectByPrimaryKey(VoipFlowStrategy voipFlowStrategy);

    /**
     * 批量删除数据
     * @param voipFlowStrategies
     * @return
     */
    boolean deletePolicys(List<VoipFlowStrategy> voipFlowStrategies);

    /**
     * 是否存在名称相同的记录
     * @param voipFlowStrategy
     * @return
     */
    boolean existSameNameRecord(VoipFlowStrategy voipFlowStrategy);

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
