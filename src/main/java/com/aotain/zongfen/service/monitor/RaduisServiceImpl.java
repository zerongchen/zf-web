package com.aotain.zongfen.service.monitor;

import com.aotain.common.config.LocalConfig;
import com.aotain.zongfen.dto.monitor.MonitorOnlineuserDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusFileDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusMonitorDTO;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;
import com.aotain.zongfen.mapper.monitor.*;
import com.aotain.zongfen.model.echarts.Data;
import com.aotain.zongfen.model.echarts.DataZoom;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.monitor.RadiusPcapDetail;
import com.aotain.zongfen.model.monitor.RadiusPolicyDetail;
import com.aotain.zongfen.model.monitor.RadiusRelayDetail;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.UnitUtils;
import com.aotain.zongfen.utils.basicdata.DateFormatConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RaduisServiceImpl implements RaduisService {

    private static final Logger LOG = LoggerFactory.getLogger(RaduisServiceImpl.class);

    @Autowired
    private RadiusMonitorMapper radiusMonitorMapper;

    @Autowired
    private RadiusPcapDetailMapper radiusPcapDetailMapper;

    @Autowired
    private RadiusRelayDetailMapper radiusRelayDetailMapper;

    @Autowired
    private RadiusPolicyDetailMapper radiusPolicyDetailMapper;

    @Autowired
    private RadiusFileDetailMapper radiusFileDetailMapper;

    @Override
    public List<RadiusMonitorDTO> getList( RadiusMonitorDTO dto ) {

        return radiusMonitorMapper.getRadiusList(dto);
    }

    @Override
    public List<ECharts<Series>> getEchartsData( RadiusMonitorDTO dto ) {


        DataZoom dataZoom = new DataZoom();
        dataZoom.setStart(0);
        dataZoom.setEnd(100);
        dataZoom.setShow(true);
        List<ECharts<Series>> list = new ArrayList<ECharts<Series>>();

        //采集
        {
            List<String> legend = new ArrayList<String>();
            List<String> axis = new ArrayList<String>();
            List<Series> series = new ArrayList<Series>();
            List<Long> data1 = new ArrayList<Long>();
            List<Long> data2 = new ArrayList<Long>();
            List<RadiusMonitorDTO> pacpDatas = radiusMonitorMapper.getRadiusPcapEcharts(dto);
            if(pacpDatas.size()>0){
                for (RadiusMonitorDTO data :pacpDatas){
                    data1.add(data.getCapturepacketnum()!=null?data.getCapturepacketnum():0l);
                    data2.add(data.getInvalidpacketnum()!=null?data.getInvalidpacketnum():0l);
                    axis.add(data.getStatTime());
                }
            }
            legend.add("采集包数");
            legend.add("错误包数");
            series.add(new Series<Long>(legend.get(0),"line","1",data1));
            series.add(new Series<Long>(legend.get(1),"line","2",data2));
            list.add(new ECharts<>(legend,axis,series,dataZoom));
        }

        //中转
        {
            List<String> legend = new ArrayList<String>();
            List<String> axis = new ArrayList<String>();
            List<Series> series = new ArrayList<Series>();
            List<Long> data1 = new ArrayList<Long>();
            List<Long> data2 = new ArrayList<Long>();
            List<Long> data3 = new ArrayList<Long>();
            List<Long> data4 = new ArrayList<Long>();
            List<RadiusMonitorDTO> relayDatas = radiusMonitorMapper.getRadiusRelayEcharts(dto);
            if(relayDatas.size()>0){
                for (RadiusMonitorDTO data :relayDatas){
                    data1.add(data.getSendnumRedirect()!=null?data.getSendnumRedirect():0l);
                    data2.add(data.getSendfailednumRedirect()!=null?data.getSendfailednumRedirect():0l);
                    data3.add(data.getReceivednum()!=null?data.getReceivednum():0l);
                    data4.add(data.getParsefailednum()!=null?data.getParsefailednum():0l);
                    axis.add(data.getStatTime());
                }
            }
            legend.add("发送包数");
            legend.add("发送失败包数");
            legend.add("接收包数");
            legend.add("解析失败包数");
            series.add(new Series<Long>(legend.get(0),"line","1",data1));
            series.add(new Series<Long>(legend.get(1),"line","2",data2));
            series.add(new Series<Long>(legend.get(2),"line","3",data3));
            series.add(new Series<Long>(legend.get(3),"line","4",data4));
            list.add(new ECharts<>(legend,axis,series,dataZoom));
        }

        //发送
        {
            List<String> legend = new ArrayList<String>();
            List<String> axis = new ArrayList<String>();
            List<Series> series = new ArrayList<Series>();
            List<Long> data1 = new ArrayList<Long>();
            List<Long> data2 = new ArrayList<Long>();
            List<RadiusMonitorDTO> policyDatas = radiusMonitorMapper.getRadiusPolicyEcharts(dto);
            if (policyDatas.size() > 0) {
                for (RadiusMonitorDTO data : policyDatas) {
                    data1.add(data.getSendnumPolicy() != null ? data.getSendnumPolicy() : 0l);
                    data2.add(data.getSendfailednumPolicy() != null ? data.getSendfailednumPolicy() : 0l);
                    axis.add(data.getStatTime());
                }
            }
            legend.add("发送包数");
            legend.add("错误包数");
            series.add(new Series<Long>(legend.get(0), "line", "1", data1));
            series.add(new Series<Long>(legend.get(1), "line", "2", data2));
            list.add(new ECharts<>(legend, axis, series, dataZoom));
        }
        //生成
        {
            List<String> legend = new ArrayList<String>();
            List<String> axis = new ArrayList<String>();
            List<Series> series = new ArrayList<Series>();
            List<Long> data1 = new ArrayList<Long>();
            List<Long> data2 = new ArrayList<Long>();
            List<RadiusMonitorDTO> createDatas = radiusMonitorMapper.getRadiusCreateEcharts(dto);
            if (createDatas.size() > 0) {
                for (RadiusMonitorDTO data : createDatas) {
                    data1.add(data.getFileNumCreate() != null ? data.getFileNumCreate() : 0l);
                    axis.add(data.getStatTime());
                }
            }
            legend.add("生成文件数");
            series.add(new Series<Long>(legend.get(0), "bar", data1));
            list.add(new ECharts<>(legend, axis, series, dataZoom));
        }
        return list;
    }

    @Override
    public List<RadiusPcapDetail> getRadiusPcapDetail( RadiusParamDTO detail ) {
        return radiusPcapDetailMapper.selectListByPrimaryKey(detail);
    }

    @Override
    public List<RadiusRelayDetail> getRadiusRelayDetail( RadiusParamDTO detail ) {
        return radiusRelayDetailMapper.selectListByPrimaryKey(detail);
    }

    @Override
    public List<RadiusPolicyDetail> getRadiusPolicyDetail( RadiusParamDTO detail ) {
        return radiusPolicyDetailMapper.selectListByPrimaryKey(detail);
    }

    @Override
    public List<RadiusFileDetailDTO> getFileDetail( RadiusParamDTO detail ) {
        return radiusFileDetailMapper.getFileDetailList(detail);
    }

    @Override
    public List<MonitorOnlineuserDetailDTO> getOnlineUserList(MonitorOnlineuserDetailDTO dto) {
        String endTime = dto.getEndTime();
        endTime = endTime+" 23:59:59";
        dto.setEndTime(endTime);
        return radiusMonitorMapper.getOnlineuserList(dto);
    }

    @Override
    public ECharts<Series<Long>> getEchartsData(MonitorOnlineuserDetailDTO dto) {
        String endTime = dto.getEndTime();
        endTime = endTime+" 23:59:59";
        dto.setEndTime(endTime);
        List<MonitorOnlineuserDetailDTO> datas = radiusMonitorMapper.getOnlineuserEchartsData(dto);
        List<String> axis = new ArrayList<String>();
        List<Series<Long>> series = new ArrayList<Series<Long>>();
        List<Long> data1 = new ArrayList<Long>();
        DataZoom dataZoom = new DataZoom();
       /* dataZoom.setShow(true);
        dataZoom.setStart(40);
        dataZoom.setEnd(60);*/
        if(datas.size()>0){
            for (MonitorOnlineuserDetailDTO data :datas){
                data1.add(data.getOnlineusernum()!=null?data.getOnlineusernum():0l);
                axis.add(data.getStatTime());
            }
        }
        series.add(new Series<Long>(data1));
        return new ECharts<>(new ArrayList<String>(),axis,series,dataZoom);
    }

    @Override
    public PageResult<Map<String, String>> getFileSecondDetail(Integer page, Integer pageSize, Map<String, String> params) {
        PageResult<Map<String,String>> result = new PageResult<>();
        try {
            PageHelper.startPage(page,pageSize);
            /**
             * 获取生成文件详情，获取字段：生成时间,生成文件大小,生成文件个数
             */
            List<Map<String, String>> lists = radiusMonitorMapper.getFileSecondDetail(params);
            String dateType = params!=null&&params.containsKey("dateType")?params.get("dateType"):"";
            if(lists.size()>0){
                for(Map<String, String> map :lists){

                    /**
                     *  总文件大小单位换算一下
                     */
                    Object fileSize = map!=null&&map.containsKey("file_size")?map.get("file_size"):null;

                    if(fileSize!=null){
                        map.put("file_size",UnitUtils.getValueStrByValue(String.valueOf(fileSize)));
                    }
                    if(map!=null&&map.containsKey("stat_time")) {
                        params = getTimeByType(dateType, map.get("stat_time"));
                        params.put("timeout", LocalConfig.getInstance().getHashValueByHashKey("monitor.file.upload.timeout"));
                        params.put("fileType","1023");
                        /**
                         * 获取字段： 异常文件数
                         */
                        List<Map<String, String>> m = radiusMonitorMapper.getWarnFileSecondCount(params);
                        if(m.size()>0){
                            map.put("warnTypeFileCount",m.get(0).get("warn_type"));
                        }
                    }else{
                        LOG.error("dateType="+dateType+",returnMap="+map);
                    }
                }
            }
            PageInfo<Map<String,String>> pageResult = new PageInfo<>(lists);
            result.setRows(lists);
            result.setTotal(pageResult.getTotal());
        } catch (Exception e) {
            LOG.error(" ",e);
        }
        return result;
    }

    @Override
    public PageResult<Map<String, String>> getFileThirdDetail(Integer page, Integer pageSize, Map<String, String> params) {
        PageResult<Map<String,String>> result = new PageResult<>();
        PageHelper.startPage(page,pageSize);
        List<Map<String, String>> lists = radiusMonitorMapper.getFileThirdDetail(params);
        PageInfo<Map<String,String>> pageResult = new PageInfo<>(lists);
        result.setRows(lists);
        result.setTotal(pageResult.getTotal());
        return result;
    }
    @Override
    public PageResult<Map<String, String>> getWarnFileThirdDetail(Integer pageIndex, Integer pageSize, Map<String, String> params) {
        PageResult<Map<String,String>> result = new PageResult<>();
        PageHelper.startPage(pageIndex,pageSize);
        List<Map<String, String>> lists = radiusMonitorMapper.getWarnFileThirdDetail(params);
        PageInfo<Map<String,String>> pageResult = new PageInfo<>(lists);
        result.setRows(lists);
        result.setTotal(pageResult.getTotal());
        return result;
    }

    @Override
    public List<Map<String, String>> getRadiusRelaySecondDetail(Map<String, String> params) {
        return radiusMonitorMapper.getRadiusRelaySecondDetail(params);
    }

    @Override
    public List<Map<String, String>> getRelayThirdDetail(Map<String, String> params) {
       return radiusMonitorMapper.getRelayThirdDetail(params);
    }

    @Override
    public List<Map<String, String>> getPcapThirdDetail(Map<String, String> params) {
        return radiusMonitorMapper.getPcapThirdDetail(params);
    }

    @Override
    public List<Map<String, String>> getRadiusPolicySecondDetail(Map<String, String> params) {
        return radiusMonitorMapper.getRadiusPolicySecondDetail(params);
    }

    /**
     * @ policy发送包监控第三级详情。
     * @param params
     * @return
     */
    @Override
    public List<Map<String, String>> getPolicyThirdDetail(Map<String, String> params) {
        return radiusMonitorMapper.getPolicyThirdDetail(params);
    }

    public static Map<String,String> getTimeByType(String dateType, String stat_time){
        try {
            Map<String,String>  params = new HashMap<>();
            if(dateType!=null&&"1".equals(dateType)){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Long startTime=format.parse(stat_time).getTime()/1000;
                Long endTime=startTime+600;
                params.put("startTime",startTime+"");
                params.put("endTime",endTime+"");
            }else if(dateType!=null&&"2".equals(dateType)){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
                Long startTime=format.parse(stat_time).getTime()/1000;
                Long endTime=startTime+3600;
                params.put("startTime",startTime+"");
                params.put("endTime",endTime+"");
            }else if(dateType!=null&&"3".equals(dateType)){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Long startTime=format.parse(stat_time).getTime()/1000;
                Long endTime=startTime+3600*24;
                params.put("startTime",startTime+"");
                params.put("endTime",endTime+"");
            }
            return params;
        } catch (ParseException e) {
            LOG.error(" ",e);
        }
        return null;
    }


}
