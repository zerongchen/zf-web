package com.aotain.zongfen.mapper.common;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.common.DictFactory;

@MyBatisDao
public interface DictFactoryMapper {
    int insert(DictFactory record);

    int insertSelective(DictFactory record);
    
    List<DictFactory> getDictFactoryList();
    
    DictFactory select(@Param("facotryCode")String facotryCode);
}