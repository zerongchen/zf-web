package com.aotain.zongfen.controller.device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.zongfen.annotation.LogAnnotation;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.log.ProxyUtil;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.IdcHouse;
import com.aotain.zongfen.service.device.IdcHouseService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 机房管理Controller类
 *
 * @author daiyh@aotain.com
 * @date 2018/03/01
 */
@RequestMapping("/device/housemanage")
@Controller
public class HouseManagerController {

    private static final Logger logger = LoggerFactory.getLogger(HouseManagerController.class);

    @Autowired
    private IdcHouseService idcHouseService;

    @RequestMapping("index")
    public String home(ModelMap modelMap) throws Exception{
        return "/devicemanage/housemanage/index";
    }

    @RequiresPermission("zf302001_query")
    @RequestMapping(value = {"/list"})
    @ResponseBody
    public List<IdcHouse> listParameter(IdcHouse idcHouse) throws Exception{
        return idcHouseService.listIdcHouse(idcHouse);
    }

    @RequiresPermission("zf302001_add")
    @RequestMapping(value = {"/add"})
    @ResponseBody
    @LogAnnotation(module = 302001,type = OperationConstants.OPERATION_SAVE)
    public ResponseResult addData(@Param(value = "idcHouses") String idcHouses) throws Exception{
        ResponseResult responseResult = new ResponseResult();
        List<IdcHouse> idcHouseList = JSON.parseArray(idcHouses, IdcHouse.class);
        List<String> houseIds = new ArrayList<>();
        if (idcHouseList==null||idcHouseList.size()==0){
            return new ResponseResult();
        }
        for (int i=0;i<idcHouseList.size();i++){
            boolean existRecord = idcHouseService.existSameIdOrNameRecord(idcHouseList.get(i));
            if (existRecord){
                responseResult.setResult(1);
                responseResult.setMessage("机房ID="+idcHouseList.get(i).getHouseId()+"或机房名="+idcHouseList.get(i).getHouseName()+"记录已经存在");
                return responseResult;
            }
        }
        for (int i=0;i<idcHouseList.size();i++){
            idcHouseList.get(i).setCreateTime(new Date());
            idcHouseService.addOrUpdate(idcHouseList.get(i));
            houseIds.add(idcHouseList.get(i).getHouseId());
        }
        responseResult.setResult(0);

        try{
            String dataJson = "houseId="+JSONArray.toJSONString(houseIds);
            ProxyUtil.changeVariable(HouseManagerController.class,"addData",dataJson);
        } catch (Exception e){
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
        }

        return responseResult;

    }

    @RequiresPermission("zf302001_modify")
    @RequestMapping(value = {"/update"})
    @ResponseBody
    @LogAnnotation(module = 302001,type = OperationConstants.OPERATION_UPDATE)
    public ResponseResult updateData(@Param(value = "idcHouses") String idcHouses) throws Exception{
        ResponseResult responseResult = new ResponseResult();
        List<IdcHouse> idcHouseList = JSON.parseArray(idcHouses, IdcHouse.class);
        if (idcHouseList==null||idcHouseList.size()==0){
            return new ResponseResult();
        }
        for (int i=0;i<idcHouseList.size();i++){
            boolean existRecord = idcHouseService.existSameRecord(idcHouseList.get(i));
            if (existRecord){
                responseResult.setResult(1);
                responseResult.setMessage("机房名"+idcHouseList.get(i).getHouseName()+"已经存在");
                return responseResult;
            }
            idcHouseList.get(i).setCreateTime(new Date());
            idcHouseService.addOrUpdate(idcHouseList.get(i));

            try{
                String dataJson = "houseId="+idcHouseList.get(i).getHouseId();
                ProxyUtil.changeVariable(HouseManagerController.class,"updateData",dataJson,idcHouseList.get(i).toString());
            } catch (Exception e){
                logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
            }
        }
        responseResult.setResult(0);
        return responseResult;

    }

    @RequiresPermission("zf302001_delete")
    @RequestMapping(value = {"/delete"})
    @ResponseBody
    @LogAnnotation(module = 302001,type = OperationConstants.OPERATION_DELETE)
    public void deleteData(@RequestParam(value = "houseIds[]") String[] hosueIds) throws Exception{
        List<String> houseIdList = Arrays.asList(hosueIds);
        idcHouseService.batchDelete(houseIdList);

        try{
            String dataJson = "houseId="+JSONArray.toJSONString(hosueIds);
            ProxyUtil.changeVariable(HouseManagerController.class,"deleteData",dataJson);
        } catch (Exception e){
            logger.error("ClassName="+this.getClass()+",methodName="+ProxyUtil.methodName+",desc=change dataJson failed...",e);
        }
    }
}
