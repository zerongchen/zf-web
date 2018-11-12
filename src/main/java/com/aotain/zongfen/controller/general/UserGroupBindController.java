package com.aotain.zongfen.controller.general;


import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.UserPolicyBindStrategy;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.controller.apppolicy.AppUserController;
import com.aotain.zongfen.dto.apppolicy.DdosExceptFlowStrategyPo;
import com.aotain.zongfen.dto.common.Multiselect;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.general.userbind.UserBindMapper;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.userbind.UserBindModel;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.userbind.UserBindService;
import com.aotain.zongfen.service.general.usergroup.UserGroupService;
import com.aotain.zongfen.service.general.usergroup.UserService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userGroupBind")
public class UserGroupBindController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(UserGroupBindController.class);

    @Autowired
    private UserBindMapper userBindMapper;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserBindService userBindService;


    @RequestMapping(value = {"/index"})
    public String home(ModelMap modelMap) throws Exception{
        return "/userGroupBind/index";
    }

    @RequestMapping(value="initTable", method = {  RequestMethod.POST,RequestMethod.GET })
    @RequiresPermission(value = "zf103003_query")
    @ResponseBody
    public PageResult<UserBindModel> initTable(UserBindModel userBind,
                                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);
            List<UserBindModel> lists = userBindService.getUserGroupList(userBind);
            PageInfo<UserBindModel> pageInfo = new PageInfo<UserBindModel>(lists);
            PageResult pageResult = new PageResult(pageInfo.getTotal(),lists);
            return pageResult;
        } catch (Exception e) {
            logger.error("error ",e);
            return new PageResult<UserBindModel>();
        }
    }

    @RequestMapping(value="/add", method = {  RequestMethod.POST,RequestMethod.GET })
    @RequiresPermission(value = "zf103003_add")
    @LogAnnotation(module = 103003,type = OperationConstants.OPERATION_SAVE)
    @ResponseBody
    public ResponseResult add(HttpServletRequest request) {
        ResponseResult responseResult = new ResponseResult();
        try {
            List<String> messageNos =  userBindService.addUserBind(request);
            responseResult.setResult(1);
            responseResult.setMessage("新增成功");

            String dataJson = "messageNo=";

            for(int i=0;i<messageNos.size();i++){
                if(i==messageNos.size()-1){
                    dataJson += messageNos.get(i);
                }else{
                    dataJson += messageNos.get(i)+",";
                }
            }
            ProxyUtil.changeVariable(UserGroupBindController.class,"add",dataJson);

           return responseResult;
        }catch (Exception e){
            logger.error("add single user error"+e);
            responseResult.setResult(0);
            responseResult.setMessage("新增失败");
            return responseResult;
        }
    }

    @RequestMapping(value="delete", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103003_delete")
    @LogAnnotation(module = 103003,type = OperationConstants.OPERATION_DELETE)
    @ResponseBody
    public ResponseResult deleteUserGroup(HttpServletRequest request) {
        ResponseResult responseResult = null;
        try {
            String[] bindIds = request.getParameterValues("bindId[]");
            String[] bindMessageTypes = request.getParameterValues("bindMessageType[]");
            String[] bindMessageNos = request.getParameterValues("bindMessageNo[]");
            String[] messageNos = request.getParameterValues("messageNo[]");
            userBindService.deleteBindPolicy(bindIds,bindMessageTypes,bindMessageNos);
            //   userGroupService.deleteUserGroupPolicy(userGroupIds,messageNos);
            responseResult = new ResponseResult();
            responseResult.setResult(1);

            String dataJson = "messageNo=";

            for(int i=0;i<messageNos.length;i++){
                if(i==messageNos.length-1){
                    dataJson += messageNos[i];
                }else{
                    dataJson += messageNos[i]+",";
                }
            }
            ProxyUtil.changeVariable(UserGroupBindController.class,"deleteUserGroup",dataJson);

        } catch (Exception e) {
            responseResult = new ResponseResult();
            responseResult.setResult(0);
        }


        return responseResult;
    }

    @RequestMapping(value = {"/resend"})
    @RequiresPermission(value = "zf103003_redo")
    @LogAnnotation(module = 103003,type = OperationConstants.OPERATION_RESEND)
    @ResponseBody
    public ResponseResult resendSpecificDpi(@RequestParam(value = "bindId") Long bindId) throws Exception{

        ResponseResult responseResult = null;
        try {
            userBindService.resendPolicy(0L,bindId,new ArrayList<>());
            responseResult = new ResponseResult();
            responseResult.setResult(1);

            // 重发关联的绑定策略
            Map<String, Object> queryMap = new HashedMap();
            queryMap.put("bindId", bindId);
            UserPolicyBindStrategy userPolicyBindStrategy = userBindMapper.getByBindMessageNoAndType(queryMap);

            String dataJson = "messageNo="+userPolicyBindStrategy.getMessageNo();
            ProxyUtil.changeVariable(UserGroupBindController.class,"resendSpecificDpi",dataJson);

            return responseResult;
        } catch (Exception e) {
            responseResult = new ResponseResult();
            responseResult.setResult(0);
            return responseResult;
        }


    }




    @RequestMapping(value="getbindMessageName", method = {  RequestMethod.POST,RequestMethod.GET })
    @ResponseBody
    public List<Multiselect> getbindMessageName(@RequestParam(value="bindMessageType") Long bindMessageType) {
        try {
            if(StringUtils.isEmpty(bindMessageType)){
                return null;
            }
            return userBindService.getbindMessageName(bindMessageType);
        } catch (Exception e) {
            logger.error("",e);
        }
        return null;
    }

}
