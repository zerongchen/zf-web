package com.aotain.zongfen.controller.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aotain.common.utils.tools.HttpRequest;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import org.apache.poi.ss.formula.functions.T;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.upload.FlowUploadDTO;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.service.upload.FlowUploadService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.basicdata.UdUploadType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.context.SpringContextUtils;

@Controller
@RequestMapping("/upload")
public class FlowUploadController {

    /**
     * 写日志
     */
    private static Logger logger = LoggerFactory.getLogger(FlowUploadController.class);
    private final static String GENERAL_FLOW="zf101001";
    private final static String POINTAPP_FLOW="zf101002";
    private final static String WEB_FLOW="zf101003";
    private final static String DIRECTION_FLOW="zf101004";
    private final static String ILLEGAL_FLOW="zf101005";
    private final static String ONEBYN_FLOW="zf101006";
    private final static String WEBPUSH_FLOW="zf101007";
    private final static String CPSP_FLOW="zf101008";
    private final static String DDOS_FLOW="zf101009";
    private final static String PROTOCOL_FLOW="zf101010";
    private final static String HTTPGET_FLOW="zf101011";
    private final static String WLAN_FLOW="zf101012";
    private final static String VOIP_FLOW="zf101013";
    private final static String DOWNLOAD_FLOW="zf101014";
    private final static String USERPREFERR_FLOW="zf101015";
    private final static String P2P_FLOW="zf101016";
    private final static String IP_FLOW="zf101017";

    @Autowired
    private FlowUploadService flowUploadService;

