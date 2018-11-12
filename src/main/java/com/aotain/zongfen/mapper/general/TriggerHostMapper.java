package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.TriggerHostDTO;
import com.aotain.zongfen.model.general.TriggerHost;

import java.util.List;

@MyBatisDao
public interface TriggerHostMapper {
    int insert(TriggerHost record);

    int insertSelective(TriggerHost record);

    int updateSelective(TriggerHost record);

    /**
     *
     * @param record
     * @return
     */
    List<TriggerHostDTO> getTriggerHost( TriggerHost record);

    /**
     *
     * @return
     */
    Long getMaxID();

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
    int updateNum( TriggerHost record);

    int selectByHostListName(String hostListName);

    TriggerHost selectByHostListId(Long hostListId);
}