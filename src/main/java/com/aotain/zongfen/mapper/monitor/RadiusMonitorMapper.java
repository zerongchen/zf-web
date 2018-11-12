package com.aotain.zongfen.mapper.monitor;
import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.monitor.MonitorOnlineuserDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusMonitorDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface RadiusMonitorMapper {


    List<RadiusMonitorDTO> getRadiusList(RadiusMonitorDTO dto);

    /**
     * 3A抓包
     * @param dto
     * @return
     */
    List<RadiusMonitorDTO> getRadiusPcapEcharts(RadiusMonitorDTO dto);

    /**
     * 3A中转
     * @param dto
     * @return
     */
    List<RadiusMonitorDTO> getRadiusRelayEcharts(RadiusMonitorDTO dto);

    /**
     * 3A发送包
     * @param dto
     * @return
     */
    List<RadiusMonitorDTO> getRadiusPolicyEcharts(RadiusMonitorDTO dto);

    /**
     * 3A上报
     * @param dto
     * @return
     */
    List<RadiusMonitorDTO> getRadiusCreateEcharts(RadiusMonitorDTO dto);

    /**
     * 获取在线用户数据
     * @param dto
     * @return
     */
    List<MonitorOnlineuserDetailDTO> getOnlineuserList(MonitorOnlineuserDetailDTO dto);
    /**
     * 在线用户数据图数据
     * @author chenym
     * @param dto
     * @return
     */
    List<MonitorOnlineuserDetailDTO> getOnlineuserEchartsData(MonitorOnlineuserDetailDTO dto);

    List<Map<String,String>> getFileSecondDetail(Map<String, String> params);

    List<Map<String,String>> getFileThirdDetail(Map<String, String> params);

    List<Map<String,String>> getWarnFileSecondCount(Map<String, String> params);

    List<Map<String,String>> getWarnFileThirdDetail(Map<String, String> params);

    List<Map<String,String>> getRadiusRelaySecondDetail(Map<String, String> params);

    List<Map<String,String>> getRelayThirdDetail(Map<String, String> params);

    List<Map<String,String>> getPcapThirdDetail(Map<String, String> params);

    List<Map<String,String>> getRadiusPolicySecondDetail(Map<String, String> params);

    List<Map<String,String>> getPolicyThirdDetail(Map<String, String> params);
}
