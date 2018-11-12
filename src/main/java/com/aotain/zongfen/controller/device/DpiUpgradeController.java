package com.aotain.zongfen.controller.device;


import com.aotain.common.policyapi.model.DpiDeviceInfoStrategy;
import com.aotain.zongfen.interceptor.RequiresPermission;
import com.aotain.zongfen.mapper.device.DPIRecDeviceMapper;
import com.aotain.zongfen.mapper.device.DpiUpgradeMapper;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/device/dpiUpgrade")
public class DpiUpgradeController {

    private static Logger LOG = LoggerFactory.getLogger(DpiUpgradeController.class);

    @Autowired
    private DpiUpgradeMapper dpiUpgradeMapper;
    @Autowired
    private DPIRecDeviceMapper dpiRecDeviceMapper;

    @RequestMapping(value ="/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/devicemanage/dpiupgrade/index");
        return mav;
    }


    @RequiresPermission(value = "zf301004_query")
    @RequestMapping(value = {"/getDpiUpgrade"})
    @ResponseBody
    public PageResult<Map<String,String>> getDpiUpgrade(@RequestParam(required = true, name = "pageSize") Integer pageSize,
                                                       @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
                                                       HttpServletRequest request){
        try {
            Map<String,String> params = new HashMap<>();
            String DeploySiteName = request.getParameter("DeploySiteName");
            String update_time = request.getParameter("update_time");
            params.put("DeploySiteName",DeploySiteName);
            params.put("update_time",update_time);

            PageResult<Map<String,String>> result = new PageResult<>();
            PageHelper.startPage(pageIndex,pageSize);
            List<Map<String,String>> list = dpiUpgradeMapper.getDpiUpgrade(params);
            PageInfo<Map<String,String>> pageResult = new PageInfo<>(list);
            result.setRows(list);
            result.setTotal(pageResult.getTotal());

            return result;
        } catch (Exception e) {
            LOG.error("get softwareManage Data fail ",e);
        }
        return null;
    }

    @RequiresPermission(value = "zf301004_query")
    @RequestMapping(value = {"/getDpiUpgradePort"})
    @ResponseBody
    public PageResult<Map<String,String>> getDpiUpgradePort(@RequestParam(required = true, name = "pageSize") Integer pageSize,
                                                        @RequestParam(required = true, name = "pageIndex") Integer pageIndex,
                                                        HttpServletRequest request){
        try {
            Map<String,String> params = new HashMap<>();
            String DeploySiteName = request.getParameter("DeploySiteName");
            String update_time = request.getParameter("update_time");
            params.put("DeploySiteName",DeploySiteName);
            params.put("update_time",update_time);

            PageResult<Map<String,String>> result = new PageResult<>();
            PageHelper.startPage(pageIndex,pageSize);
            List<Map<String,String>> list = dpiUpgradeMapper.getDpiUpgradePort(params);
            PageInfo<Map<String,String>> pageResult = new PageInfo<>(list);
            result.setRows(list);
            result.setTotal(pageResult.getTotal());
            return result;

        } catch (Exception e) {
            LOG.error("get softwareManage Data fail ",e);
        }
        return null;
    }

    @RequiresPermission(value = "zf301004_query")
    @RequestMapping(value = {"/getDpiUpgradePortDetail"})
    @ResponseBody
    public Map<String,Object> getDpiUpgradePortDetail(HttpServletRequest request){
        try {
            Map<String,String> params = new HashMap<>();
            String Upgrade_Id = request.getParameter("Upgrade_Id");
            String DeploySiteName = request.getParameter("DeploySiteName");
            params.put("Upgrade_Id",Upgrade_Id);
            params.put("DeploySiteName",DeploySiteName);

            Map<String,Object> returnMaps = new HashMap<>();
            List<Map<String,String>> list = dpiUpgradeMapper.getDpiUpgradePortDetail(params);
            returnMaps.put("rows",list);
            returnMaps.put("size",list.size());
            return returnMaps;

        } catch (Exception e) {
            LOG.error("getDpiUpgradePortDetail Data fail ",e);
        }
        return null;
    }

    @RequiresPermission(value = "zf301004_query")
    @RequestMapping(value = {"/getDpiSiteName"})
    @ResponseBody
    public Map<String,Object> getDpiSiteName(){
        try {
            Map<String,Object> returnMaps = new HashMap<>();
            List<DpiDeviceInfoStrategy> list = dpiRecDeviceMapper.getInfoByName(null);
            returnMaps.put("rows",list);
            returnMaps.put("size",list.size());
            return returnMaps;

        } catch (Exception e) {
            LOG.error("getDpiSiteName Data fail ",e);
        }
        return null;
    }

}
