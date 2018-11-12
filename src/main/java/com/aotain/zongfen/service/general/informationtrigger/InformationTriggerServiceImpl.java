package com.aotain.zongfen.service.general.informationtrigger;

import com.aotain.zongfen.dto.general.TriggerDTO;
import com.aotain.zongfen.mapper.general.TriggerMapper;
import com.aotain.zongfen.mapper.general.TriggerRelationMapper;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.general.Trigger;
import com.aotain.zongfen.model.general.TriggerRelation;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class InformationTriggerServiceImpl implements InformationTriggerService {


    @Autowired
    private TriggerMapper triggerMapper;

    @Autowired
    private TriggerRelationMapper triggerRelationMapper;

    @Override
    public List<TriggerDTO> getTrigger( Trigger trigger ) {
        return triggerMapper.getTrigger(trigger);
    }

    @Override
    public void delete( List<Trigger>  triggers) {
        if(triggers.size()<=0){
            return;
        }
        triggerMapper.delete(triggers);
    }

    @Override
    @Transactional
    public ResponseResult insertOrUpdate( HttpServletRequest request ) {

        String triggerName = request.getParameter("triggerName");
        Integer triggerType = Integer.parseInt(request.getParameter("triggerType"));
        String id = request.getParameter("triggerId");
        boolean isUpdate = !StringUtils.isEmpty(id);
        Integer triggerId = null;
        ResponseResult  responseResult = null;
        Trigger trigger = new Trigger();
        if(isUpdate){
            String createTimeStr = request.getParameter("createTimeStr");
            trigger.setCreateTime(DateUtils.parseTimesTampSql(createTimeStr));
            triggerId = Integer.parseInt(id);
            triggerMapper.deleteByPrimaryKey(triggerId);
            triggerRelationMapper.deleteByTriggerId(triggerId);
        }

        if(triggerMapper.countByTriggerName(triggerName)>0){
            responseResult = new ResponseResult();
            responseResult.setResult(0);
            responseResult.setMessage("该策略名已经存在");
            return responseResult;
        }

        trigger.setTriggerId(triggerId);
        trigger.setModifyOper(SpringUtil.getSysUserName());
        trigger.setCreateOper(SpringUtil.getSysUserName());
        trigger.setTriggerName(triggerName);
        trigger.setTriggerFlag(1);
        trigger.setOperateType(0);
        trigger.setTriggerType(triggerType);
        triggerMapper.insertSelective(trigger);
        triggerId = trigger.getTriggerId();

        if(triggerType == 2){
            String[] kwlinkId = request.getParameter("KwlinkId").split(",");
            int len = kwlinkId.length;
            Integer[] array = new Integer[len];
            for (int i = 0; i<len ; i++){
                array[i] = Integer.parseInt(kwlinkId[i]);
            }
            triggerRelationMapper.insertKwArrays(triggerId,array);
        }else if(triggerType == 0){
            String[] HostlinkIds = request.getParameter("HostlinkId").split(",");
            int len = HostlinkIds.length;
            Integer[] array = new Integer[len];
            for (int i = 0; i<len ; i++){
                array[i] = Integer.parseInt(HostlinkIds[i]);
            }
            triggerRelationMapper.insertHostArrays(triggerId,array);
        }else if(triggerType == 1){
            Integer wllinkId = Integer.parseInt(request.getParameter("WllinkId"));
            TriggerRelation triggerRelation = new TriggerRelation();
            triggerRelation.setTriggerId(triggerId);
            triggerRelation.setTriggerHostListid(wllinkId);
            triggerRelationMapper.insertSelective(triggerRelation);
        }
        responseResult = new ResponseResult();
        responseResult.setResult(1);
        return responseResult;
    }


}
