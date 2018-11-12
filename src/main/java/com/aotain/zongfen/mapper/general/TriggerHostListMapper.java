package com.aotain.zongfen.mapper.general;

import java.util.List;

import com.aotain.zongfen.model.general.TriggerHost;
import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.TriggerHostListDTO;
import com.aotain.zongfen.model.general.TriggerHostList;

@MyBatisDao
public interface TriggerHostListMapper {
    int insert(TriggerHostList record);

    int deleteById(TriggerHostList record);

    int deleteByIds(Long[] array);

    int deleteByListIds(Long[] array);

    int insertSelective(TriggerHostList record);

    List<TriggerHostListDTO> getTriggerHostList(@Param("triggerHostListid") Long triggerHostListid);

    int insertHostList(@Param("triggerHost")TriggerHost triggerHost, @Param("list") List<String> list);

    int countHostName(TriggerHostList record);

    Long getNumByListId(@Param("id") Long id);

    List<String> getTriggerHostName(TriggerHost triggerHost);
}