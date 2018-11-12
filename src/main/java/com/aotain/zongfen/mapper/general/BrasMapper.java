package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.BrasDTO;
import com.aotain.zongfen.model.general.Bras;

import java.util.List;

@MyBatisDao
public interface BrasMapper {
    int insert(Bras record);

    int insertSelective(Bras record);

    List<BrasDTO> getBrase( Bras record);

    /**
     * 获取所有用户并根据brasid和brasname去重
     * @return
     */
    List<Bras> selectDistinctBrasList();
}