package com.aotain.zongfen.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.aotain.zongfen.model.device.IdcHouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.service.common.MultiSelectService;

@Controller
@RequestMapping("select")
public class SelectController {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(SelectController.class);

    @Autowired
    private MultiSelectService multiSelectService;
    
    @RequestMapping("getAreaGroupPolicy")
    @ResponseBody
    public List<Multiselect> getAreaGroupPolicy(){
        return multiSelectService.getAreaGroupPolicy();
    }

    @RequestMapping("getMessageTypeName")
    @ResponseBody
    public List<Multiselect> getMessageTypeName(){
        return multiSelectService.getMessageTypeName();
    }


    @RequestMapping("getDdosManagePolicy")
    @ResponseBody
    public List<Multiselect> getDdosManagePolicy(){
        return multiSelectService.getDdosManagePolicy();
    }

    @RequestMapping("getUserGroup")
    @ResponseBody
    public List<Multiselect> getUserGroup(){
        return multiSelectService.getUserGroup();
    }

    @RequestMapping(value="getAppType", method= RequestMethod.GET)
    @ResponseBody
    public List<Multiselect> getAppType(){
        return multiSelectService.getAppType();
    }

    /**
     * 获取子类应用
     * @param appType 子类应用 10进制
     * @return
     */
    @RequestMapping(value="getAppByType", method= RequestMethod.GET)
    @ResponseBody
    public List<Multiselect> getAppByType(@RequestParam(required = true) Integer appType){
    	if(appType==null) {
    		return new ArrayList<Multiselect>();
    	}else {
    		return multiSelectService.getAppByType(appType);
    	}
    }


    @RequestMapping(value="getClassFileInfo", method= RequestMethod.GET)
    @ResponseBody
    public List<Multiselect> getClassFileInfo(@RequestParam(required = true ,defaultValue = "0" ) Integer classType){
        return multiSelectService.getClassFileInfo(classType);
    }

    @RequestMapping(value="getZongfenDev", method= RequestMethod.GET)
    @ResponseBody
    public List<Multiselect> getZongfenDev( HttpServletRequest request){
        List<Multiselect> list= new ArrayList<Multiselect>();
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("packetType",Integer.parseInt(request.getParameter("packetType")));
            map.put("packetSubtype",Integer.parseInt(request.getParameter("packetSubtype")));
            list = multiSelectService.getZongfenDev(map);
        }catch (Exception e){
            logger.debug("cant get upload zongfen device "+e);
        }

        return list ;
    }

    @RequestMapping(value="getWebType", method= RequestMethod.GET)
    @ResponseBody
    public List<Multiselect> getWebType( HttpServletRequest request){
        List<Multiselect> list= new ArrayList<Multiselect>();
        try {
            list = multiSelectService.getWebType();
        }catch (Exception e){
            logger.debug("cant get web type ",e);
        }
        return list ;
    }
    
    @RequestMapping("getWebPushManagePolicy")
    @ResponseBody
    public List<Multiselect> getWebPushManagePolicy(){
        return multiSelectService.getWebPushManagePolicy();
    }

    @RequestMapping("getAppUserPolicy")
    @ResponseBody
    public List<Multiselect> getAppUserPolicy(){
        return multiSelectService.getAppUserPolicy();
    }
    
    @RequestMapping("getAreaList")
    @ResponseBody
    public List<Multiselect> getAreaList(){
        return multiSelectService.getAreaList();
    }

    @RequestMapping("getIdcHouseList")
    @ResponseBody
    public List<Multiselect> getIdcHouseList(){
        return multiSelectService.getIdcHouseList();
    }

    @RequestMapping("getCarrieroperators")
    @ResponseBody
    public List<Multiselect> getCarrieroperators(){
        return multiSelectService.getCarrieroperators();
    }
}
