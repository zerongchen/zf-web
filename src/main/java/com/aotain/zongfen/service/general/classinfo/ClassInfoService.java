package com.aotain.zongfen.service.general.classinfo;

import java.util.List;

import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.zongfen.dto.general.FileDetailListDTO;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.ClassFileSendMessage;
import com.aotain.zongfen.model.general.ClassInfo;

public interface ClassInfoService {

    /**
     * 前端列表展示
     *
     * @param classInfo
     * @return
     */
    List<ClassInfo> getClassInfos( ClassInfo classInfo );

    /**
     * 前端删除操作，实际上是将operate_type参数设置为1
     *
     * @param strs
     * @return
     */
    int deleteClassInfos( String[] strs );

    ResponseResult<ClassInfo> insertOrUpdate( ClassInfo classInfo );

    /**
     * 获取ip，端口，username和password
     *
     * @param messageType
     * @return
     */
    ClassFileSendMessage getMessageByType( Integer messageType );

    /**
     * 获取ip，端口，username和password 的数据集合
     *
     * @param messageType
     * @param zongFenDevice
     * @return
     */
    List<ClassFileSendMessage> getMessageByTypes( Integer messageType, ZongFenDevice zongFenDevice );
    
    List<FileDetailListDTO> getFileDetail(Integer messageType);

    String reSend(FileDetailListDTO detailListDTO);
    
	/**
	 * 
	* @Title: getServerList
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return
	* @return List<ZongFenDevice>
	* @throws
	 */
	public List<ZongFenDevice> getServerList();
}

