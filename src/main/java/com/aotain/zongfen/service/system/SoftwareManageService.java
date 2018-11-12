package com.aotain.zongfen.service.system;

import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ChinaArea;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.utils.PageResult;

import java.util.List;
import java.util.Map;

public interface SoftwareManageService {


    PageResult<Map<String,String>> getInitTable(Integer pageSize, Integer pageIndex, Map<String, String> params);

    PageResult<Map<String,List<String>>> getSoftwareProvider();

    int addProvider(List<Map<String, String>> pMapList);

    void updateProvider(Map<String, String> pMap);

    void deleteProvider(String providerShort);
}
