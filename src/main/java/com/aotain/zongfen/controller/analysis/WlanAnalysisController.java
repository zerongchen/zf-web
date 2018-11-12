package com.aotain.zongfen.controller.analysis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.aotain.zongfen.dto.analysis.WlanDto;
import com.aotain.zongfen.dto.common.WlanParamVo;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.service.analysis.WlanAnalysisService;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.export.BaseModel;

/**
 * 指定应用访问用户分析
 * @author tanzj
 *
 */
@RequestMapping("/analysis/wlan")
@Controller
public class WlanAnalysisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExportService exportService;
    
    @Autowired
    private CommonService commonService;
    
    @Autowired
    private WlanAnalysisService wlanAnalysisService;
    
    @RequestMapping("/index")
    @RequiresPermission({"WLAN终端分析"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/analysis/wlan/index");
        return modelAndView;
    }
    
    @RequiresPermission({"zf201006_query"})
    @RequestMapping("/getIndexData")
    @ResponseBody
    public PageResult<WlanDto> getIndexData(WlanParamVo params) {
    	PageResult<WlanDto> result = new PageResult<WlanDto>();
    	try {
    		result = wlanAnalysisService.getIndexListData(params);
		} catch (Exception e) {
			logger.error("getIndexData fail",e);
		}
    	return result;
    }

    @RequiresPermission({"zf201004_query"})
    @RequestMapping("/getDetailData")
    @ResponseBody
    public List<WlanDto> getDetailData(WlanParamVo params) {
    	List<WlanDto> result = new ArrayList<WlanDto>();
    	try {
    		result = wlanAnalysisService.getDetailListData(params);
		} catch (Exception e) {
			logger.error("getIndexData fail",e);
		}
    	return result;
    }

    @RequiresPermission({"zf201006_export"})
    @RequestMapping(value="export", method= RequestMethod.POST)
    @LogAnnotation
    public void handleMainExport( WlanParamVo params, HttpServletResponse response, HttpServletRequest request) {
        String type="";
        BaseModel<WlanDto> baseModel = new BaseModel<WlanDto>();
        try {
        	List<WlanDto> lists = wlanAnalysisService.getExportData(params);
        	String fileName = "";
            baseModel.setDatas(lists);
            baseModel.setOs(response.getOutputStream());
            baseModel.setExportType(params.getExportType());
            String[] headers = new String[]{"用户账号","终端数"};
            String[] fields = new String[]{"useraccount","devicecnt"};
            baseModel.setFields(fields);
            baseModel.setHeaders(headers);
            baseModel.setFileName("WLAN终端分析");
            fileName = exportService.export(baseModel, response, request);
        	ModelType modelType = ModelType.MODEL_WLANANALYSIS;
            String dataJson = "fileName=" + fileName;
            ProxyUtil.changeVariable(this.getClass(), "handleMainExport", dataJson, dataJson, modelType.getModel(), OperationType.EXPORT);
        } catch (UnsupportedEncodingException e) {
            logger.error("export wlan analysis , "+ type +" error" , e);
        } catch (IOException e) {
            logger.error("export wlan analysis , "+ type +" error" , e);
        } catch (Exception e) {
            logger.info("export user wlan analysis , change LogAnnotation failed..." , e);
        }
    
    }

    }

