package com.aotain.zongfen.controller.general;

import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.general.BrasDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.general.Bras;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.bras.BrasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bras")
public class BrasController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BrasService brasService;

    @Autowired
    private CommonService commonService;

    @RequestMapping("/index")
    public String index(){
        return "/bras/index";
    }


    @RequestMapping("/getBras")
    @ResponseBody
    @RequiresPermission(value = "zf104006_query")
    public List<BrasDTO> getBras( Bras bras){
        return brasService.getBrase(bras);
    }

    @RequestMapping("/export.do")
    @ResponseBody
//    @LogAnnotation()
    @RequiresPermission(value = "zf104006_export")
    public void export( HttpServletRequest request, HttpServletResponse response , Bras bras){

        List<BrasDTO> list = brasService.getBrase(bras);
        List<List<?>> dataList = new ArrayList<>();
        List<Class<?>> classList = new ArrayList<Class<?>>();
        dataList.add(list);
        classList.add(BrasDTO.class);
        String fileName = commonService.exportData(dataList,classList,"bras",response,request);
        try {
            String dataJSON="fileName="+fileName;
            ProxyUtil.changeVariable(this.getClass(),"export",dataJSON,"",ModelType.MODEL_BRASE.getModel(), OperationType.EXPORT);
        } catch (Exception e) {
            logger.error("logging model "+ ModelType.MODEL_BRASE.getDescription()+"... change LogAnnotation param error "+e);
        }

    }
}
