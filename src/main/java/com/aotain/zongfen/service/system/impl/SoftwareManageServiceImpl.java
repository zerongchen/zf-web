package com.aotain.zongfen.service.system.impl;

import com.aotain.zongfen.mapper.system.SoftwareProviderMapper;
import com.aotain.zongfen.service.system.SoftwareManageService;
import com.aotain.zongfen.utils.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.avro.generic.GenericData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class SoftwareManageServiceImpl implements SoftwareManageService {

    @Autowired
    private SoftwareProviderMapper softwareProviderMapper;

    @Override
    public PageResult<Map<String, String>> getInitTable(Integer pageSize, Integer pageIndex, Map<String, String> params) {
        PageResult<Map<String,String>> result = new PageResult<>();
        PageHelper.startPage(pageIndex,pageSize);
        List<Map<String, String>> lists = softwareProviderMapper.getInitTable(params);
        PageInfo<Map<String,String>> pageResult = new PageInfo<>(lists);
        result.setRows(lists);
        result.setTotal(pageResult.getTotal());
        return result;
    }

    @Override
    public PageResult<Map<String, List<String>>> getSoftwareProvider() {
        PageResult<Map<String, List<String>>> result = new PageResult<>();
        List<String> nList = new ArrayList<>();
        List<String> cList = new ArrayList<>();


        List<Map<String, List<String>>> rlists=new ArrayList<>();
        Map<String, List<String>> rMap = new HashMap<>();
        List<Map<String, String>> mlists = softwareProviderMapper.getSoftwareProvider();
        for(Map<String, String> k:mlists){
            if(k.containsKey("software_provider_name")){
                nList.add(k.get("software_provider_name"));
            }
            if(k.containsKey("software_provider")){
                cList.add(k.get("software_provider"));
            }
        }
        rMap.put("software_provider_name",nList);
        rMap.put("software_provider",cList);
        rlists.add(rMap);

        PageInfo<Map<String, List<String>>> pageResult = new PageInfo<>(rlists);
        result.setRows(rlists);
        result.setTotal(pageResult.getTotal());
        return result;
    }

    @Override
    public int addProvider(List<Map<String, String>> pMapList) {
        return softwareProviderMapper.addProvider(pMapList);
    }

    @Override
    public void updateProvider(Map<String, String> pMap) {
        softwareProviderMapper.updateProvider(pMap);
    }

    @Override
    public void deleteProvider(String providerShort) {
        softwareProviderMapper.deleteProvider(providerShort);
    }
}
