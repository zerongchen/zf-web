package com.aotain.zongfen.controller.useranalysis;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.login.support.Authority;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.WebFlowQueryParam;
import com.aotain.zongfen.model.useranalysis.WebFlowUbas;
import com.aotain.zongfen.service.common.CommonServiceImpl;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.service.export.HiveExportService;
import com.aotain.zongfen.service.useranalysis.IWebAppSiteService;
import com.aotain.zongfen.utils.IPUtil;
import com.aotain.zongfen.utils.export.BaseModel;
import com.aotain.zongfen.utils.export.ExportType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网站流量分析Controller类(方法与WebAppSiteController一致,只是为了方便权限控制)
 * @see WebAppSiteController
 *
 * @author daiyh@aotain.com
 * @date 2018/06/19
 */
@RestController
@RequestMapping("/userbehavioranalysis/webflowanalysis")
public class WebFlowAnalysisController {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(WebFlowAnalysisController.class);

    @Autowired
    private IWebAppSiteService webAppSiteServiceImpl;

    @Autowired
    private HiveExportService hiveExportServiceImpl;

    @Autowired
    private CommonServiceImpl commonServiceImpl;

    @Autowired
    private ExportService exportService;

    @RequestMapping(value="/index", method= RequestMethod.GET)
    @RequiresPermission({"网站流量分析"})
    public ModelAndView idcIndex(){
        ModelAndView mv = new ModelAndView("/userbehavioranalysis/webflowanalysis/index");
        return mv;
    }

