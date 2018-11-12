package com.aotain.zongfen.service.export;

import java.util.List;
import java.util.Map;

public interface HiveExportService {

    boolean addExportTask(Map<String, Object> params);

    boolean deleteExportTask(Map<String, Object> params);

    boolean updateDownloadFile(Map<String, Object> params);

    List<Map<String,String>> selectExportTask(int pageIndex, int pageSize, Map<String,String> params);

}
