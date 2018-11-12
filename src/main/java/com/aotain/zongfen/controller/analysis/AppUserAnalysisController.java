package com.aotain.zongfen.controller.analysis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.analysis.AppUserDto;
import com.aotain.zongfen.dto.common.AppUserParamVo;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.service.analysis.AppUserAnalysisService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.export.BaseModel;

/**
 * 指定应用访问用户分析
 * @author tanzj
 *
 */
@RequestMapping("/analysis/appuser")
@Controller
public class AppUserAnalysisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExportService exportService;
    
    @Autowired
    private CommonService commonService;
    
    @Autowired
    private AppUserAnalysisService appUserAnalysisService;

    @RequestMapping("/index")
    @RequiresPermission({"访问指定应用用户分析"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/analysis/appuser/index");
        return modelAndView;
    }
    
    @RequiresPermission({"zf201004_query"})
    @RequestMapping("/getIndexData")
    @ResponseBody
    public PageResult<AppUserDto> getIndexData(AppUserParamVo params) {
    	PageResult<AppUserDto> result = new PageResult<AppUserDto>();
    	try {
    		result = appUserAnalysisService.getIndexListData(params);
		} catch (Exception e) {
			logger.error("getIndexData fail",e);
		}
    	return result;
    }

    @RequiresPermission({"zf201004_query"})
    @RequestMapping("/getDetailData")
    @ResponseBody
    public PageResult<AppUserDto> getDetailData(AppUserParamVo params) {
    	PageResult<AppUserDto> result = new PageResult<AppUserDto>();
    	try {
    		result = appUserAnalysisService.getDetailListData(params);
		} catch (Exception e) {
			logger.error("getIndexData fail",e);
		}
    	return result;
    }

    @RequiresPermission({"zf201004_export"})
    @RequestMapping(value="export", method= RequestMethod.POST)
    @LogAnnotation
    public void handleMainExport( AppUserParamVo params, HttpServletResponse response, HttpServletRequest request) {
        String type="";
        BaseModel<AppUserDto> baseModel = new BaseModel<AppUserDto>();
        try {
        	List<AppUserDto> lists = appUserAnalysisService.getExportData(params);
        	String fileName = "";
            baseModel.setDatas(lists);
            baseModel.setOs(response.getOutputStream());
            baseModel.setExportType(params.getExportType());
            String[] headers = new String[]{"应用类别","应用名称","访问用户数"};
            String[] fields = new String[]{"apptype","appname","count"};
            baseModel.setFields(fields);
            baseModel.setHeaders(headers);
            baseModel.setFileName("指定应用访问用户分析");
            fileName = exportService.export(baseModel, response, request);
        	ModelType modelType = ModelType.MODEL_USERAPPANALYSIS;
            String dataJson = "fileName=" + fileName;
            ProxyUtil.changeVariable(this.getClass(), "handleMainExport", dataJson, dataJson, modelType.getModel(), OperationType.EXPORT);
        } catch (UnsupportedEncodingException e) {
            logger.error("export appuser analysis , "+ type +" error" , e);
        } catch (IOException e) {
            logger.error("export appuser analysis , "+ type +" error" , e);
        } catch (Exception e) {
            logger.info("export appuser analysis , change LogAnnotation failed..." , e);
        }
    
    }

    }

