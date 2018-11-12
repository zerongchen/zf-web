package com.aotain.zongfen.service.monitor;

import com.aotain.zongfen.dto.monitor.MonitorOnlineuserDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusFileDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusMonitorDTO;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.monitor.RadiusPcapDetail;
import com.aotain.zongfen.model.monitor.RadiusPolicyDetail;
import com.aotain.zongfen.model.monitor.RadiusRelayDetail;
import com.aotain.zongfen.utils.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RaduisService {

    List<RadiusMonitorDTO> getList( RadiusMonitorDTO dto);

    /**
     * 在线用户数据表格数据
     * @author chenym
     * @param dto
     * @return
     */
    List<MonitorOnlineuserDetailDTO> getOnlineUserList(MonitorOnlineuserDetailDTO dto);
    /**
     * 在线用户数据图数据
     * @author chenym
     * @param dto
     * @return
     */
    ECharts<Series<Long>> getEchartsData( MonitorOnlineuserDetailDTO dto);

    /**
     * 3A数据监控
     * @param dto
     * @return
     */
    List<ECharts<Series>> getEchartsData( RadiusMonitorDTO dto);

    /**
     * 获取中转详情
     * @param detail
     * @return
     */
    List<RadiusPcapDetail> getRadiusPcapDetail( RadiusParamDTO detail);

    /**
     * 获取中转详情
     * @param detail
     * @return
     */
    List<RadiusRelayDetail> getRadiusRelayDetail(RadiusParamDTO detail);

    /**
     * 获取发包详情
     * @param detail
     * @return
     */
    List<RadiusPolicyDetail> getRadiusPolicyDetail( RadiusParamDTO detail);

    /**
     * 获取文件列表
     * @param detail
     * @return
     */
    List<RadiusFileDetailDTO> getFileDetail( RadiusParamDTO detail);

    PageResult<Map<String,String>> getFileSecondDetail(Integer page, Integer pageSize, Map<String, String> params);

    PageResult<Map<String,String>> getFileThirdDetail(Integer pageIndex, Integer pageSize, Map<String, String> params);

    PageResult<Map<String,String>> getWarnFileThirdDetail(Integer pageIndex, Integer pageSize, Map<String, String> params);

    List<Map<String,String>> getRadiusRelaySecondDetail(Map<String, String> params);

    List<Map<String,String>> getRelayThirdDetail(Map<String, String> params);

    List<Map<String,String>> getPcapThirdDetail(Map<String, String> params);

    List<Map<String,String>> getRadiusPolicySecondDetail(Map<String, String> params);

    List<Map<String,String>> getPolicyThirdDetail(Map<String, String> params);
}
