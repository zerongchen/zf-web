package com.aotain.zongfen.controller.analysis;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.analysis.AppTrafficDetailResult;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.analysis.Params;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.sankey.SanKey;
import com.aotain.zongfen.service.analysis.AppTrafficAnalysisService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.TrafficDataConstant;
import com.aotain.zongfen.utils.basicdata.UdUploadType;
import com.aotain.zongfen.utils.export.BaseModel;
import com.aotain.zongfen.utils.export.DamsLogUtils;
import com.aotain.zongfen.utils.export.ExportType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 应用流量流向
 * @author chenzr
 *
 */
@RequestMapping("/analysis/apptraffic")
@Controller
public class AppTrafficAnalysisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExportService exportService;
    @Autowired
    private AppTrafficAnalysisService appTrafficAnalysisService;

    @RequestMapping("/index")
    @RequiresPermission({"应用流量流向分析"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageType", 1);
        modelAndView.addObject("authorized", "zf201002");
        modelAndView.setViewName("/analysis/apptraffic/index");
        return modelAndView;
    }

    @RequestMapping("/indexIdc")
    @RequiresPermission({"IDC应用流量流向分析"})
    public ModelAndView indexIdc() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageType", 2);
        modelAndView.addObject("authorized", "zf202002");
        modelAndView.setViewName("/analysis/apptraffic/index");
        return modelAndView;
    }

    @RequestMapping("getAreaSankey")
    @ResponseBody
    public SanKey getAreaSankey(HttpServletRequest request,HttpServletResponse response,@RequestBody Params params){

        try {
            if(isPermited(request,response,params,"query")){
                return appTrafficAnalysisService.getAreaSankey(params);
            }
            return null;
        }catch (Exception e){
            logger.error("get app traffic echarts data error ",e);
            return null;
        }

    }


    @RequestMapping("getMainList")
    @ResponseBody
    public PageResult<AppTrafficResult> getMainList(HttpServletRequest request,HttpServletResponse response,Params params,
                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        if(isPermited(request,response,params,"query")) {
            try {
                PageHelper.startPage(page, pageSize);
                List<AppTrafficResult> lists = appTrafficAnalysisService.getMainList(params);
                PageInfo<AppTrafficResult> pageInfo = new PageInfo<AppTrafficResult>(lists);
                PageResult pageResult = new PageResult(pageInfo.getTotal(), lists);
                return pageResult;
            } catch (Exception e) {
                logger.error("get app traffic main list error" , e);
            }
        }
        return new PageResult<>();
    }



    @RequestMapping("getEchartsRankingData")
    @ResponseBody
    public ECharts getEchartsRankingData(HttpServletRequest request,HttpServletResponse response, @RequestBody Params params){
        try {
            if(isPermited(request,response,params,"query")) {
                return appTrafficAnalysisService.getEchartRankingData(params);
            }
            return null;
        }catch (Exception e){
            logger.error("get app traffic Echarts Ranking Data error ",e);
            return null;
        }

    }

    @RequestMapping("getTableRankingData")
    @ResponseBody
    public PageResult<AppTrafficDetailResult> getTableRankingData(HttpServletRequest request,HttpServletResponse response,Params params,
                                                    @RequestParam(required = false, defaultValue = "1") Integer page,
                                                    @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        if(isPermited(request,response,params,"query")) {
            try {
                PageHelper.startPage(page, pageSize);
                List<AppTrafficDetailResult> lists = appTrafficAnalysisService.getTableRankingData(params);
                PageInfo<AppTrafficDetailResult> pageInfo = new PageInfo<AppTrafficDetailResult>(lists);
                PageResult pageResult = new PageResult(pageInfo.getTotal(), lists);
                return pageResult;
            } catch (Exception e) {
                logger.error("app traffic get show table ranking list error" , e);
            }
        }
        return new PageResult<>();
    }

    @RequestMapping("getEchartsTrendData")
    @ResponseBody
    public ECharts getEchartsTrendData(HttpServletRequest request,HttpServletResponse response, @RequestBody Params params){

       try {
           if(isPermited(request,response,params,"query")) {
               return appTrafficAnalysisService.getEchartTrendData(params);
           }
           return null;
       }catch (Exception e){
           logger.error(" get app traffic Echarts Trend Data error ",e);
           return null;
       }

    }

    @RequestMapping("getTableTrendData")
    @ResponseBody
    public PageResult<AppTrafficDetailResult> getTableTrendData(HttpServletRequest request,HttpServletResponse response,Params params,
                                                                  @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        if(isPermited(request,response,params,"query")) {
            try {
                PageHelper.startPage(page, pageSize);
                List<AppTrafficDetailResult> lists = appTrafficAnalysisService.getTableTrendData(params);
                PageInfo<AppTrafficDetailResult> pageInfo = new PageInfo<AppTrafficDetailResult>(lists);
                PageResult pageResult = new PageResult(pageInfo.getTotal(), lists);
                return pageResult;
            } catch (Exception e) {
                logger.error("app traffic get show table ranking list error" , e);
            }
        }
        return new PageResult<>();
    }

    @RequestMapping(value="export", method= RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public void handleMainExport( BaseModel<AppTrafficResult> baseModel, Params params, HttpServletResponse response, HttpServletRequest request) {
        String type="";
        if(isPermited(request,response,params,"export")) {
            try {
                List<AppTrafficResult> lists = appTrafficAnalysisService.getMainList(params);
                baseModel.setDatas(lists);
                baseModel.setOs(response.getOutputStream());
                ModelType modelType = null;
                if (params.getPageType() == 1) {
                    baseModel.setFileName("城域网应用流量流向分析");
                    modelType = ModelType.MODEL_USERTRAFFICANALYSIS;
                    type="area export";
                } else if (params.getPageType() == 2) {
                    modelType = ModelType.MODEL_USERTRAFFICANALYSIS_IDC;
                    baseModel.setFileName("IDC应用流量流向分析");
                    type="IDC export";
                }
                String fileName = exportService.export(baseModel, response, request);
                String dataJson = "fileName=" + fileName;
                ProxyUtil.changeVariable(this.getClass(), "handleMainExport", dataJson, dataJson, modelType.getModel(), OperationType.EXPORT);
            } catch (UnsupportedEncodingException e) {
                logger.error("export user traffic analysis , "+ type +" error" , e);
            } catch (IOException e) {
                logger.error("export user traffic analysis , "+ type +" error" , e);
            } catch (Exception e) {
                logger.info("export user traffic analysis , change LogAnnotation failed..." , e);
            }
        }
    }

    @RequestMapping(value="exportChild", method= RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public void handleMainExportChild( BaseModel<AppTrafficDetailResult> baseModel, Params params, HttpServletResponse response,HttpServletRequest request){
        String type="";
        if(isPermited(request,response,params,"export")) {
            try {
                List<AppTrafficDetailResult> lists = null;
                if (params.getType() == 1) {//趋势
                    lists = appTrafficAnalysisService.getTableTrendData(params);
                    baseModel.setFileName("趋势分析");
                    type="trending export";
                } else {
                    lists = appTrafficAnalysisService.getTableRankingData(params);
                    baseModel.setFileName("排名分析");
                    type="ranking export";
                }
                ModelType modelType = null;
                if (params.getAreaType() == 1 || params.getAreaType() == 4) {
                    modelType = ModelType.MODEL_USERTRAFFICANALYSIS;
                } else {
                    modelType = ModelType.MODEL_USERTRAFFICANALYSIS_IDC;
                }
                baseModel.setDatas(lists);
                baseModel.setOs(response.getOutputStream());
                String fileName = exportService.export(baseModel, response, request);
                String dataJson = "fileName=" + fileName;
                ProxyUtil.changeVariable(this.getClass(), "handleMainExportChild", dataJson, dataJson, modelType.getModel(), OperationType.EXPORT);

            } catch (UnsupportedEncodingException e) {
                logger.error(type+ " error " , e);
            } catch (IOException e) {
                logger.error(type+ " error " , e);
            } catch (Exception e) {
                logger.error(type+ " error , -- change LogAnnotation failed..." , e);
            }
        }
    }

    private boolean isPermited(HttpServletRequest request,HttpServletResponse response ,Params params,String operate){
        HttpSession httpSession = request.getSession();
        String permission="";
        if(params.getPageType()!=null){
            if(params.getPageType()==1){
                permission="zf201002";
            }else if(params.getPageType()==2){
                permission="zf202002";
            }else {
                return false;
            }
        }else if(params.getAreaType()!=null){
            if(params.getAreaType()==1 || params.getAreaType()==4){
                permission="zf201002";
            }else if(params.getAreaType()==30 || params.getAreaType()==31){
                permission="zf202002";
            }else {
                return false;
            }
        }
        permission=permission+"_"+operate;
        try {
            if(httpSession.getAttribute(permission)!=null && String.valueOf(httpSession.getAttribute(permission)).equalsIgnoreCase("1")){
                return true;
            }else {
                response.sendRedirect(request.getContextPath() + "/nopermission");
                return false;
            }
        } catch (Exception e) {
            logger.error("app traffic authorize analysis error",e);
        }
        return false;
    }
}

