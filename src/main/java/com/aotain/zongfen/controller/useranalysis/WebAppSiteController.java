package com.aotain.zongfen.controller.useranalysis;

import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.WebFlowQueryParam;
import com.aotain.zongfen.model.useranalysis.WebFlowUbas;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.service.useranalysis.IWebAppSiteService;
import com.aotain.zongfen.utils.export.BaseModel;
import com.aotain.zongfen.utils.export.ExportType;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * web应用站点分析Controller类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/12
 */
@RestController
@RequestMapping("/userbehavioranalysis/webappsite")
public class WebAppSiteController {


    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(WebAppSiteController.class);

    @Autowired
    @Qualifier("webAppSiteServiceImpl")
    private IWebAppSiteService webAppSiteService;

    @Autowired
    private ExportService exportService;

    @RequestMapping(value="/index", method= RequestMethod.GET)
    @RequiresPermission({"Web应用站点分析"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("/userbehavioranalysis/webappsite/index");
        return mv;
    }

    @RequestMapping(value="/idcindex", method= RequestMethod.GET)
    @RequiresPermission({"网站流量分析"})
    public ModelAndView idcIndex(){
        ModelAndView mv = new ModelAndView("/userbehavioranalysis/webflowanalysis/index");
        return mv;
    }

    @RequestMapping("/listData")
    @RequiresPermission({"zf201003_query"})
    public PageResult listData(@RequestBody WebFlowQueryParam webFlowQueryParam) {
        PageResult pageResult;
        try{
            Page<WebFlowUbas> result = webAppSiteService.listData(webFlowQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping("/listData2")
    @RequiresPermission({"zf201003_query"})
    public PageResult listData2(@RequestBody WebFlowQueryParam webFlowQueryParam) {
        PageResult pageResult;
        try{
            Page<WebFlowUbas> result = webAppSiteService.listWebSiteData(webFlowQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping("/getEChartsData")
    @RequiresPermission({"zf201003_query"})
    public ECharts<Series> getChartData(@RequestBody WebFlowQueryParam appTrafficQueryParam){
        try{
            List<WebFlowUbas> appTrafficChartBeans = webAppSiteService.getChartData(appTrafficQueryParam);
            ECharts<Series> eCharts = webAppSiteService.wrapChartData(appTrafficChartBeans);
            return eCharts;
        } catch (Exception e){
            logger.error("there is some error ",e);
            return null;
        }

    }

    @RequestMapping("/getLineData")
    @RequiresPermission({"zf201003_query"})
    public ECharts<Series> getLineData(@RequestBody WebFlowQueryParam appTrafficQueryParam){
        try{
            List<WebFlowUbas> appTrafficChartBeans = webAppSiteService.getLineData(appTrafficQueryParam);
            ECharts<Series> eCharts = webAppSiteService.wrapLineData(appTrafficQueryParam,appTrafficChartBeans);
            return eCharts;
        } catch (Exception e){
            logger.error("there is some error ",e);
            return null;
        }

    }

    @RequiresPermission({"zf201003_export"})
    @RequestMapping("/exportData")
    @LogAnnotation(module = 201003,type = OperationConstants.OPERATION_EXPORT)
    public ResponseResult exportData(HttpServletRequest httpServletRequest, HttpServletResponse response){
        try{
            ResponseResult result = handleExport(httpServletRequest,response,0);
            try{
                Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));
                //默认导出excel
                ExportType g = ExportType.valueOf(exportType);
                String dataJson = "fileName=Web应用站点分析"+g.getSuffix();
                ProxyUtil.changeVariable(WebAppSiteController.class,"exportData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return result;
        } catch (Exception e){
            logger.error("export failed...",e);
            return null;
        }
    }

    @RequiresPermission({"zf201003_export"})
    @RequestMapping("/exportIdcData")
//    @LogAnnotation(module = 201003,type = OperationConstants.OPERATION_EXPORT)
    public ResponseResult exportIdcData(HttpServletRequest httpServletRequest,HttpServletResponse response){
        try{
            ResponseResult result = handleExport(httpServletRequest,response,1);
            return result;
        } catch (Exception e){
            logger.error("export failed...",e);
            return null;
        }
    }

    public ResponseResult handleExport(HttpServletRequest httpServletRequest,HttpServletResponse response,int type) throws SQLException{
        BaseModel<WebFlowUbas> baseModel = new BaseModel();
        String imgUrl = httpServletRequest.getParameter("imgUrl");
        Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));

        WebFlowQueryParam webFlowQueryParam = new WebFlowQueryParam();


        // 用来区分导出的是第一个table还是第二个页面的table
        Integer listType = 1;
        if (httpServletRequest.getParameter("listType")!=null){
            listType  = Integer.valueOf(httpServletRequest.getParameter("listType"));
            webFlowQueryParam.setListType(listType);
        }

        if (httpServletRequest.getParameter("probeType")!=null){
            int probeType = Integer.valueOf(httpServletRequest.getParameter("probeType"));
            webFlowQueryParam.setProbeType(probeType);
        } else {
            webFlowQueryParam.setProbeType(0);
        }

        if (httpServletRequest.getParameter("startTime")!=null){
            long startTime = Long.valueOf(httpServletRequest.getParameter("startTime"));
            webFlowQueryParam.setStartTime(startTime);
        }
        if (httpServletRequest.getParameter("statType")!=null){
            int statType = Integer.valueOf(httpServletRequest.getParameter("statType"));
            webFlowQueryParam.setStatType(statType);
        }
        if (httpServletRequest.getParameter("areaId")!=null){
            String areaId = httpServletRequest.getParameter("areaId");
            webFlowQueryParam.setAreaId(areaId);
        }
        if (httpServletRequest.getParameter("userGroupNo")!=null){
            long userGroupNo = Long.valueOf(httpServletRequest.getParameter("userGroupNo"));
            webFlowQueryParam.setUserGroupNo(userGroupNo);
        }
        if (httpServletRequest.getParameter("siteType")!=null){
            int siteType = Integer.valueOf(httpServletRequest.getParameter("siteType"));
            webFlowQueryParam.setSiteType(siteType);
        }
        if (httpServletRequest.getParameter("siteName")!=null){
            String siteName = httpServletRequest.getParameter("siteName");
            webFlowQueryParam.setSiteName(siteName);
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
        List<WebFlowUbas> dataLists = Lists.newArrayList();
        // 表格1
        if (listType==1) {
            headers = new String[]{"Host","网站类型","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","点击次数"};
            fields = new String[]{"siteName","siteTypeName","siteTrafficUp","siteTrafficDn","siteTrafficSum","siteHitFreq"};
            dataLists = webAppSiteService.getExportData(webFlowQueryParam);
        } else if (listType==2){
            headers = new String[]{"统计时间","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","点击次数"};
            fields = new String[]{"statTime","siteTrafficUp","siteTrafficDn","siteTrafficSum","siteHitFreq"};
            dataLists = webAppSiteService.getLineData(webFlowQueryParam);
        }
        baseModel.setHeaders(headers);
        baseModel.setFields(fields);
        baseModel.setDatas(dataLists);

        if (type==0){
            baseModel.setFileName("Web应用站点分析");
        } else if (type == 1 ) {
            baseModel.setFileName("网站流量分析");
        }

        exportService.export(baseModel,response,httpServletRequest);
        return new ResponseResult(0,"导出成功",null,null,null);
    }

    @RequestMapping("/getSiteTypeByName")
    public Map getAppTypeByName(String siteTypeName){
        Map<String,Object> queryMap = Maps.newHashMap();
        queryMap.put("siteName",siteTypeName.trim());
        long siteType = webAppSiteService.getSiteTypeByName(queryMap);
        queryMap.put("siteType",siteType);
        return queryMap;
    }
}
