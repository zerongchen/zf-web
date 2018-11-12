package com.aotain.zongfen.service.apppolicy.flowmanager;

import com.aotain.zongfen.dto.apppolicy.FlowManagerDTO;
import com.aotain.zongfen.model.common.ResponseResult;

import java.util.List;
import java.util.Map;

public interface FlowManagerService {

    List<FlowManagerDTO> getList(FlowManagerDTO flowManagerDTO);

    ResponseResult addDB(FlowManagerDTO flowManagerDTO);
    ResponseResult modifyDB(FlowManagerDTO flowManagerDTO);
    ResponseResult deleteMessage(String[] ids);

    ResponseResult reSendPolicy(FlowManagerDTO flowManagerDTO);
}
