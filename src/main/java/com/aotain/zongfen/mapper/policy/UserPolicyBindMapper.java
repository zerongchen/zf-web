package com.aotain.zongfen.mapper.policy;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;

@MyBatisDao
public interface UserPolicyBindMapper {
    
    int insert(UserPolicyBindStrategy record);

    int insertSelective(UserPolicyBindStrategy record);
    
    UserPolicyBindStrategy getByBindMessageNo(Long messageNo);
    
    List<UserPolicyBindStrategy> getByBindMessageNoAndType(Map<String,Object> bindQuery);

    UserPolicyBindStrategy getByBindMessage(UserPolicyBindStrategy record);
    
    /**
     * 
    * @Title: getByBindMessages
    * @Description: 获取多个绑定策略 
    * @param @param record
    * @param @return
    * @return List<UserPolicyBindStrategy>
    * @throws
     */
    List<UserPolicyBindStrategy> getByBindMessages(UserPolicyBindStrategy record);
    
    /**
     * 
    * @Title: deleteByBindMessage 
    * @Description: 物理删除(这里用一句话描述这个方法的作用) 
    * @param @param record
    * @param @return    设定文件 
    * @return int    返回类型 
    * @throws
     */
    int deleteByBindMessage(UserPolicyBindStrategy record);
    /**
     * 
    * @Title: updateOrDelete 
    * @Description: 修改或者删除策略，逻辑删除(这里用一句话描述这个方法的作用) 
    * @param @param record
    * @param @return    设定文件 
    * @return int    返回类型 
    * @throws
     */
    int updateOrDelete(UserPolicyBindStrategy record);
    
    /**
     * 
    * @Title: deleteByBindMessageNo 
    * @Description: 物理删除(这里用一句话描述这个方法的作用) 
    * @param @param bindMessageNo
    * @param @return    设定文件 
    * @return int    返回类型 
    * @throws
     */
    int deleteByBindMessageNo(Long bindMessageNo);
    
    UserPolicyBindStrategy getOneByBindMessageNoAndType(Map<String,Object> bindQuery);
    /**
     * 获取所有的策略，根据策略messageNo
     * @param record
     * @return
     */
    List<UserPolicyBindStrategy> getByMessageNo(UserPolicyBindStrategy record);
    
    /**
     * 根据messageNo删除
     * @param messageNo
     * @return
     */
    int deleteByMessageNo(@Param("messageNo")Long messageNo);
    
    /**
     * 
    * @Title: getByBindMessageNosAndType
    * @Description: 根据messageType和多个messageNo获取绑定策略的数量 
    * @param @return
    * @return int
    * @throws
     */
    int getByBindMessageNosAndType(Map<String,Object> bindQuery);
    
    /**
     * 
    * @Title: getBindMessageNo
    * @Description: 根据messageNo和bindMessageType查找bindMessageNo
    * @param @param query
    * @param @return
    * @return Long
    * @throws
     */
    Long getBindMessageNo(Map<String,Object> query);
}