package com.aotain.zongfen.controller.analysis;


import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.analysis.AppTrafficResult;
import com.aotain.zongfen.dto.analysis.ShareKWDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.analysis.Params;
import com.aotain.zongfen.service.analysis.ShareKWService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.export.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 1拖n
 * @author chenzr
 *
 */
@RequestMapping("/analysis/share")
@Controller
public class ShareAnalysisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExportService exportService;

    @Autowired
    private ShareKWService service;

    @RequestMapping("/index")
    @RequiresPermission("1拖N用户分析")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("authorized","zf201007");
        modelAndView.setViewName("/analysis/share/index");
        return modelAndView;
    }

    @RequestMapping("/getList")
    @ResponseBody
    @RequiresPermission("zf201007_query")
    public PageResult<ShareKWDTO> getList( ShareKWDTO kwdto){
        try {
            return service.getSharePageResult(kwdto);
        }catch (Exception e){
            logger.error("get share kw data error ",e);
        }
            return null;
        }


    @RequestMapping(value="export", method= RequestMethod.POST)
    @ResponseBody
    @LogAnnotation(module = 201007,type = 7)
    @RequiresPermission("zf201007_export")
    public void handleMainExport( BaseModel<ShareKWDTO> baseModel, ShareKWDTO params, HttpServletResponse response, HttpServletRequest request) {
            try {
                List<ShareKWDTO> lists = service.getShareList(params);
                baseModel.setDatas(lists);
                baseModel.setOs(response.getOutputStream());
                baseModel.setFileName("1拖N用户分析");
                String fileName = exportService.export(baseModel, response, request);
                String dataJson = "fileName=" + fileName;
                ProxyUtil.changeVariable(this.getClass(), "handleMainExport", dataJson, dataJson);
            } catch (UnsupportedEncodingException e) {
                logger.error("export share kw analysis error" , e);
            } catch (IOException e) {
                logger.error("export share kw analysis error" , e);
            } catch (Exception e) {
                logger.info("export share kw analysis , change LogAnnotation failed..." , e);
            }
        }



    @RequestMapping("/idcindex")
    @RequiresPermission("假接入真互联分析")
    public ModelAndView idcindex() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("authorized","zf202004");
        modelAndView.setViewName("/analysis/share/idc_index");
        return modelAndView;
    }

    @RequestMapping("/getIdcList")
    @ResponseBody
    @RequiresPermission("zf202004_query")
    public PageResult<ShareKWDTO> getIdcList( ShareKWDTO kwdto){
        try {
            return service.getSharePageResult(kwdto);
        }catch (Exception e){
            logger.error("get idc share kw data error ",e);
        }
        return null;
    }


    @RequestMapping(value="idcExport", method= RequestMethod.POST)
    @ResponseBody
    @LogAnnotation(module = 202004,type = 7)
    @RequiresPermission("zf202004_export")
    public void handleIdcMainExport( BaseModel<ShareKWDTO> baseModel, ShareKWDTO params, HttpServletResponse response, HttpServletRequest request) {
        try {
            List<ShareKWDTO> lists = service.getShareList(params);
            baseModel.setDatas(lists);
            baseModel.setOs(response.getOutputStream());
            baseModel.setFileName("假接入真互联分析");
            String fileName = exportService.export(baseModel, response, request);
            String dataJson = "fileName=" + fileName;
            ProxyUtil.changeVariable(this.getClass(), "handleIdcMainExport", dataJson, dataJson);
        } catch (UnsupportedEncodingException e) {
            logger.error("export IDC share kw analysis error" , e);
        } catch (IOException e) {
            logger.error("export IDC share kw analysis error" , e);
        } catch (Exception e) {
            logger.info("export DDC share kw analysis , change LogAnnotation failed..." , e);
        }
    }
}
