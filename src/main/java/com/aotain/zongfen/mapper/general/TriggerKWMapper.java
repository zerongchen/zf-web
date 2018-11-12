package com.aotain.zongfen.mapper.general;

import java.util.List;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.TriggerKWDTO;
import com.aotain.zongfen.model.general.TriggerKW;

@MyBatisDao
public interface TriggerKWMapper {
    int insert(TriggerKW record);

    int insertSelective(TriggerKW record);

    int updateSelective(TriggerKW record);

    List<TriggerKWDTO> getTriggerKW( TriggerKW record);

    Long getMaxId();

    /**
     *
     * @param array
     * @return
     */
    int deleteById(Long[] array);

    /**
     * 更新个数
     * @param record
     * @return
     */
    int updateNum( TriggerKW record);

    /**
     * 根据listName查询是否存在相同名称的记录
     * @param triggerKwListname
     * @return
     */
    int selectByListName(String triggerKwListname);

    TriggerKW selectByListId(Long triggerKwListId);

}