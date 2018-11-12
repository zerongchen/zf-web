package com.aotain.zongfen.controller.login;


import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.dto.monitor.SftpMonitorDTO;
import com.aotain.zongfen.model.analysis.Params;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.AppTraffic;
import com.aotain.zongfen.model.useranalysis.AppTrafficQueryParam;
import com.aotain.zongfen.service.analysis.AppTrafficAnalysisService;
import com.aotain.zongfen.service.indexpage.IndexPageService;
import com.aotain.zongfen.service.monitor.MonitorTaskAlarmService;
import com.aotain.zongfen.service.useranalysis.AppTrafficServiceImpl;
import com.aotain.zongfen.utils.ColorUtils;
import com.aotain.zongfen.utils.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/indexpage")
public class IndexPageController {

    private static Logger LOG = LoggerFactory.getLogger(IndexPageController.class);

    @Autowired
    private MonitorTaskAlarmService monitorTaskAlarmService;

    @Autowired
    private AppTrafficAnalysisService appTrafficAnalysisService;

    @Autowired
    private AppTrafficServiceImpl appTrafficServiceImpl;

    @Autowired
    private IndexPageService indexPageService;

    @RequestMapping(value = "/initTable")
    @ResponseBody
    public PageResult<MonitorTaskAlarmDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize, MonitorTaskAlarmDTO dto) {

        try {
            Calendar c = Calendar.getInstance();
            String statTime = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            dto.setStartTime(statTime);
            return monitorTaskAlarmService.getList(pageIndex, pageSize, dto);
        } catch (Exception e) {
            LOG.error("error,", e);
        }
        return null;
    }

    /**
     * 流量流向IDC 流入流量占比
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getPie0")
    @ResponseBody
    public List<Map<String, Object>> getPie0(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        try {

            DecimalFormat decimalFormat = new DecimalFormat("######0.0000");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

            Map<String,String> paramMap = new HashMap<>();

            Calendar c = Calendar.getInstance();
            String endTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            c.add(Calendar.DATE, -7);
            String startTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());

            paramMap.put("startTime",startTime);
            paramMap.put("endTime",endTime);
            List<AppTrafficResult> lists = indexPageService.getIdcTraffic(paramMap);
            List<Map<String, Object>> seies = new ArrayList<>();
            Double totalFlow = 0.0;
            Double top9Flow = 0.0;
            if (lists.size() > 0) {
                for (AppTrafficResult m : lists) {
                    totalFlow += m.getTotalFlow();
                }
                for (int i = 0; i < lists.size(); i++) {
                    AppTrafficResult k = lists.get(i);
                    Map<String, Object> keyValue = new HashMap<>();

                    decimalFormat = new DecimalFormat("######0.0000");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    String percent = decimalFormat.format(k.getTotalFlow() / totalFlow);
                    keyValue.put("value", percent);

                    decimalFormat = new DecimalFormat("######0.00");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    keyValue.put("name", k.getSource() + " " + decimalFormat.format(Double.valueOf(percent) * 100) + "%");

                    Map<String,Map<String,String>> itemStylei = new HashMap<>();
                    Map<String,String> colori = new HashMap<>();
                    if (i == 9) {
                        Double single10Flow = totalFlow - top9Flow;
                        decimalFormat = new DecimalFormat("######0.0000");
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                        percent = decimalFormat.format(single10Flow / totalFlow);
                        keyValue.put("value", percent);
                        decimalFormat = new DecimalFormat("######0.00");
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                        keyValue.put("name", "其他省" + " " + decimalFormat.format(Double.valueOf(percent) * 100) + "%");

                        colori.put("color",ColorUtils.colorArr[i]);
                        itemStylei.put("normal",colori);
                        keyValue.put("itemStyle",itemStylei);
                        seies.add(keyValue);
                        break;
                    }
                    colori.put("color", ColorUtils.colorArr[i]);
                    itemStylei.put("normal",colori);
                    keyValue.put("itemStyle",itemStylei);
                    seies.add(keyValue);
                    top9Flow += k.getTotalFlow();
                }
            }
            return seies;
        } catch (Exception e) {
            LOG.error("error,", e);
        }
        return null;
    }

    @RequestMapping(value = "/getBar0")
    @ResponseBody
    public ECharts<Series> getBar0(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        try {

            // 城域网
            int pageType = 1;
            //天粒度
            int dateType = 2;
        //    List<AppTrafficResult> lists = appTrafficAnalysisService.getMainList(params);
            Map<String,String> paramMap = new HashMap<>();
            Calendar c = Calendar.getInstance();
            String endTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            c.add(Calendar.DATE, -7);
            String startTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            paramMap.put("startTime",startTime);
            paramMap.put("endTime",endTime);

            List<AppTrafficResult> lists = indexPageService.getDpiTraffic(paramMap);
            List<String> axis = new ArrayList<String>();
            List<String> upFlow = new ArrayList<String>();
            List<String> downFlow = new ArrayList<String>();
            Double maxFlow = 0.0;
            for (AppTrafficResult k : lists) {
                if (maxFlow < (k.getFlowDn() + k.getFlowUp())) {
                    maxFlow = (k.getFlowDn() + k.getFlowUp());
                }
            }
            String unit = getUnit(maxFlow);
            for (AppTrafficResult k : lists) {
                axis.add(k.getTarget());
                upFlow.add(transferUnit(k.getFlowUp(),unit) + "");
                downFlow.add(transferUnit(k.getFlowDn(),unit) + "");
            }

            Map<String,Map<String,String>> itemStyle1 = new HashMap<>();
            Map<String,String> color1 = new HashMap<>();
            color1.put("color",ColorUtils.colorArr[0]);
            itemStyle1.put("normal",color1);

            Map<String, Object> upMap = new HashMap<>();
            upMap.put("name", "上行流量");
            upMap.put("type", "bar");
            upMap.put("stack", "1");
            upMap.put("data", upFlow);
            upMap.put("itemStyle", itemStyle1);

            Map<String,Map<String,String>> itemStyle2 = new HashMap<>();
            Map<String,String> color2 = new HashMap<>();
            color2.put("color",ColorUtils.colorArr[1]);
            itemStyle2.put("normal",color2);

            Map<String, Object> downMap = new HashMap<>();
            downMap.put("name", "下行流量");
            downMap.put("type", "bar");
            downMap.put("stack", "1");
            downMap.put("data", downFlow);
            downMap.put("itemStyle", itemStyle2);


            List seriesList = new ArrayList();
            seriesList.add(upMap);
            seriesList.add(downMap);
            ECharts<Series> eCharts = new ECharts();
            eCharts.setAxis(axis);
            eCharts.setSeries(seriesList);
            eCharts.setUnit(unit);
            return eCharts;
        } catch (Exception e) {
            LOG.error("error,", e);
        }
        return null;
    }

    @RequestMapping(value = "/getPie1")
    @ResponseBody
    public List<Map<String, Object>> getPie1(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize, AppTrafficQueryParam params) {

        try {


            Map<String,String> paramMap = new HashMap<>();
            Calendar c = Calendar.getInstance();
            String endTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            c.add(Calendar.DATE, -7);
            String startTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            paramMap.put("startTime",startTime);
            paramMap.put("endTime",endTime);

            List<AppTraffic> appDefinedStrategyList = indexPageService.getDpiAppflow(paramMap);

            List<Map<String, Object>> seies = new ArrayList<>();

            Double totalFlow = 0.0;
            Double top9Flow = 0.0;
            if (appDefinedStrategyList.size() > 0) {
                for (AppTraffic m : appDefinedStrategyList) {
                    totalFlow += m.getAppTrafficSum();
                }

                for (int i = 0; i < appDefinedStrategyList.size(); i++) {
                    AppTraffic k = appDefinedStrategyList.get(i);
                    Map<String, Object> keyValue = new HashMap<>();

                    DecimalFormat decimalFormat = new DecimalFormat("######0.0000");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    String percent = decimalFormat.format(k.getAppTrafficSum() / totalFlow);
                    keyValue.put("value", percent);

                    decimalFormat = new DecimalFormat("######0.00");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

                    keyValue.put("name", k.getAppTypeName() + " " + decimalFormat.format(Double.valueOf(percent) * 100) + "%");

                    Map<String,Map<String,String>> itemStylei = new HashMap<>();
                    Map<String,String> colori = new HashMap<>();

                    if (i == 9) {
                        Double single10Flow = totalFlow - top9Flow;
                        decimalFormat = new DecimalFormat("######0.0000");
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                        percent = decimalFormat.format(single10Flow / totalFlow);
                        keyValue.put("value", percent);

                        decimalFormat = new DecimalFormat("######0.00");
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

                        keyValue.put("name", "其他类型" + " " + decimalFormat.format(Double.valueOf(percent) * 100) + "%");

                        colori.put("color", ColorUtils.colorArr[i]);
                        itemStylei.put("normal",colori);
                        keyValue.put("itemStyle",itemStylei);
                        seies.add(keyValue);
                        break;
                    }
                    colori.put("color", ColorUtils.colorArr[i]);
                    itemStylei.put("normal",colori);
                    keyValue.put("itemStyle",itemStylei);
                    seies.add(keyValue);
                    top9Flow += k.getAppTrafficSum();
                }
            }

            return seies;
        } catch (Exception e) {
            LOG.error("error,", e);
        }
        return null;
    }

    @RequestMapping(value = "/getBar1")
    @ResponseBody
    public ECharts<Series> getBar1(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize, AppTrafficQueryParam params) {

        try {
            DecimalFormat decimalFormat = new DecimalFormat("######0.00");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

            Map<String,String> paramMap = new HashMap<>();
            Calendar c = Calendar.getInstance();
            String endTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            c.add(Calendar.DATE, -7);
            String startTime = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            paramMap.put("startTime",startTime);
            paramMap.put("endTime",endTime);

            List<AppTraffic> appDefinedStrategyList = indexPageService.getAppIdAppflow(paramMap);
        //    List<AppTraffic> appDefinedStrategyList = appTrafficServiceImpl.listData(params);
            List<String> axis = new ArrayList<String>();
            List<String> total = new ArrayList<String>();
            Double maxFlow = 0.0;
            for (AppTraffic k : appDefinedStrategyList) {
                if (maxFlow < k.getAppTrafficSum()) {
                    maxFlow = k.getAppTrafficSum() * 1.0;
                }
            }
            String unit = getUnit(maxFlow);
            for (AppTraffic k : appDefinedStrategyList) {
                axis.add(k.getAppNameOfId());
                total.add(transferUnit(k.getAppTrafficSum()/1.0,unit) + "");
            }

            Map<String,Map<String,String>> itemStyle2 = new HashMap<>();
            Map<String,String> color2 = new HashMap<>();
            color2.put("color",ColorUtils.colorArr[0]);
            itemStyle2.put("normal",color2);

            Map<String, Object> totalFLowMap = new HashMap<>();
            totalFLowMap.put("name", "流量");
            totalFLowMap.put("type", "bar");
            totalFLowMap.put("stack", "1");
            totalFLowMap.put("data", total);
            totalFLowMap.put("itemStyle",itemStyle2);

            List seriesList = new ArrayList();
            seriesList.add(totalFLowMap);
            ECharts<Series> eCharts = new ECharts();
            eCharts.setAxis(axis);
            eCharts.setSeries(seriesList);
            eCharts.setUnit(getUnit(maxFlow));
            return eCharts;

        } catch (Exception e) {
            LOG.error("error,", e);
        }
        return null;
    }

    public String getUnit(Double maxSize) {
        if (maxSize < Long.parseLong("1048576")&& maxSize >= Long.parseLong("1024")) {
            return "KB";
        } else if (Long.parseLong("1073741824") > maxSize && maxSize >= Long.parseLong("1048576")) {
            return "M";
        } else if (Long.parseLong("1099511627776") > maxSize && maxSize >= Long.parseLong("1073741824")) {
            return "G";
        } else if (maxSize >= Long.parseLong("1099511627776")) {
            return "T";
        } else {
            return "B";
        }
    }

    public Double transferUnit(Double flowValue, String unit) {
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String realSize = "";

        if (flowValue != null) {
            Long ut = Long.parseLong("1024");
            if ("KB".equals(unit)) {
                realSize = decimalFormat.format(flowValue / ut.longValue());
            } else if ("M".equals(unit)) {
                ut = Long.parseLong("1048576");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("G".equals(unit)) {
                ut = Long.parseLong("1073741824");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("T".equals(unit)) {
                ut = Long.parseLong("1099511627776");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else {
                realSize = flowValue + "";
            }
        }
        if (realSize != "") {
            return Double.valueOf(realSize);
        } else {
            return null;
        }
    }
}
