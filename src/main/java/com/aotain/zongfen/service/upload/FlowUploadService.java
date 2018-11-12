package com.aotain.zongfen.service.upload;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.aotain.zongfen.dto.upload.FlowUploadDTO;
import com.aotain.zongfen.model.common.ResponseResult;

public interface FlowUploadService {

    /**
     * 获取前端显示列表
     * @param record
     * @return
     */
    List<FlowUploadDTO> getPolicyList( FlowUploadDTO record);


    ResponseResult<FlowUploadDTO> addDB( FlowUploadDTO record) ;

    ResponseResult<FlowUploadDTO> modifyDb( FlowUploadDTO record);


    ResponseResult<FlowUploadDTO> delete(String[] ids);

    ResponseResult<FlowUploadDTO> reSendPolicy( FlowUploadDTO flowUploadDTO );
}
