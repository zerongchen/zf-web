package com.aotain.zongfen.mapper.device;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.device.DpiStaticPort;

@MyBatisDao
public interface DpiStaticPortMapper {
    int deleteByPrimaryKey(Integer portno);

    int insert(DpiStaticPort record);

    int insertSelective(DpiStaticPort record);

    DpiStaticPort selectByPrimaryKey(Integer portno);

    int updateByPrimaryKeySelective(DpiStaticPort record);

    int updateByPrimaryKey(DpiStaticPort record);
    /**
     * 
    * @Title: selectList 
    * @Description: 获取静态站点下的端口列表信息(这里用一句话描述这个方法的作用) 
    * @param @param deploysitename
    * @param @return    设定文件 
    * @return List<DpiStaticPort>    返回类型 
    * @throws
     */
    List<DpiStaticPort> selectList(@Param("deploysitename")String deploysitename);

    /**
     * 根据linkid 和linkdesc去重查询
     * @return
     */
    List<DpiStaticPort> selectDistinctLinkList();
}