package com.aotain.zongfen.mapper.apppolicy;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.policyapi.model.WebPushStrategy;
import com.aotain.zongfen.dto.apppolicy.WebPushStrategyVo;

import java.util.List;

@MyBatisDao
public interface PolicyWebPushMapper {
    /**
     * 插入数据
     * @param vo
     * @return
     */
    int insertSelective(WebPushStrategyVo vo);


    /**
     * 分页查询数据
     * @param page
     * @return
     */
    List<WebPushStrategyVo> listData(WebPushStrategyVo page);

    /**
     * 根据主键查询数据
     * @param vo
     * @return
     */
    WebPushStrategyVo selectByPrimaryKey(WebPushStrategyVo vo);

    /**
     * 根据主键删除数据（逻辑删除）
     * @param vo
     * @return
     */
    int deleteData(WebPushStrategyVo vo);

    int updateByPrimaryKeySelective(WebPushStrategyVo vo);
}