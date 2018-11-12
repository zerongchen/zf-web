package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.TriggerKWListDTO;
import com.aotain.zongfen.model.general.TriggerKW;
import com.aotain.zongfen.model.general.TriggerKWList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface TriggerKWListMapper {
    int insert(TriggerKWList record);

    int deleteById(TriggerKWList record);

    int deleteByIds(Long[] array);

    int deleteByListIds(Long[] array);

    int insertSelective(TriggerKWList record);

    List<TriggerKWListDTO> getTriggerKWList( @Param("triggerKwListid") Long triggerKwListid);

    int inserKwList(@Param("triggerKW") TriggerKW triggerKW, @Param("list") List<String> list);

    int countkwName(TriggerKWList record);

    Long getNumByListId(@Param("id") Long id);

    List<String> getTriggerKWName(TriggerKW triggerKW);

}