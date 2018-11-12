package com.aotain.zongfen.controller.zfpush;

import com.alibaba.fastjson.JSONObject;
import com.aotain.common.utils.model.push.IsmsMessageAck;
import com.aotain.common.utils.model.push.IsmsPushAck;
import com.aotain.common.utils.push.PushClient;
import com.aotain.common.utils.push.PushSecurityTool;
import com.aotain.zongfen.mapper.zfpush.ZfV2PushMessageMapper;
import com.aotain.zongfen.model.zfpush.ZfV2PushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping("/zfpushack")
public class ZfPushAckController {

    private static Logger LOG = LoggerFactory.getLogger(ZfPushAckController.class);
    @Autowired
    private ZfV2PushMessageMapper zfV2PushMessageMapper;

    @Autowired
    private PushClient pushClient;

    @RequestMapping("")
    @ResponseBody
    public void zfpushack(HttpServletRequest request,IsmsMessageAck ismsMessageAck){
       // LOG.info("args pushInterfaceMessage:"+pushInterfaceMessage);
        try {
            PushSecurityTool tool = new PushSecurityTool();
            PushSecurityTool.PushDecryptResult result = tool.decrypt(ismsMessageAck.getRandVal(),ismsMessageAck.getPwdHash(),ismsMessageAck.getResult(),ismsMessageAck.getResultHash(),ismsMessageAck.getEncryptAlgorithm(),ismsMessageAck.getHashAlgorithm(),ismsMessageAck.getCompressionFormat(),ismsMessageAck.getPushVersion());
            LOG.info("PushSecurityTool.PushDecryptResult:"+result);
            IsmsPushAck ismsPushAck = JSONObject.parseObject(result.getData(),IsmsPushAck.class);
            LOG.info("args ismsPushAck:"+ismsPushAck);

            List<IsmsPushAck.PushAck> list = ismsPushAck.getPushAck();

            for(IsmsPushAck.PushAck ack:list){
                ZfV2PushMessage message = new ZfV2PushMessage();
                message.setPushid(ack.getPushId());

                /**
                 * 执行结果代码
                 * 0——完全成功；
                 * 1——保留；
                 * 2——失败；
                 */
                BigInteger resultCode = ack.getResultCode();
                if(resultCode.intValue()==0){
                    message.setPushStatus(2);
                }else if(resultCode.intValue()==1){
                    message.setPushStatus(1);
                }else if(resultCode.intValue()==2){
                    message.setPushStatus(3);
                    ZfV2PushMessage m=  zfV2PushMessageMapper.selectByPrimaryKey(ack.getPushId());
                    if(m.getRepushTimes()<3){
                        pushClient.pushMessage(ack.getPushId());
                        LOG.info("pushClient.pushMessage(ack.getPushId()):"+ack.getPushId());
                    }else{
                        LOG.info("getRepushTimes>=3:"+ack.getPushId());
                    }
                }

                zfV2PushMessageMapper.updateByPrimaryKey(message);
            }
        } catch (Exception e) {
            LOG.error("",e);
        }

    }

}
