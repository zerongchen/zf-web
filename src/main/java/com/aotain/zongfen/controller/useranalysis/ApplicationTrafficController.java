package com.aotain.zongfen.controller.useranalysis;

import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.AppTraffic;
import com.aotain.zongfen.model.useranalysis.AppTrafficChartBean;
import com.aotain.zongfen.model.useranalysis.AppTrafficQueryParam;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.service.common.MultiSelectService;
import com.aotain.zongfen.service.useranalysis.IAppTrafficService;
import com.aotain.zongfen.utils.export.BaseModel;
import com.aotain.zongfen.utils.export.ExportType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 应用流量分析controller类
 *
 * @author daiyh@aotain.com
 * @date 2018/04/23
 */
@RequestMapping("/userbehavioranalysis/apptraffic")
@Controller
public class ApplicationTrafficController extends BaseController{


    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTrafficController.class);

    @Autowired
    private IAppTrafficService appTrafficService;

    @Autowired
    private MultiSelectService multiSelectService;

    @Autowired
    private ExportService exportService;

    @RequestMapping("/index")
    @RequiresPermission({"全应用流量分析"})
    public String index() {
        return "/userbehavioranalysis/apptraffic/index";
    }

    @RequestMapping("/idcindex")
    @RequiresPermission({"IDC全应用流量分析"})
    public String idcIndex() {
        return "/userbehavioranalysis/idctraffic/index";
    }

    @RequestMapping("/listData")
    @ResponseBody
    @RequiresPermission({"zf201001_query"})
    public PageResult listData(@RequestBody AppTrafficQueryParam appTrafficQueryParam) {
        PageHelper.startPage(appTrafficQueryParam.getPage(),appTrafficQueryParam.getPageSize());
        List<AppTraffic> appDefinedStrategyList = appTrafficService.listData(appTrafficQueryParam);
        PageInfo pageInfo = new PageInfo(appDefinedStrategyList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @RequestMapping("/listIdcData")
    @ResponseBody
    @RequiresPermission({"zf202001_query"})
    public PageResult listIdcData(@RequestBody AppTrafficQueryParam appTrafficQueryParam) {
        PageHelper.startPage(appTrafficQueryParam.getPage(),appTrafficQueryParam.getPageSize());
        List<AppTraffic> appDefinedStrategyList = appTrafficService.listData(appTrafficQueryParam);
        PageInfo pageInfo = new PageInfo(appDefinedStrategyList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @RequiresPermission({"zf201001_query"})
    @RequestMapping("/listDefinedAppIdData")
    @ResponseBody
    public PageResult listDefinedAppIdData(@RequestBody AppTrafficQueryParam appTrafficQueryParam) {
        PageHelper.startPage(appTrafficQueryParam.getPage(),appTrafficQueryParam.getPageSize());
        List<AppTraffic> appDefinedStrategyList = appTrafficService.listAppIdData(appTrafficQueryParam);
        PageInfo pageInfo = new PageInfo(appDefinedStrategyList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @RequiresPermission({"zf202001_query"})
    @RequestMapping("/listIdcDefinedAppIdData")
    @ResponseBody
    public PageResult listIdcDefinedAppIdData(@RequestBody AppTrafficQueryParam appTrafficQueryParam) {
        PageHelper.startPage(appTrafficQueryParam.getPage(),appTrafficQueryParam.getPageSize());
        List<AppTraffic> appDefinedStrategyList = appTrafficService.listAppIdData(appTrafficQueryParam);
        PageInfo pageInfo = new PageInfo(appDefinedStrategyList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }


    @RequestMapping("/getAreaList")
    @ResponseBody
    public Map listAreaList() {
        List<ChinaArea> chinaAreaList = appTrafficService.getAreaList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("rows",chinaAreaList);
        return map;

    }

    @RequestMapping("/getUserGroup")
    @ResponseBody
    public Map listUserGroup() {
        List<Multiselect> multiselects = multiSelectService.getUserGroup();
        Map<String, Object> map = Maps.newHashMap();
        map.put("rows",multiselects);
        return map;
    }

    @RequiresPermission({"zf201001_query"})
    @RequestMapping("/getEChartsData")
    @ResponseBody
    public ECharts<Series> getChartData(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getChartData(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapChartData(appTrafficQueryParam.getStatType(),appTrafficChartBeans,false);
        return eCharts;
    }

    @RequiresPermission({"zf202001_query"})
    @RequestMapping("/getIdcEChartsData")
    @ResponseBody
    public ECharts<Series> getIdcEChartsData(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getChartData(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapChartData(appTrafficQueryParam.getStatType(),appTrafficChartBeans,false);
        return eCharts;
    }


    @RequiresPermission({"zf201001_query"})
    @RequestMapping("/getSecondEChartsData")
    @ResponseBody
    public ECharts<Series> getChartDataByAppType(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getChartDataByAppType(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapChartData(appTrafficQueryParam.getStatType(),appTrafficChartBeans,true);
        return eCharts;
    }

    @RequiresPermission({"zf202001_query"})
    @RequestMapping("/getIdcSecondEChartsData")
    @ResponseBody
    public ECharts<Series> getIdcChartDataByAppType(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getChartDataByAppType(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapChartData(appTrafficQueryParam.getStatType(),appTrafficChartBeans,true);
        return eCharts;
    }

    @RequiresPermission({"zf201001_query"})
    @RequestMapping("/getLineData")
    @ResponseBody
    public ECharts<Series> getLineData(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getLineDataByAppId(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapLineData(appTrafficQueryParam,appTrafficChartBeans);
        return eCharts;
    }

    @RequiresPermission({"zf202001_query"})
    @RequestMapping("/geIdctLineData")
    @ResponseBody
    public ECharts<Series> geIdctLineData(@RequestBody AppTrafficQueryParam appTrafficQueryParam){
        List<AppTrafficChartBean> appTrafficChartBeans = appTrafficService.getLineDataByAppId(appTrafficQueryParam);
        ECharts<Series> eCharts = appTrafficService.wrapLineData(appTrafficQueryParam,appTrafficChartBeans);
        return eCharts;
    }


    @RequestMapping("/getAppIdByName")
    @ResponseBody
    public Map getAppIdByName(String appIdName,Long appType){
        Map<String,Object> queryMap = Maps.newHashMap();
        queryMap.put("appIdName",appIdName);
        queryMap.put("appType",appType);
        long appId = appTrafficService.getAppIdByName(queryMap);
        queryMap.put("appId",appId);
        return queryMap;
    }

    @RequestMapping("/getAppTypeByName")
    @ResponseBody
    public Map getAppTypeByName(String appTypeName){
        Map<String,Object> queryMap = Maps.newHashMap();
        queryMap.put("appTypeName",appTypeName);
        long appType = appTrafficService.getAppTypeByName(queryMap);
        queryMap.put("appType",appType);
        return queryMap;
    }

    @RequiresPermission({"zf201001_export"})
    @RequestMapping("/exportData")
    @ResponseBody
    @LogAnnotation(module = 201001,type = OperationConstants.OPERATION_EXPORT)
    public ResponseResult exportData(HttpServletRequest httpServletRequest,HttpServletResponse response){
        try{
            ResponseResult result = handlExport(httpServletRequest,response,0);
            try{
                Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));
                //默认导出excel
                ExportType g = ExportType.valueOf(exportType);
                String dataJson = "fileName=城域网全应用流量分析"+g.getSuffix();
                ProxyUtil.changeVariable(ApplicationTrafficController.class,"exportData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return result;
        } catch (Exception e){
            logger.error("export failed...",e);
            return null;
        }
    }

    @RequiresPermission({"zf202001_export"})
    @RequestMapping("/exportIdcData")
    @ResponseBody
    @LogAnnotation(module = 202001,type = OperationConstants.OPERATION_EXPORT)
    public ResponseResult exportIdcData(HttpServletRequest httpServletRequest,HttpServletResponse response){
        try{
            ResponseResult result = handlExport(httpServletRequest,response,1);
            try{
                Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));
                //默认导出excel
                ExportType g = ExportType.valueOf(exportType);

                String dataJson = "fileName=IDC全应用流量分析"+g.getSuffix();
                ProxyUtil.changeVariable(ApplicationTrafficController.class,"exportIdcData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return result;
        } catch (Exception e){
            logger.error("export failed...",e);
            return null;
        }
    }

    private ResponseResult handlExport(HttpServletRequest httpServletRequest,HttpServletResponse response,int type){
        BaseModel<AppTraffic> baseModel = new BaseModel();
        String imgUrl = httpServletRequest.getParameter("imgUrl");
        Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));
        Integer tableType = Integer.valueOf(httpServletRequest.getParameter("tableType"));

        AppTrafficQueryParam appTrafficQueryParam = new AppTrafficQueryParam();

        if (httpServletRequest.getParameter("listType")!=null){
            Integer listType = Integer.valueOf(httpServletRequest.getParameter("listType"));
            appTrafficQueryParam.setListType(listType);
        }

        if (httpServletRequest.getParameter("probeType")!=null){
            int probeType = Integer.valueOf(httpServletRequest.getParameter("probeType"));
            appTrafficQueryParam.setProbeType(probeType);
        } else {
            appTrafficQueryParam.setProbeType(0);
        }

        if (httpServletRequest.getParameter("startTime")!=null){
            long startTime = Long.valueOf(httpServletRequest.getParameter("startTime"));
            appTrafficQueryParam.setStartTime(startTime);
        }
        if (httpServletRequest.getParameter("dateType")!=null){
            int dateType = Integer.valueOf(httpServletRequest.getParameter("dateType"));
            appTrafficQueryParam.setDateType(dateType);
        }
        if (httpServletRequest.getParameter("areaId")!=null){
            String areaId = httpServletRequest.getParameter("areaId");
            appTrafficQueryParam.setAreaId(areaId);
        }
        if (httpServletRequest.getParameter("userGroupNo")!=null){
            long userGroupNo = Long.valueOf(httpServletRequest.getParameter("userGroupNo"));
            appTrafficQueryParam.setUserGroupNo(userGroupNo);
        }
        if (httpServletRequest.getParameter("appType")!=null){
            long appType = Long.valueOf(httpServletRequest.getParameter("appType"));
            appTrafficQueryParam.setAppType(appType);
        }
        if (httpServletRequest.getParameter("appId")!=null){
            long appId = Long.valueOf(httpServletRequest.getParameter("appId"));
            appTrafficQueryParam.setAppId(appId);
        }


        if (exportType==null){
            exportType = 1;
        }
        baseModel.setExportType(exportType);

        String value = StringUtils.split(imgUrl)[1];
        List<String> imgUrls = Lists.newArrayList();
        imgUrls.add(value);
        baseModel.setDataURLs(imgUrls);

        String[] headers = {};
        String[] fields = {};
        List<AppTraffic> dataLists = Lists.newArrayList();
        // 表格1
        if (tableType==1) {
            headers = new String[]{"应用分类","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","用户数","报文数（次）","并发session数（次","新建session数(次)"};
            fields = new String[]{"appTypeName","appTrafficUp","appTrafficDn","appTrafficSum","appUserNum","appPacketsNum","appSessionsNum","appNewSessionNum"};
            dataLists = appTrafficService.listData(appTrafficQueryParam);
        } else if (tableType==2){
            headers = new String[]{"应用名称","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","用户数","报文数（次）","并发session数（次","新建session数(次)"};
            fields = new String[]{"appNameOfId","appTrafficUp","appTrafficDn","appTrafficSum","appUserNum","appPacketsNum","appSessionsNum","appNewSessionNum"};
            dataLists = appTrafficService.listData(appTrafficQueryParam);
        } else if (tableType==3){
            headers = new String[]{"统计时间","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","用户数","报文数（次）","并发session数（次","新建session数(次)"};
            fields = new String[]{"statTime","appTrafficUp","appTrafficDn","appTrafficSum","appUserNum","appPacketsNum","appSessionsNum","appNewSessionNum"};
            dataLists = appTrafficService.listAppIdData(appTrafficQueryParam);
        }
        baseModel.setHeaders(headers);
        baseModel.setFields(fields);
        baseModel.setDatas(dataLists);

        if (type==0){
            baseModel.setFileName("全应用流量分析");
        } else if (type == 1 ) {
            baseModel.setFileName("IDC全应用流量分析");
        }

        exportService.export(baseModel,response,httpServletRequest);
        return new ResponseResult(0,"导出成功",null,null,null);
    }
}
