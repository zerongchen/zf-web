package com.aotain.zongfen.service.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CommonService {

    void exportTemplete( HttpServletRequest request, HttpServletResponse response,String fileName);

    void exportTemplete(HttpServletRequest request,HttpServletResponse response,final String fileName,final String filePath);

    String exportData(List<List<?>> dataList, List<Class<?>> classList, String fileName, HttpServletResponse response, HttpServletRequest request);
    
    void uploadFile( HttpServletRequest request, HttpServletResponse response,String fileName);

	public void uploadFile(HttpServletRequest request, HttpServletResponse response,final String fileName,String realName);
    
    String getFileNameByFilterAndRemove(String path,String str);

    String toUtf8String( String b);

    /**
     *
     * @param path 文件路径
     * @param classType 库类型
     * @param file_type 文件类型
     * @return 返回文件名
     */
    String getFileNameByType( String path, Integer classType, Integer file_type);

    void downLoadFile( HttpServletRequest request, HttpServletResponse response,String path,String fileName);
    
    /**
     * 
    * @Title: exportData
    * @Description: 导出excel保存到本地
    * @param @param dataList
    * @param @param classList
    * @param @param fileName
    * @return void
    * @throws
     */
    public String exportData( List<List<?>> dataList, List<Class<?>> classList, String fileName);

    /**
     * 根据UserId 获取用户组名
     * @param userGroupId
     * @return
     */
    String getUserGroupName(Long userGroupId);


    /**
     * 根据appType 取应用大类名称
     * @param appType
     * @return
     */
    String getAppTypeName(Integer appType);

    
    /**
     * 
    * @Title: saveFile
    * @Description: 保存文件到服务器
    * @param @param request
    * @param @param fileName 上传文件form表单中名称
    * @param @param saveName 要保存的文件名，尽量以自己功能命名
    * @param @return
    * @return String
    * @throws
     */
    public String saveFile(HttpServletRequest request,String fileName,String saveName);
}
