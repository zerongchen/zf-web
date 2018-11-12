package com.aotain.zongfen.controller.system;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.common.config.pagehelper.PageResult;
import com.aotain.common.config.pagehelper.TimeScope;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.model.system.OperationLog;
import com.aotain.zongfen.service.system.IOperationLogService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * 操作日志Controller类
 *
 * @author daiyh@aotain.com
 * @date 2018/02/28
 */
@Controller
@RequestMapping(value = "/system/log")
public class OperationLogController extends BaseController{

    @Autowired
    private IOperationLogService operationLogService;

    @RequestMapping(value = {"/index"})
    public String home(ModelMap modelMap) throws Exception{
        return "/system/operationLog/index";
    }

    @RequestMapping(value = {"/list"})
    @ResponseBody
    @RequiresPermission(value="zf501001_query")
    public PageResult listLog(@Param("page") Page page,
                              @Param("userName") String userName,
                              @Param("timeScope") TimeScope timeScope) throws Exception{
        if ( !StringUtils.isEmpty(userName) ){
            page.getFuzzyParams().put("oper_user",userName);
        }
        if ( !StringUtils.isEmpty(timeScope.getStartTime()) || !StringUtils.isEmpty(timeScope.getEndTime())){
            page.getTimeScopeParams().put("oper_time",timeScope);
        }
        page.getOrderByParams().put("oper_time","DESC");
        List<OperationLog> operationLogs = operationLogService.listData(page);
        return wrapResultData(page,operationLogs);
    }

    @RequestMapping(value = {"/delete"})
    @ResponseBody
    public void listLog(@RequestParam(value = "ids[]") Integer[] ids) throws Exception{
        List<Integer> idList = Arrays.asList(ids);
        operationLogService.batchDelete(idList);
    }
}
