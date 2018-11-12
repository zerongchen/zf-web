package com.aotain.zongfen.mapper.apppolicy.flowmirror;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.apppolicy.flowmirror.FlowMirrorIp;
import com.github.abel533.mapper.Mapper;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/09
 */
@MyBatisDao
public interface FlowMirrorIpMapper extends Mapper<FlowMirrorIp> {

    /**
     * 根据policyId删除记录
     * @param policyId
     * @return
     */
    int deleteByPolicyId(long policyId);
}
