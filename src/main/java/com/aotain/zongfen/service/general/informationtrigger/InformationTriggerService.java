package com.aotain.zongfen.service.general.informationtrigger;

import com.aotain.zongfen.dto.general.TriggerDTO;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.Trigger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface InformationTriggerService {

    /**
     * 获取策略列表
     * @param trigger
     * @return
     */
    List<TriggerDTO> getTrigger( Trigger trigger);

    /**
     * 删除操作
     * @param array
     */
    void delete(List<Trigger> array);



    ResponseResult insertOrUpdate( HttpServletRequest request);


}
