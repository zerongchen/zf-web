package com.aotain.zongfen.mapper.apppolicy.flowmirror;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.FlowMirrorStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/09
 */
@MyBatisDao
public interface FlowMirrorMapper {
    /**
     * 新增记录
     * @param flowMirrorStrategy
     * @return
     */
    int insertSelective(FlowMirrorStrategy flowMirrorStrategy);

    /**
     * 修改记录
     * @param flowMirrorStrategy
     * @return
     */
    int updateSelective(FlowMirrorStrategy flowMirrorStrategy);

    /**
     * 删除数据
     * @param flowMirrorStrategy
     * @return
     */
    int deleteData(FlowMirrorStrategy flowMirrorStrategy);

    /**
     * 根据policyId查询记录
     * @param policyId
     * @return
     */
    FlowMirrorStrategy selectByPrimaryKey(long policyId);

    /**
     * 查询记录
     * @param queryMap
     * @return
     */
    List<FlowMirrorStrategy> listData(Map<String,Object> queryMap);
}
