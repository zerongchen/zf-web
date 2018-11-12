package com.aotain.zongfen.controller.general;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.dto.general.TriggerDTO;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.Trigger;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.MultiSelectService;
import com.aotain.zongfen.service.general.informationtrigger.InformationTriggerService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/trigger")
public class InformationTriggerController {

    @Autowired
    private InformationTriggerService informationTriggerService;

    @Autowired
    private MultiSelectService multiSelectService;

    @RequestMapping("index")
    public String index(){
        return "/informationpush/trigger";
    }

    @RequestMapping(value = "getTrigger",method = {RequestMethod.POST})
    @ResponseBody
    public List<TriggerDTO> getTrigger( Trigger trigger){

        return informationTriggerService.getTrigger(trigger);
    }

    @RequestMapping(value = "delete",method = {RequestMethod.POST})
    @ResponseBody
    public void deleteById( HttpServletRequest request){

        List<Trigger> list = new ArrayList<>();
        Gson gson = new Gson();
        String[] triggers  = request.getParameterValues("trigger[]");
        for(int i = 0; i<triggers.length;i++){
            Trigger trigger= gson.fromJson(triggers[i],Trigger.class);
            list.add(trigger);
        }
        informationTriggerService.delete(list);
    }

    @RequestMapping(value = "insertOrUpdate",method = {RequestMethod.POST})
    @ResponseBody
    public ResponseResult insertOrUpdate( HttpServletRequest request){

        return informationTriggerService.insertOrUpdate(request);

    }

    @RequestMapping(value = "getWSOption",method = {RequestMethod.GET})
    @ResponseBody
    public List<Multiselect> getWSOption(){
        return multiSelectService.getWSOption();
    }

    @RequestMapping(value = "getKWOption",method = {RequestMethod.GET})
    @ResponseBody
    public List<Multiselect> getKWOption(){
        return multiSelectService.getKWOption();
    }

    @RequestMapping(value = "getWLOption",method = {RequestMethod.GET})
    @ResponseBody
    public List<Multiselect> getWLOption(){
        return multiSelectService.getWLOption();
    }


}
