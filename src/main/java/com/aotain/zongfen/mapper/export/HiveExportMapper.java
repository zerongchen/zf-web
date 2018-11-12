package com.aotain.zongfen.mapper.export;


import com.aotain.common.config.annotation.MyBatisDao;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface HiveExportMapper {

    int addExportTask(Map<String, Object> params);

    int deleteExportTask(Map<String, Object> params);

    int updateDownloadFile(Map<String, Object> params);

    List<Map<String,String>> selectExportTask(Map<String,String> params);
}