    /**
     * 流量流向分析结果上报
     * @return
     */
    @RequestMapping("flowDirection")
    @RequiresPermission({"应用流量流向"})
    public ModelAndView flowDirection(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("packetType", UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", DIRECTION_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }
    
    /**
     * WEB流量统计上报(资源在内+资源在外)
     * @return
     */
    @RequestMapping("webFlow")
    @RequiresPermission({"Web类流量"})
    public ModelAndView webFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型,此处将该子类型设置为 WEB流量统计上报(资源在外) 返回的实则是 内外结合的页面
        modelAndView.addObject("packetType", UdUploadType.WEB_FLOW_UPLOAD_OUTSIDESOURCE.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.WEB_FLOW_UPLOAD_OUTSIDESOURCE.getPacketSubtype());
        modelAndView.addObject("authorized", WEB_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }


    /**
     * VOIP流量统计上报
     * @return
     */
    @RequestMapping("voipFlow")
    @RequiresPermission({"Voip类流量"})
    public ModelAndView voipFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.VOIP_FLOW_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.VOIP_FLOW_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", VOIP_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * 通用流量统计上报
     * @return
     */
    @RequestMapping("generalFlow")
    @RequiresPermission({"通用类流量"})
    public ModelAndView generalFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.GENERAL_FLOW_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.GENERAL_FLOW_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", GENERAL_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * 访问指定应用的用户统计上报策略
     *
     */
    @RequestMapping("pointAppFlow")
    @RequiresPermission({"指定应用用户"})
    public ModelAndView pointAppFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.APP_USER_FLOW_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", POINTAPP_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * Download类分析功能模块上报策略
     *
     */
    @RequestMapping("downloadFlow")
    @RequiresPermission({"Download类"})
    public ModelAndView downloadFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.DOWNLOAD_FLOW_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.DOWNLOAD_FLOW_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", DOWNLOAD_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }


    /**
     * 用户偏好分析上报策略
     * @return
     */
    @RequestMapping("userPreferenceFlow")
    @RequiresPermission({"用户偏好"})
    public ModelAndView userPreferenceFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.USER_PREFERENCE_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.USER_PREFERENCE_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", USERPREFERR_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * 非法路由检测分析上报策略
     * @return
     */
    @RequestMapping("illegalFlow")
    @RequiresPermission({"非法路由检测"})
    public ModelAndView illegalFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.ILLEGAL_ROUTER_CHECK_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.ILLEGAL_ROUTER_CHECK_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", ILLEGAL_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * 1拖N用户行为分析上报策略（检测结果和关键字段）
     * @return
     */
    @RequestMapping("oneByNFlow")
    @RequiresPermission({"1拖N检测"})
    public ModelAndView oneByNFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型 此处将该子类型设置为 一拖N用户行为分析（检测结果） 返回的实则是结合的页面
        modelAndView.addObject("packetType", UdUploadType.ONE_N_USERACTION_CR_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.ONE_N_USERACTION_CR_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", ONEBYN_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * WEB信息推送结果上报策略
     * @return
     */
    @RequestMapping("webPushNFlow")
    @RequiresPermission({"Web推送结果"})
    public ModelAndView webPushNFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", WEBPUSH_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * DDOS异常流量上报策略
     * @return
     */
    @RequestMapping("ddosFlow")
    @RequiresPermission({"DDoS异常流量"})
    public ModelAndView ddosFlow(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", DDOS_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }
    /**
     * CP/SP 资源服务器分析上报模块
     * @return
     */
    @RequestMapping("cpspUpload")
    @RequiresPermission({"CP/SP服务器"})
    public ModelAndView cpspUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.CP_SP_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.CP_SP_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", CPSP_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * P2P应用流量流向分析模块
     * @return
     */
    @RequestMapping("p2pFlowAnalyseUpload")
    @RequiresPermission({"P2P应用流量流向"})
    public ModelAndView p2pFlowAnalyseUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.P2P_FLOW_ANALYSE_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.P2P_FLOW_ANALYSE_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", P2P_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }

    /**
     * IP地址流量TOP N流量分析
     * @return
     */
    @RequestMapping("ipAddressTopnUpload")
    @RequiresPermission({"IP地址流量TOPN流量"})
    public ModelAndView ipAddressTopnUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //PacketType UD类型 , PacketSubtype UD 子类型
        modelAndView.addObject("packetType", UdUploadType.IP_ADDRESS_TOPN_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.IP_ADDRESS_TOPN_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", IP_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }
    
    /**
     * 特殊协议信息上报
     * @return
     */
    @RequestMapping("protocolUpload")
    @RequiresPermission({"特有协议信息"})
    public ModelAndView protocolUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //packetType UD2 packetSubtype 子类型100 ,前端需要根据不同的协议进行区分
        modelAndView.addObject("packetType", UdUploadType.PROTOCOL_INFO_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.PROTOCOL_INFO_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", PROTOCOL_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }
    /**
     * httpget信息上报
     * @return
     */
    @RequestMapping("htppgetUpload")
    @RequiresPermission({"HTTPGET数据"})
    public ModelAndView htppgetUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("packetType", UdUploadType.HTTPGET_BW_KEY_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.HTTPGET_BW_KEY_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", HTTPGET_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }
    /**
     * 移动终端分析数据上报
     * @return
     */
    @RequestMapping("wlanUpload")
    @RequiresPermission({"移动终端"})
    public ModelAndView wlanUpload(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("packetType", UdUploadType.WLAN_INFO_UPLOAD.getPacketType());
        modelAndView.addObject("packetSubtype", UdUploadType.WLAN_INFO_UPLOAD.getPacketSubtype());
        modelAndView.addObject("authorized", WLAN_FLOW);
        setAttributes(modelAndView,request);
        modelAndView.setViewName("/upload/flowupload");
        return modelAndView;
    }


    @RequestMapping("getList")
    @ResponseBody
    public PageResult<FlowUploadDTO> getList( FlowUploadDTO recode,
                                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        try {
            PageHelper.startPage(page, pageSize);
            List<FlowUploadDTO> lists = flowUploadService.getPolicyList(recode);
            PageInfo<FlowUploadDTO> pageInfo = new PageInfo<FlowUploadDTO>(lists);
            PageResult<FlowUploadDTO> pageResult = new PageResult<FlowUploadDTO>(pageInfo.getTotal(),lists);
            return  pageResult;
        }catch (Exception e){
            logger.error("get flow upload list error ",e);
            return new PageResult<FlowUploadDTO>(0l,new ArrayList<FlowUploadDTO>());
        }

    }

    @RequestMapping("insert")
    @ResponseBody
    public ResponseResult<FlowUploadDTO> insertDB( HttpServletRequest request,HttpServletResponse response, @RequestBody FlowUploadDTO recode){
        ResponseResult<FlowUploadDTO> responseResult = new ResponseResult<FlowUploadDTO>();
        try {

          if(isPermited(recode,"add",request,response)){
              return flowUploadService.addDB(recode);
          }else {
              return null;
          }
        }catch (Exception e){
            logger.error(" add flow upload strategy  error " ,e);
            responseResult.setResult(0);
            responseResult.setMessage("请刷新重试");
            return responseResult;
        }

    }
    @RequestMapping("update")
    @ResponseBody
    public ResponseResult<FlowUploadDTO> updateDB( HttpServletRequest request,HttpServletResponse response, @RequestBody FlowUploadDTO recode){
        try {
            if(isPermited(recode,"modify",request,response)){
                return flowUploadService.modifyDb(recode);
            }
            return null;
        }catch (Exception e){
            logger.error("update flow upload error " ,e);
            return null;
        }

    }

    @RequestMapping("delete")
    @ResponseBody
    public ResponseResult<FlowUploadDTO> delete( HttpServletRequest request,HttpServletResponse response){
        try {

            if(isPermited(null,"delete",request,response)){
                String[] messageNos = request.getParameterValues("messageNos[]");
                return flowUploadService.delete(messageNos);
            }
            return null;
        }catch (Exception e){
            logger.error("delete flow upload error ",e);
            return null;
        }
    }

    @RequestMapping("resend")
    @ResponseBody
    public ResponseResult<FlowUploadDTO> reSendPolicy( HttpServletRequest request,HttpServletResponse response, @RequestBody FlowUploadDTO flowUploadDTO) {
    	ResponseResult<FlowUploadDTO> responseResult = new ResponseResult<FlowUploadDTO>();
        try {
            if(isPermited(flowUploadDTO,"redo",request,response)){
                return  flowUploadService.reSendPolicy(flowUploadDTO);
            }
            return null;
        }catch (Exception e){
            logger.error("resend upload policy fail :",e);
            responseResult.setResult(0);
            responseResult.setMessage("重发失败,请刷新重试");
            return responseResult;
        }
    }

    /**
     * 针对前段的操作栏的权限
     * @param modelAndView
     */
    private void setAttributes(ModelAndView modelAndView,HttpServletRequest request){

        String permissionStr = (String)modelAndView.getModel().get("authorized");
        HttpSession httpSession = request.getSession();
        if(httpSession.getAttribute(permissionStr+"_redo")!=null && String.valueOf(httpSession.getAttribute(permissionStr+"_redo")).equalsIgnoreCase("1")){
            modelAndView.addObject("redoAu",1);
        }
        if(httpSession.getAttribute(permissionStr+"_modify")!=null && String.valueOf(httpSession.getAttribute(permissionStr+"_modify")).equalsIgnoreCase("1")){
            modelAndView.addObject("modifyAu",1);
        }
        if(httpSession.getAttribute(permissionStr+"_delete")!=null && String.valueOf(httpSession.getAttribute(permissionStr+"_delete")).equalsIgnoreCase("1")){
            modelAndView.addObject("deleteAu",1);
        }
    }

    private boolean isPermited( FlowUploadDTO recode,String operation, HttpServletRequest request, HttpServletResponse response){

        int packetType ,packetSubtype=0;
        if(recode!=null){
            packetType =recode.getPacketType();
            packetSubtype =recode.getPacketSubtype();
        }else {
            packetType = Integer.parseInt(request.getParameter("packType"));
            packetSubtype = Integer.parseInt(request.getParameter("packSubType"));
        }
        HttpSession httpSession = request.getSession();
        String permission = "";
        if(packetType==UdUploadType.GENERAL_FLOW_UPLOAD.getPacketType() && packetSubtype==UdUploadType.GENERAL_FLOW_UPLOAD.getPacketSubtype()){
            permission=GENERAL_FLOW;
        }else if(packetType==UdUploadType.APP_USER_FLOW_UPLOAD.getPacketType() && packetSubtype==UdUploadType.APP_USER_FLOW_UPLOAD.getPacketSubtype()){
            permission=POINTAPP_FLOW;
        }else if(packetType==UdUploadType.WEB_FLOW_UPLOAD_OUTSIDESOURCE.getPacketType() && (packetSubtype==UdUploadType.WEB_FLOW_UPLOAD_OUTSIDESOURCE.getPacketSubtype()
                                                                                        || packetSubtype==UdUploadType.WEB_FLOW_UPLOAD_INSIDESOURCE.getPacketSubtype())){
            permission=WEB_FLOW;
        }else if(packetType==UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketType() && packetSubtype==UdUploadType.APP_FLOW_DIRECTION_UPLOAD.getPacketSubtype()){
            permission=DIRECTION_FLOW;
        }else if(packetType==UdUploadType.ILLEGAL_ROUTER_CHECK_UPLOAD.getPacketType() && packetSubtype==UdUploadType.ILLEGAL_ROUTER_CHECK_UPLOAD.getPacketSubtype()){
            permission=ILLEGAL_FLOW;
        }else if(packetType==UdUploadType.ONE_N_USERACTION_CR_UPLOAD.getPacketType() && (packetSubtype==UdUploadType.ONE_N_USERACTION_CR_UPLOAD.getPacketSubtype()
                                                                                        || packetSubtype==UdUploadType.ONE_N_USERACTION_KW_UPLOAD.getPacketSubtype())){
            permission=ONEBYN_FLOW;
        }else if(packetType==UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketType() && packetSubtype==UdUploadType.WEB_PUSHRESULT_UPLOAD.getPacketSubtype()){
            permission=WEBPUSH_FLOW;
        }else if(packetType==UdUploadType.CP_SP_UPLOAD.getPacketType() && packetSubtype==UdUploadType.CP_SP_UPLOAD.getPacketSubtype()){
            permission=CPSP_FLOW;
        }else if(packetType==UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketType() && packetSubtype==UdUploadType.DDOS_ANALYSE_UPLOAD.getPacketSubtype()){
            permission=DDOS_FLOW;
        }else if(packetType==UdUploadType.PROTOCOL_INFO_UPLOAD.getPacketType()){
            permission=PROTOCOL_FLOW;
        }else if(packetType==UdUploadType.HTTPGET_BW_KEY_UPLOAD.getPacketType() && packetSubtype==UdUploadType.HTTPGET_BW_KEY_UPLOAD.getPacketSubtype()){
            permission=HTTPGET_FLOW;
        }else if(packetType==UdUploadType.WLAN_INFO_UPLOAD.getPacketType() && packetSubtype==UdUploadType.WLAN_INFO_UPLOAD.getPacketSubtype()){
            permission=WLAN_FLOW;
        }else if(packetType==UdUploadType.VOIP_FLOW_UPLOAD.getPacketType() && packetSubtype==UdUploadType.VOIP_FLOW_UPLOAD.getPacketSubtype()){
            permission=VOIP_FLOW;
        }else if(packetType==UdUploadType.DOWNLOAD_FLOW_UPLOAD.getPacketType() && packetSubtype==UdUploadType.DOWNLOAD_FLOW_UPLOAD.getPacketSubtype()){
            permission=DOWNLOAD_FLOW;
        }else if(packetType==UdUploadType.USER_PREFERENCE_UPLOAD.getPacketType() && packetSubtype==UdUploadType.USER_PREFERENCE_UPLOAD.getPacketSubtype()){
            permission=USERPREFERR_FLOW;
        }else if(packetType==UdUploadType.P2P_FLOW_ANALYSE_UPLOAD.getPacketType() && packetSubtype==UdUploadType.P2P_FLOW_ANALYSE_UPLOAD.getPacketSubtype()){
            permission=P2P_FLOW;
        }else if(packetType==UdUploadType.IP_ADDRESS_TOPN_UPLOAD.getPacketType() && packetSubtype==UdUploadType.IP_ADDRESS_TOPN_UPLOAD.getPacketSubtype()){
            permission=IP_FLOW;
        }else {
           return false;
        }
        permission=permission+"_"+operation;
        try {
            if(httpSession.getAttribute(permission)!=null && String.valueOf(httpSession.getAttribute(permission)).equalsIgnoreCase("1")){
                return true;
            }else {
              response.sendRedirect(request.getContextPath() + "/nopermission");
              return false;
            }
        } catch (Exception e) {
            logger.error("flow upload authorize analysis error",e);
        }
        return false;
    }
}
