package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.DdosExceptFlowStrategy;
import com.aotain.common.policyapi.model.msg.DdosFlowAppAttachNormal;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface DdosFlowManageMapper {

    /**
     * 插入数据
     * @return
     */
    int insertSelective(Map<String,Integer> ddosMap);


    /**
     * 分页查询数据
     * @return
     */
    List<DdosExceptFlowStrategyPo> listData(DdosExceptFlowStrategyPo page);

    /**
     * 根据主键查询数据
     * @param ddosFlowStrategy
     * @return
     */
    DdosExceptFlowStrategy selectByPrimaryKey(DdosExceptFlowStrategy ddosFlowStrategy);

    /**
     * 根据主键删除数据（逻辑删除）
     * @param ddosFlowStrategy
     * @return
     */
    int deleteData(DdosExceptFlowStrategy ddosFlowStrategy);

    int updateByPrimaryKeySelective(DdosExceptFlowStrategy ddosExceptFlowStrategy);

    /**
     * 根据主键查询数据
     * @param ddosFlowStrategy
     * @return
     */
    List<DdosFlowAppAttachNormal> selectByMessage(DdosExceptFlowStrategy ddosFlowStrategy);

}
