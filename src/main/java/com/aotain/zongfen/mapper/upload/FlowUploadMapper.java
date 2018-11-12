package com.aotain.zongfen.mapper.upload;

import com.aotain.common.config.annotation.MyBatisDao;
import com.aotain.zongfen.dto.upload.FlowUploadDTO;
import com.aotain.zongfen.model.upload.FlowUpload;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface FlowUploadMapper {

    int deleteByPrimaryKey(Long messageNo);

    int insertSelective(FlowUpload record);

    FlowUpload selectByPrimaryKey(Long messageNo);

    List<FlowUpload> selectByPrimaryKeys(@Param("array") Long[] array);

    int updateByPrimaryKeySelective(FlowUpload record);

    int isSameMessageName(FlowUploadDTO record);

    List<FlowUploadDTO> getPolicyList( FlowUploadDTO record);

    /**
     * 获取与指定应用用户相关策略table数据
     * @param record
     * @return
     */
    List<FlowUploadDTO> getAppUserPolicyList( FlowUploadDTO record);
    /**
     * 加了流量流向的策略定制
     * @param record
     * @return
     */
    List<FlowUploadDTO> selectPolicyList( FlowUploadDTO record);
    /**
     * 加了ddos的策略定制
     * @param record
     * @return
     */
    List<FlowUploadDTO> selectDdosPolicyList( FlowUploadDTO record);
    
    /**
     * 加了WebPush的策略定制
     * @param record
     * @return
     */
    List<FlowUploadDTO> selectWebPushPolicyList( FlowUploadDTO record);

    Map<String,String> getAppPolicyMessageName(Map<String, String> params);
}