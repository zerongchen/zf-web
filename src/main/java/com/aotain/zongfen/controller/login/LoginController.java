package com.aotain.zongfen.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.aotain.login.pojo.LoginUser;
import com.aotain.login.pojo.UserDetailInfo;
import com.aotain.login.support.Authority;
import com.aotain.zongfen.utils.IPUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	/**
	 * 当前系统在权限系统的部署id
	 */
	@Value("${service.local.deployid}")
	private String deployid;

	@Value("${server.port}")
	private String deployPort;
	/**
	 * 登录成功跳转路径
	 */
	private String callBackPath;

/*	@Value("${service.sso.loginurl}")
	private String serviceSsoLoginurl;*/

	@RequestMapping({"/login"})
	public ModelAndView toLogin(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/login");
		return mav;
	}
	@RequestMapping(value="/dealLogin", method=RequestMethod.GET)
	public String dealLoginGet() {
		return "redirect:/login";
	}

	@RequestMapping(value="/dealLogin", method=RequestMethod.POST)
	public String dealLogin(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser,Model model){
		//用户名及密码验证
		System.out.println("do login sessionId : "+request.getSession().getId());
		try {
			String md5Hex = DigestUtils
					.md5Hex(loginUser.getPassword()).toUpperCase();
			loginUser.setPassword(md5Hex);
			loginUser.setDeployid(deployid);
			//加上验证码,验证码的规则如下
			/**
			 * 每次登录都需要校验验证码
			 * 连续三次输错用户名或者密码，就去校验验证码
			 * 如果登陆连续失败10次，就记录当前时间，一分钟内不让再登陆
			 * 登陆成功，所有的失败信息都清空
			 */
//            loginUser.setVerificationCode("xxxx");
			//调用登陆接口
			JSONObject result = Authority.login(request,response, loginUser);
			//如果返回的结果中包含有message的信息，就表明登陆失败，失败信息就是mesage中的内容
			if (StringUtils.isNotBlank(result.getString("message"))) {
				if ("验证码错误".equals(result.getString("message"))){
					model.addAttribute("errorCode",1);
				} else {
					model.addAttribute("errorCode",2);
				}
				model.addAttribute("message",result.getString("message"));
				return "/login";
			}
			logger.info("loginController,request.getRemoteHost()="+request.getRemoteHost());
			logger.info("loginController,request.getRequestURL()="+request.getRequestURL());
			logger.info("loginController,request.getHeader(\"Referer\")="+request.getHeader("Referer"));

			//String userLocalIp = IPUtil.getIpAddress(request);
			//登陆成功之后需要调用这个方法，跳转到这个页面，第三个参数是本项目的登陆成功页面的uri
			callBackPath = "http://"+ IPUtil.getIpByLine(request.getRequestURL().toString())+":"+deployPort+"/home";
			logger.info("loginController,callBackPath="+callBackPath);

		//	callBackPath = "http://"+ ExceptionCollector.getLocalHost()+":"+deployPort+"/home";
			return Authority.successPage(request,response,callBackPath);
		} catch (Exception e) {
			logger.error("login error!", e);
			return "/login";
		}


	}

	/**
	 * 获取验证码图片
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/code", consumes= MediaType.ALL_VALUE, produces = MediaType.IMAGE_JPEG_VALUE)
	ResponseEntity<byte[]> getValidateCode(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("code sessionId : "+request.getSession().getId());
		byte[] image= Authority.getValidateCode(request);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		return ResponseEntity.ok(image);
	}


	@RequestMapping(value ="/logout")
	public String logout(HttpServletRequest re) {
		Authority.logout(re);
		return "redirect:/login";
	}


	@RequestMapping({"","/","/home"})
    public ModelAndView home(){
		ModelAndView mav = new ModelAndView("/home");
        return mav;
    }

	@RequestMapping(value ="/index")
    public ModelAndView index() {
		ModelAndView mav = new ModelAndView("/index");
        return mav;
    }

	@RequestMapping(value ="/nopermission")
	public ModelAndView nopermission() {
		ModelAndView mav = new ModelAndView("/nopermission");
		return mav;
	}

	@RequestMapping(value = "/getLoginUser")
    @ResponseBody
    public UserDetailInfo getLoginUser(HttpServletRequest request) {
        return Authority.getUserDetailInfo(request);
    }
}
