package com.aotain.zongfen.controller.useranalysis;

import com.aotain.common.config.LocalConfig;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.login.support.Authority;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;
import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.echarts.ECharts;
import com.aotain.zongfen.model.echarts.Series;
import com.aotain.zongfen.model.useranalysis.DDoSAttackArea;
import com.aotain.zongfen.model.useranalysis.DDoSQueryParam;
import com.aotain.zongfen.model.useranalysis.DDoSUbasDetail;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.common.ExportService;
import com.aotain.zongfen.service.export.HiveExportService;
import com.aotain.zongfen.service.useranalysis.DDoSAttackServiceImpl;
import com.aotain.zongfen.utils.IPUtil;
import com.aotain.zongfen.utils.export.BaseModel;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ddos异常流量分析Controller类
 *
 * @author daiyh@aotain.com
 * @date 2018/06/20
 */
@RestController
@RequestMapping("/userbehavioranalysis/ddosirregular")
public class DdosIrregularAnalysisController {


    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(DdosIrregularAnalysisController.class);

    @Autowired
    private DDoSAttackServiceImpl dDoSAttackService;
    @Autowired
    @Qualifier("hiveExportServiceImpl")
    private HiveExportService hiveExportServiceImpl;


/*    @Autowired
    private ExportService exportService;*/

    @Autowired
    private CommonService commonService;

