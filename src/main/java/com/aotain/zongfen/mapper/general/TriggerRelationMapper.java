package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.general.TriggerRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface TriggerRelationMapper {
    int insert(TriggerRelation record);

    int insertSelective(TriggerRelation record);

    int insertHostArrays(@Param("triggerId") Integer triggerId,@Param("array") Integer[] array);

    int insertKwArrays(@Param("triggerId") Integer triggerId,@Param("array") Integer[] array);

    int deleteByTriggerId(@Param("triggerId") Integer triggerId);

    int deleteByTriggerHostId(Long[] array);

    int deleteByTriggerKwId(Long[] array);

    List<Integer> getRelationTriggerIdByHostListId(@Param("id") Integer id);

    List<Integer> getRelationTriggerIdByKwListId(@Param("id") Integer id);
}