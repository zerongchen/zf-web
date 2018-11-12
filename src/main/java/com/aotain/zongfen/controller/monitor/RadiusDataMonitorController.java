package com.aotain.zongfen.controller.monitor;

import com.aotain.common.config.LocalConfig;
import com.aotain.zongfen.dto.monitor.MonitorOnlineuserDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusFileDetailDTO;
import com.aotain.zongfen.dto.monitor.RadiusMonitorDTO;
import com.aotain.zongfen.dto.monitor.RadiusParamDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.monitor.RadiusPcapDetail;
import com.aotain.zongfen.model.monitor.RadiusPolicyDetail;
import com.aotain.zongfen.model.monitor.RadiusRelayDetail;
import com.aotain.zongfen.service.monitor.RaduisService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/aaa")
public class RadiusDataMonitorController {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(RadiusDataMonitorController.class);

    @Autowired
    private RaduisService raduisService;


    @RequestMapping("index")
    @RequiresPermission("AAA数据监控")
    public String index() {
        return "/datamonitor/a_index";
    }

    @RequestMapping("getOnlineUserList")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<MonitorOnlineuserDetailDTO> getOnlineUserList( MonitorOnlineuserDetailDTO recode,
                                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            PageHelper.startPage(page, pageSize);
            List<MonitorOnlineuserDetailDTO> lists = raduisService.getOnlineUserList(recode);
            PageInfo<MonitorOnlineuserDetailDTO> pageInfo = new PageInfo<MonitorOnlineuserDetailDTO>(lists);
            PageResult<MonitorOnlineuserDetailDTO> pageResult = new PageResult<MonitorOnlineuserDetailDTO>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("get online list error",e);
            return new PageResult<MonitorOnlineuserDetailDTO>(0l,new ArrayList<>());
        }

    }
    @RequestMapping("getEChartsOnlineList")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public ECharts<Series<Long>> getEChartsOnlineList(@RequestBody MonitorOnlineuserDetailDTO recode){
        try {

            return raduisService.getEchartsData(recode);
        }catch (Exception e){
            logger.error("getEChartsOnlineList error",e);
            return new ECharts<>();
        }
    }

    @RequestMapping("getList")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<RadiusMonitorDTO> getList( RadiusMonitorDTO recode,
                                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        try {
            PageHelper.startPage(page, pageSize);
            List<RadiusMonitorDTO> lists = raduisService.getList(recode);
            PageInfo<RadiusMonitorDTO> pageInfo = new PageInfo<RadiusMonitorDTO>(lists);
            PageResult<RadiusMonitorDTO> pageResult = new PageResult<RadiusMonitorDTO>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("get 3A list error ",e);
            return new PageResult<RadiusMonitorDTO>(0l,new ArrayList<>());
        }

    }

    @RequestMapping("getEChartsList")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public List<ECharts<Series>> getEChartsList(@RequestBody RadiusMonitorDTO recode){
        try {
            return raduisService.getEchartsData(recode);
        }catch (Exception e){
            logger.error("get 3a echarts list error ",e);
            return null;
        }
    }


    @RequestMapping("getRadiusPcapDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<RadiusPcapDetail> getRadiusPcapDetail( RadiusParamDTO recode,
                                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            PageHelper.startPage(page, pageSize);
            List<RadiusPcapDetail> lists = raduisService.getRadiusPcapDetail(recode);
            PageInfo<RadiusPcapDetail> pageInfo = new PageInfo<RadiusPcapDetail>(lists);
            PageResult<RadiusPcapDetail> pageResult = new PageResult<RadiusPcapDetail>(pageInfo.getTotal(),lists);
            return pageResult;
        }catch (Exception e){
            logger.error("get 3A Pcap Detail error ",e);
            return new PageResult<RadiusPcapDetail>(0l,new ArrayList<>());
        }
    }


    @RequestMapping("getPcapThirdDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getPcapThirdDetail(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,HttpServletRequest request){

        try {
            Map<String,String> params = new HashMap<>();
            String startTime= request.getParameter("startTime");
            String endTime= request.getParameter("endTime");
            String dateType= request.getParameter("dateType");

            if(dateType!=null&&"1".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"2".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"3".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime()/1000+"";
            }

            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("dateType",dateType);

            PageHelper.startPage(pageIndex, pageSize);
            List<Map<String,String>> lists = raduisService.getPcapThirdDetail(params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            PageResult<Map<String,String>> pageResult = new PageResult<Map<String,String>>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("getPcapThirdDetail error ",e);
            return new PageResult<Map<String,String>>(0l,new ArrayList<>());
        }
    }

    @RequestMapping("getRadiusRelaySecondDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getRadiusRelaySecondDetail( RadiusParamDTO recode,
                                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,HttpServletRequest request){

        try {
            Map<String,String> params = new HashMap<>();
            String startTime= request.getParameter("startTime");
            String endTime= request.getParameter("endTime");
            String dateType= request.getParameter("dateType");
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("dateType",dateType);

            PageHelper.startPage(page, pageSize);
            List<Map<String,String>> lists = raduisService.getRadiusRelaySecondDetail(params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            PageResult<Map<String,String>> pageResult = new PageResult<Map<String,String>>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("getRadiusRelaySecondDetail error ",e);
            return new PageResult<Map<String,String>>(0l,new ArrayList<>());
        }
    }
    @RequestMapping("getRelayThirdDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getRelayThirdDetail(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,HttpServletRequest request){

        try {
            Map<String,String> params = new HashMap<>();
            String startTime= request.getParameter("startTime");
            String endTime= request.getParameter("endTime");
            String dateType= request.getParameter("dateType");

            if(dateType!=null&&"1".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"2".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"3".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime()/1000+"";
            }

            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("dateType",dateType);

            PageHelper.startPage(pageIndex, pageSize);
            List<Map<String,String>> lists = raduisService.getRelayThirdDetail(params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            PageResult<Map<String,String>> pageResult = new PageResult<Map<String,String>>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("getRelayThirdDetail error ",e);
            return new PageResult<Map<String,String>>(0l,new ArrayList<>());
        }
    }

    @RequestMapping("getRadiusPolicySecondDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getRadiusPolicySecondDetail(HttpServletRequest request,
                                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        try {
            Map<String,String> params = new HashMap<>();
            String startTime= request.getParameter("startTime");
            String endTime= request.getParameter("endTime");
            String dateType= request.getParameter("dateType");
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("dateType",dateType);

            PageHelper.startPage(page, pageSize);
            List<Map<String,String>> lists = raduisService.getRadiusPolicySecondDetail(params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            PageResult<Map<String,String>> pageResult = new PageResult<Map<String,String>>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("get 3A Policy Detail error ",e);
            return new PageResult<Map<String,String>>(0l,new ArrayList<>());
        }
    }

    @RequestMapping("getPolicyThirdDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getPolicyThirdDetail(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,HttpServletRequest request){

        try {
            Map<String,String> params = new HashMap<>();
            String startTime= request.getParameter("startTime");
            String endTime= request.getParameter("endTime");
            String dateType= request.getParameter("dateType");

            if(dateType!=null&&"1".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"2".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"3".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime()/1000+"";
            }

            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("dateType",dateType);

            PageHelper.startPage(pageIndex, pageSize);
            List<Map<String,String>> lists = raduisService.getPolicyThirdDetail(params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            PageResult<Map<String,String>> pageResult = new PageResult<Map<String,String>>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("getPolicyThirdDetail error ",e);
            return new PageResult<Map<String,String>>(0l,new ArrayList<>());
        }
    }

    @RequestMapping("getFileDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<RadiusFileDetailDTO> getFileDetail( RadiusParamDTO recode,
                                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            PageHelper.startPage(page, pageSize);
            List<RadiusFileDetailDTO> lists = raduisService.getFileDetail(recode);
            PageInfo<RadiusFileDetailDTO> pageInfo = new PageInfo<RadiusFileDetailDTO>(lists);
            PageResult<RadiusFileDetailDTO> pageResult = new PageResult<RadiusFileDetailDTO>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("get 3A file Detail error ",e);
            return new PageResult<RadiusFileDetailDTO>(0l,new ArrayList<>());
        }
    }

    @RequestMapping("getFileSecondDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getFileSecondDetail(HttpServletRequest request,
                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            String dateType = request.getParameter("dateType");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            Map<String,String> params = new HashMap<>();
            params.put("dateType",dateType);
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            return raduisService.getFileSecondDetail(page,pageSize,params);

        } catch (Exception e) {
            logger.error("get 3A getFileSecondDetail error ",e);
        }
        return null;
    }

    @RequestMapping("/getFileThirdDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getFileThirdDetail(HttpServletRequest request,
                                                              @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            String stat_time = request.getParameter("stat_time");
            String fileType = request.getParameter("fileType");
            String dateType = request.getParameter("dateType");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String order = request.getParameter("order");
            String sort = request.getParameter("sort");

            if(dateType!=null&&"1".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"2".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"3".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime()/1000+"";
            }

            Map<String,String> params = new HashMap<>();
            params.put("stat_time",stat_time);
            params.put("fileType",fileType);
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("order",order);
            params.put("sort",sort);
            params.put("timeout", LocalConfig.getInstance().getHashValueByHashKey("monitor.file.upload.timeout"));
            return raduisService.getFileThirdDetail(pageIndex,pageSize,params);

        } catch (Exception e) {
            logger.error("get 3A getFileThirdDetail error ",e);
        }
        return null;
    }


    @RequestMapping("/getWarnFileThirdDetail")
    @RequiresPermission(value = "zf402002_query")
    @ResponseBody
    public PageResult<Map<String,String>> getWarnFileThirdDetail(HttpServletRequest request,
                                                             @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            String stat_time = request.getParameter("stat_time");
            String fileType = request.getParameter("fileType");
            String dateType = request.getParameter("dateType");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String order = request.getParameter("order");
            String sort = request.getParameter("sort");

            if(dateType!=null&&"1".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"2".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd HH").parse(endTime).getTime()/1000+"";
            }else if(dateType!=null&&"3".equals(dateType)){
                startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTime).getTime()/1000+"";
                endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime()/1000+"";
            }

            Map<String,String> params = new HashMap<>();
            params.put("stat_time",stat_time);
            params.put("fileType",fileType);
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("order",order);
            params.put("sort",sort);
            params.put("timeout", LocalConfig.getInstance().getHashValueByHashKey("monitor.file.upload.timeout"));
            return raduisService.getWarnFileThirdDetail(pageIndex,pageSize,params);

        } catch (Exception e) {
            logger.error("get 3A getFileThirdDetail error ",e);
        }
        return null;
    }


}
