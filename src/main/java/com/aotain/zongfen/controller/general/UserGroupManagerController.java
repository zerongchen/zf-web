package com.aotain.zongfen.controller.general;


import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.model.UserGroupStrategy;
import com.aotain.common.policyapi.model.msg.UserMessage;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.controller.BaseController;
import com.aotain.zongfen.dto.general.user.UserDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.ModelType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.model.BaseEntity;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.device.DpiStaticPort;
import com.aotain.zongfen.model.general.Bras;
import com.aotain.zongfen.model.general.user.User;
import com.aotain.zongfen.model.general.user.UserGroup;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.service.general.usergroup.UserGroupService;
import com.aotain.zongfen.service.general.usergroup.UserService;
import com.aotain.zongfen.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/userGroupManager")
public class UserGroupManagerController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(UserGroupManagerController.class);


    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = {"/index"})
    public String home(ModelMap modelMap) throws Exception{
        return "/userGroupManager/index";
    }

    @RequestMapping(value = {"/users"})
    @RequiresPermission(value = "zf103001_query")
    public ModelAndView listUser(ModelAndView mv,
                           @RequestParam(required = true, name = "usergroupid") Long usergroupid) throws Exception{
        mv.addObject("userGroupId",usergroupid);
        mv.addObject("userGroupName",commonService.getUserGroupName(usergroupid));
        mv.setViewName("/userGroupManager/users");
        return mv;
    }

    @RequestMapping(value="initTable", method = {  RequestMethod.POST,RequestMethod.GET })
    @RequiresPermission(value = "zf103001_query")
    @ResponseBody
    public List<UserGroup> initTable( UserGroup userGroup ) {
        return userGroupService.getUserGroupList(userGroup);
    }

     @RequestMapping(value="getUsers", method = {  RequestMethod.POST,RequestMethod.GET })
     @ResponseBody
     @RequiresPermission(value = "zf103001_query")
     public List<UserDTO> getUsers( UserDTO dto) {
            return userService.getUsers(dto);
     }


    @RequestMapping(value="/addUser", method = {  RequestMethod.POST,RequestMethod.GET })
    @RequiresPermission(value = "zf103001_add")
    @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_SAVE)
    @ResponseBody
    public ResponseResult addUser(@RequestBody List<User> users) {
        try {
            return userService.insetUser(users);
        }catch (Exception e){
            logger.error("add single user error",e);
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(0);
            responseResult.setMessage("新增失败");
            return responseResult;
        }
    }


    @RequestMapping(value="import", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103001_add")
    @ResponseBody
    public ResponseResult<List<ImportResultList>> importUseInfo( HttpServletRequest request, UserGroup userGroup ) {
        List<ImportResultList> list =null;
        ResponseResult<List<ImportResultList>> responseResult = new ResponseResult();
        try {
           return userGroupService.addOrUpdateUserGroup(request, userGroup);
            } catch (ImportException e) {
            responseResult.setData(e.getImportResultLists());
            logger.error("import user group error ",e);
        }
        return responseResult;
    }

    @RequestMapping(value="manualAddUserGroup", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103001_add")
    @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_SAVE)
    @ResponseBody
    public ResponseResult addUserGroup(@RequestBody UserGroupStrategy userGroupStrategy ) {
        try {


            userGroupStrategy.setAction(1);
            userGroupStrategy.setMessageType(MessageType.USER_GROUP_POLICY.getId());

            String userName = SpringUtil.getSysUserName();
            BaseEntity baseEntity = new BaseEntity();
            baseEntity.setCreateOper(userName);
            baseEntity.setCreateTime(new Date());
            baseEntity.setModifyTime(new Date());
            baseEntity.setModifyOper(userName);

            UserGroup userGroup = new UserGroup();
            userGroup.setUserGroupName(userGroupStrategy.getUserGroupName());
            if (userGroupStrategy.getUserGroupId()!=null){
                userGroup.setUserGroupId(userGroupStrategy.getUserGroupId());
            }
            if (userGroupService.existSameGroupName(userGroup)){
                return new ResponseResult(1,"已经存在同名的用户组(包含删除过的)!",null,null,null);
            }
            userGroupService.manualAddOrUpdateUserGroup(userGroupStrategy,baseEntity);
            String dataJson = "messageNo="+userGroupStrategy.getMessageNo();
            try {
                ProxyUtil.changeVariable(UserGroupManagerController.class,"addUserGroup",dataJson,userGroupStrategy.objectToJson());
            } catch (Exception e) {
                logger.error("User management policy, user group management, manual add user group write log failure ",e);
            }
            return new ResponseResult(0,"添加成功!",null,null,null);
        }catch (Exception e){
            logger.error("add User Group error ",e);
        }
        return null;
    }


    @RequestMapping(value="delete", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103001_delete")
    @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_DELETE)
    @ResponseBody
    public ResponseResult deleteUserGroup(HttpServletRequest request) {
        try {
            String[] userGroupIds = request.getParameterValues("userGroupId[]");
            String[] messageNos = request.getParameterValues("messageNo[]");
            userGroupService.deleteUserGroupPolicy(userGroupIds,messageNos);
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(1);
            List<BaseKeys> keys = new ArrayList<>();
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.DELETE.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
        //   bk.setDataType(DataType.POLICY.getType());
            bk.setMessage("messageNo="+Arrays.asList(messageNos));
            /*bk.setIdName("messageNo");
            for (int i=0;i<messageNos.length;i++){
               // bk.setMessage("messageNo="+messageNos[i]);
                bk.setId(Long.valueOf(messageNos[i]));
            }*/
            keys.add(bk);
            responseResult.setKeys(keys);
            return responseResult;
        }catch (Exception e){
            logger.error("delete user group error",e);
        }
        return null;
    }

    @RequestMapping(value="/deleteUsers", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103001_delete")
   // @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_DELETE)
    @ResponseBody
    public ResponseResult deleteUsers(HttpServletRequest request ,@RequestBody List<UserDTO> dtos) {

        try {
            Date time = new Date();
            List<BaseKeys> keys = new ArrayList<>();
            List<UserMessage> list = new ArrayList<>();
            for(UserDTO userDTO:dtos){
                userDTO.setModifyTime(time);
                userDTO.setModifyOper(SpringUtil.getSysUserName());
                UserMessage userMessage = new UserMessage();
                userMessage.setUserName(userDTO.getUserName());
                userMessage.setUserType(userDTO.getUserType());
                list.add(userMessage);
            }
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.DELETE.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
            bk.setDataType(DataType.OTHER.getType());


           Long messageNo = userService.deleteSelectUsers(dtos);
            bk.setMessage("messageNo="+messageNo);
            keys.add(bk);
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(1);
            responseResult.setKeys(keys);
            return responseResult;
        }catch (Exception e){
            logger.error("delete user error",e);
        }
        return null;
    }

    @RequestMapping(value="exportTemplate", method = {  RequestMethod.GET })
    @ResponseBody
    public ResponseResult exportTemplate(HttpServletRequest request,HttpServletResponse response) {
        try {

            commonService.exportTemplete(request,response,"GroupUserTemplete");
            ResponseResult responseResult = new ResponseResult();
            BaseKeys bk =new BaseKeys();
            bk.setOperType(OperationType.EXPORT.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
            bk.setDataType(DataType.FILE.getType());
            bk.setFileName("GroupUserTemplete");
            responseResult.setKeys(Arrays.asList(bk));
            return responseResult;
        }catch (Exception e){
            logger.error("exportTemplate error ",e);
        }
        return null;
    }

    @RequestMapping(value="export.do", method = {  RequestMethod.GET })
    @RequiresPermission(value = "zf103001_export")
    @ResponseBody
    public ResponseResult export(HttpServletRequest request,HttpServletResponse response,UserDTO dto) {

        try {
        List<UserDTO> list = userService.getUsers(dto);
        List<List<?>> dataList = new ArrayList<>();
        List<Class<?>> classList = new ArrayList<Class<?>>();
        dataList.add(list);
        classList.add(UserDTO.class);
        String fileName = commonService.exportData(dataList,classList,dto.getUserGroupName(),response,request);

        ResponseResult responseResult = new ResponseResult();
        if(!StringUtils.isEmpty(fileName)){
            BaseKeys bk =new BaseKeys();
            bk.setOperType(OperationType.EXPORT.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
            bk.setDataType(DataType.FILE.getType());
            bk.setFileName(fileName);
            responseResult.setKeys(Arrays.asList(bk));
        }
            return responseResult;
        }catch (Exception e){
            logger.error("export error ",e);
        }
        return null;
    }

    @RequestMapping(value="update", method = {  RequestMethod.POST })
    @RequiresPermission(value = "zf103001_modify")
    @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_UPDATE)
    @ResponseBody
    @Deprecated
    public void updateUserGroupName(UserGroup userGroup) {

        try {

            userGroupService.updateUserGroupName(userGroup);
        }catch (Exception e){
            logger.error("update user group error ",e);
        }
    }

    @RequestMapping(value = {"/resend"})
    @RequiresPermission(value = "zf103001_redo")
    @LogAnnotation(module = 103001,type = OperationConstants.OPERATION_RESEND)
    @ResponseBody
    public ResponseResult resendSpecificDpi(@RequestParam(value = "messageNo") Long messageNo) throws Exception{
        try {

            userGroupService.resendPolicy(0L,messageNo,new ArrayList<>());
            BaseKeys bk = new BaseKeys();
            bk.setOperType(OperationType.RESEND.getType());
            bk.setMessageType(ModelType.MODEL_USER_GROUP.getMessageType());
            bk.setDataType(DataType.POLICY.getType());
            bk.setMessageNo(messageNo);
            bk.setId(messageNo);
            return wrapReturnDate(Arrays.asList(bk));
        }catch (Exception e){
            logger.error("resend user group startegy error ",e );
        }
        return null;
    }

    @RequestMapping(value = {"/getLinkUser"})
    @ResponseBody
    public List<DpiStaticPort> getLinkUser(){
        try {

            return userGroupService.getLinkUser();
        }catch (Exception e){
            logger.error("get link  user error ",e);
        }
        return null;
    }

    @RequestMapping(value = {"/getBrasUser"})
    @ResponseBody
    public List<Bras> getBrasUser(){
        try {

            return userGroupService.getBrasUser();
        }catch (Exception e){
            logger.error("get bras user error",e);
        }
        return null;
    }

}
