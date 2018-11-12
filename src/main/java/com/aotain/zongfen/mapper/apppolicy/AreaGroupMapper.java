package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.msg.AreaGroup;

@MyBatisDao
public interface AreaGroupMapper {
    int deleteByMessageNo(Long messageNo);

    int insert(AreaGroup record);

    int insertSelective(AreaGroup record);

    List<AreaGroup> selectByNoAndType(AreaGroup record);

    int updateByPrimaryKeySelective(AreaGroup record);
    
    int insertList(List<AreaGroup> list);
    
    /**
     * 获取来源区域的字符串
     * @param messageNo
     * @return
     */
    AreaGroup selectInternal(@Param("messageNo")Long messageNo);
    /**
     * 获取目的区域的字符串
     * @param messageNo
     * @return
     */
    AreaGroup selectExternal(@Param("messageNo")Long messageNo);

}