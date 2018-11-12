package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.FlowSignStrategy;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/03
 */
@MyBatisDao
public interface FlowSignStrategyMapper {

    /**
     * 根据主键查询记录
     * @return
     */
    FlowSignStrategy selectByPrimaryKey(Long messageNo);

    /**
     * 新增数据
     * @param flowSignStrategy
     * @return
     */
    int insertSelective(FlowSignStrategy flowSignStrategy);

    /**
     * 修改记录
     * @param flowSignStrategy
     * @return
     */
    int updateSelective(FlowSignStrategy flowSignStrategy);

    /**
     * 删除数据
     * @param flowSignStrategy
     */
    int deleteData(FlowSignStrategy flowSignStrategy);

    /**
     * 查询记录
     * @param queryMap
     * @return
     */
    List<FlowSignStrategy> listData(Map<String,Object> queryMap);
}