    @RequestMapping(value="/index", method= RequestMethod.GET)
    @RequiresPermission({"DDoS异常流量分析"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("/userbehavioranalysis/ddosirregular/index");
        return mv;
    }

    @RequestMapping(value = "/listData",method = RequestMethod.POST)
    @RequiresPermission({"zf203001_query"})
    public PageResult listData(@RequestBody DDoSQueryParam dDoSQueryParam){
        PageResult pageResult;
        try{
            Page<DDoSUbasDetail> result = dDoSAttackService.listData(dDoSQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping(value = "/listAreaData",method = RequestMethod.POST)
    @RequiresPermission({"zf203001_query"})
    public PageResult listAreaData(@RequestBody DDoSQueryParam dDoSQueryParam){
        PageResult pageResult;
        try{
            Page<DDoSAttackArea> result = dDoSAttackService.listAreaData(dDoSQueryParam);
            pageResult = new PageResult(result.getTotal(),result,0,"success");
        } catch (Exception e){
            pageResult = PageResult.getErrorPageResult("request error");
            logger.error("there is some error ",e);
        }
        return pageResult;
    }

    @RequestMapping(value = "/getChartData",method = RequestMethod.POST)
    @RequiresPermission({"zf203001_query"})
    public ECharts<Series> getChartData(@RequestBody DDoSQueryParam dDoSQueryParam){

        Map<String,Map<String,Double>> dDoSUbas = dDoSAttackService.getChartData(dDoSQueryParam);
        ECharts<Series> eCharts = dDoSAttackService.wrapChartData(dDoSQueryParam,dDoSUbas);
        return eCharts;
    }
    @RequestMapping("/createExportTask")
    @RequiresPermission({"zf203001_export"})
    public Map<String,Object> createExportTask(HttpServletRequest httpServletRequest){
        Map<String,Object> returnMap = new HashMap<>();
        try{
            int user_id = Authority.getUserDetailInfo(httpServletRequest) != null ? Authority.getUserDetailInfo(httpServletRequest).getUserId() : 0;
            String user_ip= IPUtil.getIpAddress(httpServletRequest);

            String startTime = httpServletRequest.getParameter("startTime");
            String endTime = httpServletRequest.getParameter("endTime");
            String userGroupNo = httpServletRequest.getParameter("userGroupNo");
            String appAttackType = httpServletRequest.getParameter("appAttackType");
            String file_name = httpServletRequest.getParameter("file_name");
            String export_status = httpServletRequest.getParameter("export_status");

            StringBuilder sb = new StringBuilder();
            sb.append("select t1.attack_starttime,t1.attack_endtime,t2.USER_GROUP_NAME,t1.appattacktype,t1.appattacktraffic,t1.appattackrate,t1.attackareanum from(");

            sb.append(" select from_unixtime(attack_starttime,'yyyy-MM-dd HH:mm:ss') as attack_starttime ");
            sb.append(" , from_unixtime(attack_endtime,'yyyy-MM-dd HH:mm:ss') as attack_endtime ");
            sb.append(" ,pusergrougno ");
            sb.append(" ,case when appattacktype==1 then 'ICMP Redirection' ");
            sb.append("       when appattacktype==2 then 'ICMP Unreacheble' ");
            sb.append("       when appattacktype==3 then 'TCP anomaly Connection' ");
            sb.append("       when appattacktype==4 then 'DNS Query Application Attack' ");
            sb.append("       when appattacktype==5 then 'DNS Reply Application Attack' ");
            sb.append("       when appattacktype==6 then 'SIP Application Attack' ");
            sb.append("       when appattacktype==7 then 'HTTPS Application Attack' ");
            sb.append("       when appattacktype==8 then 'HTTP Get Application Attack' ");
            sb.append("       when appattacktype==9 then 'Challenge Collapsar Attack' ");
            sb.append(" else ''").append(" end as appattacktype");
            sb.append(" ,appattacktraffic ");
            sb.append(" ,appattackrate ");
            sb.append(" ,attackareanum ");
            sb.append(" from ").append("job_ubas_ddos").append(" where 1=1 ");
            sb.append(" and dt>='").append(startTime.replaceAll("-","")).append("'");
            sb.append(" and dt<='").append(endTime.replaceAll("-","")).append("'");
            if(!"-1".equals(userGroupNo)){
                sb.append(" and pusergrougno=").append(userGroupNo).append(" ");
            }
            if(!"-1".equals(appAttackType)){
                sb.append(" and appattacktype=").append(appAttackType).append(" ");
            }
            sb.append(")t1 left join zf_v2_usergroup t2 on t1.pusergrougno=t2.USER_GROUP_ID order by attack_starttime desc");

            Map<String,Object> params = new HashMap<>();
            params.put("file_name",file_name);
            params.put("download_status","0");
            params.put("file_type","448");
            params.put("export_status",export_status);
            params.put("hive_sql",sb.toString());
            params.put("header","攻击开始时间,攻击结束时间,受攻击用户组,攻击类型,应用层攻击流量(KB),应用层攻击速率(KB/S),攻击源所在区域数");
            params.put("create_time",new Date());
            params.put("user_id",user_id);
            params.put("user_ip",user_ip);

            boolean b = hiveExportServiceImpl.addExportTask(params);

            if(b){
                returnMap.put("status","0");
            }else{
                returnMap.put("status","1");
            }
        } catch (Exception e){
            logger.error("createExportTask failed...",e);
            returnMap.put("status","2");
        }
        return returnMap;
    }


    @RequestMapping("/deleteExportTask")
    @RequiresPermission({"zf203001_export"})
    public Map<String,Object> deleteExportTask(HttpServletRequest httpServletRequest){
        Map<String,Object> returnMap = new HashMap<>();
        try{
            String id = httpServletRequest.getParameter("taskId");

            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            boolean b = dDoSAttackService.deleteExportTask(params);
            if(b){
                returnMap.put("status","0");
            }else{
                returnMap.put("status","1");
            }
        } catch (Exception e){
            logger.error("createExportTask failed...",e);
            returnMap.put("status","2");
        }
        return returnMap;
    }

    @RequestMapping(value = "/loadExportTask",method = {RequestMethod.POST,RequestMethod.GET})
    @RequiresPermission({"zf203001_query"})
    public com.aotain.zongfen.utils.PageResult<Map<String, String>> loadExportTask(HttpServletRequest httpServletRequest,
                                                                                   @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
                                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        try {
            String download_status =httpServletRequest.getParameter("download_status");
            String file_type =httpServletRequest.getParameter("file_type");
            Map<String,String> params = new HashMap<>();
            params.put("download_status",download_status);
            params.put("file_type",file_type);

            List<Map<String,String>> lists = hiveExportServiceImpl.selectExportTask(pageIndex,pageSize,params);
            PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(lists);
            com.aotain.zongfen.utils.PageResult pageResult = new com.aotain.zongfen.utils.PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        } catch (Exception e) {
            logger.error("error,", e);
            return new com.aotain.zongfen.utils.PageResult<Map<String,String>>();
        }
    }

    @RequestMapping("downloadFile")
    @ResponseBody
    public void downloadFile(HttpServletRequest request, HttpServletResponse response){
        try {
            String file_name = request.getParameter("file_name");
            String id = request.getParameter("id");
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            params.put("download_status",1);
            boolean b = hiveExportServiceImpl.updateDownloadFile(params);
            String export_path=LocalConfig.getInstance().getHashValueByHashKey("dest_dir");
            commonService.exportTemplete(request,response,file_name+"",export_path+"");
        } catch (Exception e) {
            logger.error("error,", e);
        }
    }
}
