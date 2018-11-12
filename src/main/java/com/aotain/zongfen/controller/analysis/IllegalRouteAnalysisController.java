package com.aotain.zongfen.controller.analysis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.analysis.IllegalRouteListDto;
import com.aotain.zongfen.dto.common.IllegalRouteParamVo;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.service.analysis.IllegalRouteAnalysisService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.export.BaseModel;
import com.google.common.collect.Lists;

/**
 * 指定应用访问用户分析
 * @author tanzj
 *
 */
@RequestMapping("/analysis/illegalroute")
@Controller
public class IllegalRouteAnalysisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExportService exportService;
    
    @Autowired
    private CommonService commonService;
    
    @Autowired
    private IllegalRouteAnalysisService illegalRouteAnalysisService;

    @RequestMapping("/index")
//    @RequiresPermission({"非法路由用户分析"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/analysis/illegalroute/index");
        return modelAndView;
    }
    
    @RequiresPermission({"zf201005_query"})
    @RequestMapping("/getIndexData")
    @ResponseBody
    public PageResult<IllegalRouteListDto> getIndexData(IllegalRouteParamVo params) {
    	PageResult<IllegalRouteListDto> result = new PageResult<IllegalRouteListDto>();
    	try {
    		result = illegalRouteAnalysisService.getIndexListData(params);
		} catch (Exception e) {
			logger.error("getIndexData fail",e);
		}
    	return result;
    }

    @RequiresPermission({"zf201005_export"})
    @RequestMapping(value="export", method= RequestMethod.POST)
    @LogAnnotation
    public void handleMainExport( IllegalRouteParamVo params, HttpServletResponse response, HttpServletRequest request) {
        String type="";
        BaseModel<IllegalRouteListDto> baseModel = new BaseModel<IllegalRouteListDto>();
        try {
        	List<IllegalRouteListDto> lists = illegalRouteAnalysisService.getExportData(params);
        	String fileName = "";
            baseModel.setDatas(lists);
            baseModel.setOs(response.getOutputStream());
            baseModel.setExportType(params.getExportType());
            String value = StringUtils.split(params.getImgUrl())[1];
            List<String> imgUrls = Lists.newArrayList();
            imgUrls.add(value);
            baseModel.setDataURLs(imgUrls);
            String[] headers = new String[]{"统计时间","接入点位置","流入流量（KB）","流出流量（KB）"};
            String[] fields = new String[]{"stateTime","nodeip","inputflow","outputflow"};
            baseModel.setFields(fields);
            baseModel.setHeaders(headers);
            baseModel.setFileName("非法路由用户分析");
            fileName = exportService.export(baseModel, response, request);
        	ModelType modelType = ModelType.MODEL_ILLEGALROUTEANALYSIS;
            String dataJson = "fileName=" + fileName;
            ProxyUtil.changeVariable(this.getClass(), "handleMainExport", dataJson, dataJson, modelType.getModel(), OperationType.EXPORT);
        } catch (UnsupportedEncodingException e) {
            logger.error("export illegal route analysis , "+ type +" error" , e);
        } catch (IOException e) {
            logger.error("export illegal route analysis , "+ type +" error" , e);
        } catch (Exception e) {
            logger.info("export illegal route analysis , change LogAnnotation failed..." , e);
        }
    
    }

    @RequiresPermission({"zf201005_query"})
	@RequestMapping(value = "/getChartData")
    @ResponseBody
	public ECharts<Series> getChartData(IllegalRouteParamVo params){
		try {
			return illegalRouteAnalysisService.getChart(params);
		} catch (Exception e) {
			logger.error("getChart fail",e);
		}
		return null;
	}
    
    }

