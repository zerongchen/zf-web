package com.aotain.zongfen.mapper.policy;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.policy.Policy;

@MyBatisDao
public interface PolicyMapper {
    int insert(Policy record);

    int insertSelective(Policy record);

    Long getMaxMessageNoByType(Policy record);

    /**
     * 伪删除
     * @param record
     * @return
     */
    int deletePolicyByMesNoAndType( Policy record);

    int deletePolicyByMessageNoAndType(@Param("array") Long[] array, @Param("type") long type);

    int updatePolicyName(@Param("messageName") String messageName , @Param("messageno") Long messageno,@Param("messageType") Integer messageType);
    
    int updatePolicyByMessageNoAndType(Policy record);

    /**
     * 真删除
     * @param record
     * @return
     */
    int deleteByMessageNoAndType(Policy record);

    int isSamePolicyNameByType(Policy record);
    
    int isSameNamePolicyName(Map<String,Object> query);
    
    List<Policy> selectByType(@Param("messageType")Integer messageType);
    
    List<Policy> selectList(Policy record);
    
    List<Policy> selectListForDirection(Policy record);
    
    long count(Policy record);
    
}