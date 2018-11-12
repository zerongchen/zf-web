package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.TriggerDTO;
import com.aotain.zongfen.model.general.Trigger;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface TriggerMapper {
    int deleteByPrimaryKey(Integer triggerId);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    Trigger selectByPrimaryKey(Integer triggerId);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKey(Trigger record);

    List<TriggerDTO> getTrigger( Trigger trigger );

    int delete(List<Trigger> list);

    int countByTriggerName(@Param("triggerName") String triggerName);
    
    List<Trigger> getTriggerByType(List<Integer> list);
}