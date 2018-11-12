package com.aotain.zongfen.controller.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.zongfen.model.common.CommonTree;
import com.aotain.zongfen.service.common.CommonTreeService;

@Controller
@RequestMapping("tree")
public class CommonTreeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonTreeService commonTreeService;
	
	 
    @RequestMapping("getUploadTree")
    @ResponseBody
    public List<CommonTree> getUploadTree(){
        try {

            return commonTreeService.getTree();
        }catch (Exception e){
            logger.error("getUploadTree error",e);
            return null;
        }
    }
}
