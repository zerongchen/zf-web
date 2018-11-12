package com.aotain.zongfen.controller.monitor;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.aotain.common.config.LocalConfig;
import com.aotain.zongfen.dto.monitor.DataUploadDetailMonitorDTO;
import com.aotain.zongfen.interceptor.RequiresPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aotain.zongfen.dto.monitor.MonitorTaskAlarmDTO;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.monitor.MonitorTaskAlarm;
import com.aotain.zongfen.service.monitor.MonitorTaskAlarmService;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;

/**
 * 告警监控
 *
 * @author DongBoye
 */
@Controller
@RequestMapping("/monitor/alarm")
public class MonitorTaskAlarmController {

    private static Logger logger = LoggerFactory.getLogger(MonitorTaskAlarmController.class);

    @Autowired
    private MonitorTaskAlarmService monitorTaskAlarmService;

    @RequestMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/monitor/alarm/index");
        return mav;
    }

    @RequestMapping(value = "/list")
    @RequiresPermission(value = "zf401001_query")
    @ResponseBody
    public PageResult<MonitorTaskAlarmDTO> list(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize, MonitorTaskAlarmDTO dto) {
        return monitorTaskAlarmService.getList(pageIndex, pageSize, dto);
    }

    @RequestMapping(value = "/solution")
    @RequiresPermission(value = "zf401001_deal")
    @ResponseBody
    public PageResult<MonitorTaskAlarmDTO> solution(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                                    @RequestParam(required = false, defaultValue = "10") Integer pageSize, MonitorTaskAlarmDTO dto) {
        return monitorTaskAlarmService.getDealSolutions(pageIndex, pageSize, dto);
    }

    @RequestMapping(value = "/addSolution")
    @RequiresPermission(value = "zf401001_deal")
    @ResponseBody
    public ResponseResult<MonitorTaskAlarm> addSolution(MonitorTaskAlarm alarm) {
        ResponseResult<MonitorTaskAlarm> result = new ResponseResult<MonitorTaskAlarm>();
        List<BaseKeys> keys = new ArrayList<BaseKeys>();
        if (alarm.getAlarmId() == null) {
            result.setResult(1);
            result.setMessage("处理失败,alarmId为空！");
        } else {

            if (alarm.getDealSolution() == null || "".equals(alarm.getDealSolution().trim())) {
                result.setResult(1);
                result.setMessage("处理失败，处理内容不为空！");
            } else {
                alarm.setDealTime(new Date());
                alarm.setDealStatus((short) 1);
                alarm.setDealUser(SpringUtil.getSysUserName());

                monitorTaskAlarmService.addSolution(alarm);

                BaseKeys bk = new BaseKeys();
                bk.setDataType(DataType.OTHER.getType());
                bk.setMessageType(ModelType.MODEL_MONITOR_ALARM.getModel());
                bk.setOperType(OperationType.DEAL.getType());
                bk.setMessage("alarmId=" + alarm.getAlarmId());
                keys.add(bk);
                result.setKeys(keys);
                result.setResult(0);
                result.setMessage("处理成功！");
            }
        }
        return result;
    }

    @RequestMapping(value = "/getRefreshTime")
    @ResponseBody
    public String getRefreshTime() {
        return LocalConfig.getInstance().getHashValueByHashKey("monitor.alarm.refresh.time");
    }

    @RequestMapping(value = "/listDetail")
    @RequiresPermission(value = "zf401001_query")
    @ResponseBody
    public PageResult<Map<String, String>> listDetail(@RequestParam(required = true, defaultValue = "1") Integer pageIndex,
                                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize, HttpServletRequest request) {
        try {

            String fileType = request.getParameter("fileType");
            String reportType = request.getParameter("reportType");
            String statTime = request.getParameter("statTime");
            String sort = request.getParameter("sort");
            String order = request.getParameter("order");

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
            Long sFileTime = format.parse(statTime).getTime() / 1000;
            Long eFileTime = sFileTime + 3600;
            Map<String, String> params = new HashMap<>();
            params.put("timeout", LocalConfig.getInstance().getHashValueByHashKey("monitor.file.upload.timeout"));
            params.put("fileType", fileType);
            params.put("sort", sort);
            params.put("order", order);
            params.put("sFileTime", sFileTime + "");
            params.put("eFileTime", eFileTime + "");
            params.put("reportType", reportType + "");
            return monitorTaskAlarmService.getFileList(pageIndex, pageSize, params);
        } catch (Exception e) {
            logger.error("search fail", e);
        }
        return null;
    }
}
