package com.aotain.zongfen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExampleController {
	
	@RequestMapping("/universalTraffic")
    public ModelAndView universalTraffic() {
		ModelAndView mav = new ModelAndView("/example/universalTraffic");
        return mav;
    }
	
	@RequestMapping("/parameterSettings")
    public ModelAndView parameterSettings() {
		ModelAndView mav = new ModelAndView("/example/parameterSettings");
        return mav;
    }
	
	@RequestMapping("/DataAudit")
    public ModelAndView DataAudit() {
		ModelAndView mav = new ModelAndView("/example/DataAudit");
        return mav;
    }
}