    @RequestMapping("/listData")
    @RequiresPermission({"zf202003_query"})
    public PageResult listData(@RequestBody WebFlowQueryParam webFlowQueryParam) {
        PageResult pageResult;
        try{
            Page<WebFlowUbas> result = webAppSiteServiceImpl.listData(webFlowQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping("/listData2")
    @RequiresPermission({"zf202003_query"})
    public PageResult listData2(@RequestBody WebFlowQueryParam webFlowQueryParam) {
        PageResult pageResult;
        try{
            Page<WebFlowUbas> result = webAppSiteServiceImpl.listWebSiteData(webFlowQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping("/getEChartsData")
    @RequiresPermission({"zf202003_query"})
    public ECharts<Series> getChartData(@RequestBody WebFlowQueryParam appTrafficQueryParam){
        try{
            List<WebFlowUbas> appTrafficChartBeans = webAppSiteServiceImpl.getChartData(appTrafficQueryParam);
            ECharts<Series> eCharts = webAppSiteServiceImpl.wrapChartData(appTrafficChartBeans);
            return eCharts;
        } catch (Exception e){
            logger.error("there is some error ",e);
            return null;
        }

    }

    @RequestMapping("/getLineData")
    @RequiresPermission({"zf202003_query"})
    public ECharts<Series> getLineData(@RequestBody WebFlowQueryParam appTrafficQueryParam){
        try{
            List<WebFlowUbas> appTrafficChartBeans = webAppSiteServiceImpl.getLineData(appTrafficQueryParam);
            ECharts<Series> eCharts = webAppSiteServiceImpl.wrapLineData(appTrafficQueryParam,appTrafficChartBeans);
            return eCharts;
        } catch (Exception e){
            logger.error("there is some error ",e);
            return null;
        }

    }

    @RequestMapping("/exportIdcData")
    @RequiresPermission({"zf202003_export"})
    @LogAnnotation(module = 202003,type = OperationConstants.OPERATION_EXPORT)
    public ResponseResult exportIdcData(HttpServletRequest httpServletRequest,HttpServletResponse response){
        try{
            ResponseResult result = handlExport(httpServletRequest,response,1);
            try{
                Integer exportType = Integer.valueOf(httpServletRequest.getParameter("exportType"));
                //默认导出excel
                ExportType g = ExportType.valueOf(exportType);

                String dataJson = "fileName=网站流量分析"+g.getSuffix();
                ProxyUtil.changeVariable(WebFlowAnalysisController.class,"exportIdcData",dataJson);
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
            return result;
        } catch (Exception e){
            logger.error("export failed...",e);
            return null;
        }
    }


    public ResponseResult handlExport(HttpServletRequest httpServletRequest,HttpServletResponse response,int type) throws SQLException {
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
            dataLists = webAppSiteServiceImpl.getExportData(webFlowQueryParam);
        } else if (listType==2){
            headers = new String[]{"统计时间","上行流量(Kb)","下行流量(Kb)","总流量(Kb)","点击次数"};
            fields = new String[]{"statTime","siteTrafficUp","siteTrafficDn","siteTrafficSum","siteHitFreq"};
            dataLists = webAppSiteServiceImpl.getLineData(webFlowQueryParam);
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



    @RequestMapping("/addExportTask")
    @RequiresPermission({"zf202003_export"})
    public Map<String,Object> addExportTask(HttpServletRequest httpServletRequest){
        Map<String,Object> returnMap = new HashMap<>();
        try{

            int user_id = Authority.getUserDetailInfo(httpServletRequest) != null ? Authority.getUserDetailInfo(httpServletRequest).getUserId() : 0;
            String user_ip= IPUtil.getIpAddress(httpServletRequest);
            String file_name = httpServletRequest.getParameter("file_name");

            String export_areaId = httpServletRequest.getParameter("export_areaId");
            String export_userGroupNo = httpServletRequest.getParameter("export_userGroupNo");
            String export_siteType = httpServletRequest.getParameter("export_siteType");
            String export_siteName = httpServletRequest.getParameter("export_siteName");
            String export_startTime = httpServletRequest.getParameter("export_startTime");
            String dateType = httpServletRequest.getParameter("dateType");

            export_startTime=export_startTime.replaceAll("-","");

            StringBuilder where_sql = new StringBuilder();
            String tableName="";
            if("1".equals(dateType)){
                tableName="job_ubas_webflow_h";
                String[] dthour=export_startTime.split(" ");
                where_sql.append(" and dt='").append(dthour[0]).append("' and hour=").append(dthour[1]).append(" ");
            }else if("2".equals(dateType)){
                tableName="job_ubas_webflow_d";
                where_sql.append(" and dt='").append(export_startTime).append("' ");
            }else if("3".equals(dateType)){
                tableName="job_ubas_webflow_w";
                where_sql.append(" and dt='").append(export_startTime).append("' ");
            }else if("4".equals(dateType)){
                tableName="job_ubas_webflow_m";
                export_startTime=export_startTime+"01";
                where_sql.append(" and dt='").append(export_startTime).append("' ");
            }else{
                tableName="job_ubas_webflow_d";
            }

            StringBuilder group_sql = new StringBuilder();
            group_sql.append(" group by sitetype,sitename");
            if(!"-1".equals(export_siteType)&& !org.springframework.util.StringUtils.isEmpty(export_siteType)){
                where_sql.append(" and sitetype=").append(export_siteType).append(" ");
            }
            if(!"-1".equals(export_siteName)&& !org.springframework.util.StringUtils.isEmpty(export_siteName)){
                where_sql.append(" and sitename='").append(export_siteName).append("' ");
            }
            if(!"-1".equals(export_areaId)){
                where_sql.append(" and area_id=").append(export_areaId).append(" ");
            }
            if(!"-1".equals(export_userGroupNo)){
                where_sql.append(" and usergroupno=").append(export_userGroupNo).append(" ");
            }


            StringBuilder sb = new StringBuilder();
            sb.append("select "+export_startTime+",t1.sitename,t2.web_name,t1.sitetraffic_up,t1.sitetraffic_dn,t1.sitetraffic_sum,t1.sitehitfreq from (");
            sb.append(" select sitename ");
            sb.append(" ,sitetype ");
            sb.append(" ,sum(sitetraffic_up) as sitetraffic_up ");
            sb.append(" ,sum(sitetraffic_dn) as sitetraffic_dn ");
            sb.append(" ,sum(sitetraffic_up)+sum(sitetraffic_dn) as sitetraffic_sum ");
            sb.append(" ,sum(sitehitfreq) as sitehitfreq ");
            sb.append(" from ").append(tableName).append(" where 1=1 ");
            sb.append(where_sql).append(group_sql);
            sb.append(")t1 left join zf_dict_webtype t2 on t2.web_type=t1.sitetype");
            String hive_sql = sb.toString();

            logger.info("addExportTask,sql:"+hive_sql);
            Map<String,Object> params = new HashMap<>();
            params.put("file_name",file_name);
            params.put("download_status",0);
            params.put("file_type",256);
            params.put("export_status",0);
            params.put("hive_sql",hive_sql);
            params.put("header","时间,Host,网站类型,上行流量(Kb),下行流量(Kb),总流量(Kb),点击次数");
            params.put("create_time",new Date());
            params.put("user_id",user_id);
            params.put("user_ip",user_ip);
            boolean b = hiveExportServiceImpl.addExportTask(params);

            if(b){
                returnMap.put("status","0");
            }else{
                returnMap.put("status","1");
            }
        } catch (Exception e){
            logger.error("addExportTask failed...",e);
            returnMap.put("status","2");
        }
        return returnMap;
    }


    @RequestMapping("/deleteExportTask")
    @RequiresPermission({"zf202003_export"})
    public Map<String,Object> deleteExportTask(HttpServletRequest httpServletRequest){
        Map<String,Object> returnMap = new HashMap<>();
        try{
            String id = httpServletRequest.getParameter("taskId");

            Map<String,Object> params = new HashMap<>();
            params.put("id",id);

            boolean b = hiveExportServiceImpl.deleteExportTask(params);
            if(b){
                returnMap.put("status","0");
            }else{
                returnMap.put("status","1");
            }
        } catch (Exception e){
            logger.error("deleteExportTask failed...",e);
            returnMap.put("status","2");
        }
        return returnMap;
    }

    @RequestMapping(value = "/selectExportTask",method = {RequestMethod.POST,RequestMethod.GET})
    @RequiresPermission({"zf202003_query"})
    public com.aotain.zongfen.utils.PageResult<Map<String, String>> selectExportTask(HttpServletRequest httpServletRequest,
                                                                                   @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            String download_status =httpServletRequest.getParameter("download_status");
            String file_type =httpServletRequest.getParameter("file_type");
            Map<String,String> params = new HashMap<>();
            params.put("download_status",download_status);
            params.put("file_type",file_type);
            List<Map<String,String>> lists = hiveExportServiceImpl.selectExportTask(pageIndex,pageSize,params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            com.aotain.zongfen.utils.PageResult pageResult = new com.aotain.zongfen.utils.PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        } catch (Exception e) {
            logger.error("error,", e);
            return new com.aotain.zongfen.utils.PageResult<Map<String,String>>();
        }
    }

    @RequestMapping("downloadFile")
    @ResponseBody
    public void downloadFile(HttpServletRequest request, HttpServletResponse response){
        try {
            String file_name = request.getParameter("file_name");
            String id = request.getParameter("id");
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            params.put("download_status",1);
            boolean b = hiveExportServiceImpl.updateDownloadFile(params);
            String export_path= LocalConfig.getInstance().getHashValueByHashKey("export.upload.sftp.path");
            commonServiceImpl.exportTemplete(request,response,file_name+"",export_path+"");
        } catch (Exception e) {
            logger.error("error,", e);
        }
    }
}
