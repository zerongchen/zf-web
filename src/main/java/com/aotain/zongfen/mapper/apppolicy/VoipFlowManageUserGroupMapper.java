package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.VoipFlowManageUserGroup;
import com.github.abel533.mapper.Mapper;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/03/15
 */
@MyBatisDao
public interface VoipFlowManageUserGroupMapper extends Mapper<VoipFlowManageUserGroup>{

    /**
     * 根据voipflowId删除记录
     * @param voipflowId
     * @return
     */
    int deleteByVoipFlowId(int voipflowId);
}
