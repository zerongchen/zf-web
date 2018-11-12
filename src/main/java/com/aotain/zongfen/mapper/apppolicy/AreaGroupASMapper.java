package com.aotain.zongfen.mapper.apppolicy;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.common.policyapi.model.msg.AreaGroupAS;

@MyBatisDao
public interface AreaGroupASMapper {
    int insert(AreaGroupAS record);

    int insertSelective(AreaGroupAS record);
    
    List<AreaGroupAS> selectByGroup(@Param("areagroupId")Long areagroupId);
    
    int insertList(List<AreaGroupAS> list);
    
    int deleteByMessageNo(@Param("messageNo")Long messageNo);
    	
}