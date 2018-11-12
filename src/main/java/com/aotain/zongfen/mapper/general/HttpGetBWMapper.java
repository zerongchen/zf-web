package com.aotain.zongfen.mapper.general;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.general.HttpGetBWDTO;
import com.aotain.zongfen.model.general.HttpGetBW;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MyBatisDao
public interface HttpGetBWMapper {
    int insert(HttpGetBW record);

    int insertSelective(HttpGetBW record);

    List<HttpGetBWDTO> getDomain(HttpGetBW recode);

    int deleteByType(@Param("type") Integer type);

    int deleteByIds(@Param("array") Long[] array);

    List<String> getListByType(@Param("type") Integer type);

    int insertByType(@Param("type") Integer type,@Param("set") Set<String> set);

    List<Map<String,BigDecimal>> getTimeGap(@Param("filetype") Integer filetype);

    int updateBw(HttpGetBW httpGetBW);
}