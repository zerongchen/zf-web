package com.aotain.zongfen.service.general.httpget;

import com.aotain.zongfen.dto.general.HttpGetBWDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.HttpGetBW;
import scala.Int;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HttpGetListService {

    /**
     * 获取domin 列表
     * @param httpGetBW
     * @return
     */
    List<HttpGetBWDTO> getDomain( HttpGetBW httpGetBW);

    /**
     * 导入
     * @param request
     */
    ImportResultList handleImport( HttpServletRequest request) throws ImportException;

    /**
     * 生成文件并下发
     * @param httpGetBW
     * @return
     */
    String createAndSend(HttpGetBW httpGetBW,Integer zonfenId);

    /**
     *  判断是否需要提示
     * @param alarmType
     * @return
     */
    Boolean isInAlarm( Integer alarmType);

    /**
     * 将文件ftp到相应服务器
     * @param httpgetList
     * @return
     */
    String sendHttpGetMessage(List<HttpGetListServiceImpl.HttpGetInfo> httpgetList,Integer zonfenId);

    /**
     * 根据IDs 删除, 并更新对应分类库的提示信息
     * @param ids
     * @param type
     * @return
     */
    int deleteBw(String[] ids,Integer type);

    /**
     * 更新
     * @param recode
     * @return
     */
    ResponseResult updateBw( HttpGetBW recode);

    /**
     * 获取前端提示信息
     * @return
     */
    Map<String,Integer> getWarning();

}
