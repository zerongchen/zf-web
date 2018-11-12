package com.aotain.zongfen.service.apppolicy.ddosflowmanage;

import com.aotain.common.policyapi.model.DdosExceptFlowStrategy;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;

import java.util.List;

/**
 * Demo class
 *
 * @author chenym@aotain.com
 * @date 2018/04/04
 */
public interface DdosFlowManageService {

    /**
     * 添加ddos策略和用户绑定策略
     * @return
     */
	long addddosFlowPolicyAndUserBindPolicy(DdosExceptFlowStrategy ddosExceptFlowStrategy);

    /**
     * 查询分页数据
     * @param
     */
    List<DdosExceptFlowStrategyPo> listData(DdosExceptFlowStrategyPo page);

    /**
     * 根据主键查询记录
     * @param ddosExceptFlowStrategy
     * @return
     */
    DdosExceptFlowStrategy selectByPrimaryKey(DdosExceptFlowStrategy ddosExceptFlowStrategy);

    /**
     * 批量删除数据
     * @param ddosExceptFlowStrategys
     * @return
     */
    boolean deletePolicys(List<DdosExceptFlowStrategy> ddosExceptFlowStrategys);

    boolean modifyDdosFlowManageAndUserBindPolicy(DdosExceptFlowStrategy ddosExceptFlowStrategy);

    /**
     * 是否存在名称相同的记录
     * @param voipFlowStrategy
     * @return
     */
  //  boolean existSameNameRecord(VoipFlowStrategy voipFlowStrategy);

    /**
     * 重发策略
     * @param topTaskId
     * @param messageNo
     * @param dpiIp
     * @param needSendBindPolicy 是否需要发送绑定策略
     * @return
     */
    boolean resendPolicy(long topTaskId, long messageNo, boolean needSendBindPolicy, List<String> dpiIp);

}
