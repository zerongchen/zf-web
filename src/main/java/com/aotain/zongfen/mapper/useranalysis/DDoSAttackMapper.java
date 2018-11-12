package com.aotain.zongfen.mapper.useranalysis;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.model.useranalysis.DDoSQueryParam;
import com.aotain.zongfen.model.useranalysis.DDoSUbas;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/06/21
 */
@MyBatisDao
public interface DDoSAttackMapper {
    /**
     * 获取按天统计数据
     * @param dDoSQueryParam
     * @return
     */
    List<DDoSUbas> getChartData(DDoSQueryParam dDoSQueryParam);

    List<Map<String,String>> loadExportTask(Map<String, String> params);

    int createExportTask(Map<String, Object> params);

    int deleteExportTask(Map<String, Object> params);

    void updateDownloadFile(Map<String, String> params);
}
