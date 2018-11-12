package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.mapper.general.user.UserStaticIPMapper;
import com.aotain.zongfen.model.dataimport.DataErrorInfo;
import com.aotain.zongfen.model.general.user.UserStaticIP;
import com.aotain.zongfen.utils.DataValidateUtils;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.dataimport.ErrorType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository(value="userStaticIpValidator")
@Scope("prototype")
public class UserStaticIpValidator {

    private static final Logger LOG = LoggerFactory.getLogger(UserStaticIpValidator.class);

    private Integer operate;

    public Integer getOperate() {
        return operate;
    }

    public void setOperate( Integer operate ) {
        this.operate = operate;
    }

    private  UserStaticIpImportMgr userStaticIpImportMgr;

    public void setUserStaticIpImportMgr(UserStaticIpImportMgr userStaticIpImportMgr){
        this.userStaticIpImportMgr = userStaticIpImportMgr;
    }

    private void addError(DataErrorInfo dataErrorInfo){
        userStaticIpImportMgr.getImportResultList().addError(dataErrorInfo);
    }

    public void validate( Map<Integer,Map<Integer, String[]>> map){

        List<UserStaticIP> exitIps = null;
        if(operate ==0){
            exitIps = new ArrayList<UserStaticIP>();
        }else {
            exitIps = getExietIP();
        }
        for (Integer sheetNum:map.keySet()){
            Map<Integer, String[]> childMap = map.get(sheetNum);
            if(childMap==null || childMap.isEmpty()) continue;
            for (Integer rowNum:childMap.keySet()){
                String[] array = childMap.get(rowNum);
                if(array!=null){
                    if(array.length<3){
                        addError(new DataErrorInfo(sheetNum,rowNum,array.length,ErrorType.NULL.getDescription()));
                        LOG.error("IP地址用户信息导入校验失败,列数不对,actually "+array.length+",required 3");
                    }else {
                        if(StringUtils.isEmpty(array[0])){
                            addError(new DataErrorInfo(sheetNum,rowNum,0,ErrorType.NULL.getDescription()));
                            LOG.error("IP地址用户信息导入校验失败,第一列为空");
                            continue;
                        }else if(array[0].length()>50){
                            addError(new DataErrorInfo(sheetNum,rowNum,0, ErrorType.VALUE_OUT_OF_RANGE.getDescription() ));
                            LOG.error("IP地址用户信息导入校验失败,第一列长度超过范围50");
                            continue;
                        }
                        if(StringUtils.isEmpty(array[1])){
                            addError(new DataErrorInfo(sheetNum,rowNum,1,ErrorType.NULL.getDescription()));
                            LOG.error("IP地址用户信息导入校验失败,第二列为空");
                            continue;
                        }else {
                            if(DataValidateUtils.validate(DataValidateUtils.IPV6_HEX_REGEXP,array[1])){
                                if(StringUtils.isEmpty(array[2])){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.NULL.getDescription()));
                                    continue;
                                }else if(!DataValidateUtils.validate(DataValidateUtils.NUMBER_REGEXP,array[2]) ){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.PATTERN_ERROR.getDescription()));
                                    continue;
                                }else if(Integer.parseInt(array[2])>32){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.OUT_OF_RANGE.getDescription()));
                                    continue;
                                }else {
                                    if (exitIps.size()<1) continue;
                                    boolean isExit =false;
                                    loop1:for (UserStaticIP userStaticIP:exitIps){
                                        if(array[1].equals(userStaticIP.getUserip()) && Integer.parseInt(array[2]) == userStaticIP.getUseripPrefix()){
                                            addError(new DataErrorInfo(sheetNum,rowNum,2,"Ip + 前缀"+ErrorType.REPEAT.getDescription()));
                                            isExit = true;
                                            break loop1;
                                        }
                                    }
                                    if(!isExit){
                                        exitIps.add(new UserStaticIP(array[1],Integer.parseInt(array[2])));
                                    }
                                }
                            }else if(DataValidateUtils.validate(DataValidateUtils.IPV4_REGEXP,array[1])){
                                if(StringUtils.isEmpty(array[2])){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.NULL.getDescription()));
                                    continue;
                                }else if(!DataValidateUtils.validate(DataValidateUtils.NUMBER_REGEXP,array[2]) ){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.PATTERN_ERROR.getDescription()));
                                    LOG.error("格式错误，第"+sheetNum+"个表，第{"+rowNum+"}行"+array[2]+" 前缀长度不为数字");
                                    continue;
                                }else if(Integer.parseInt(array[2])>64){
                                    addError(new DataErrorInfo(sheetNum,rowNum,2,ErrorType.OUT_OF_RANGE.getDescription()));
                                    continue;
                                }else {
                                    if (exitIps.size()<1) continue;
                                    boolean isExit =false;
                                    loop1:for (UserStaticIP userStaticIP:exitIps){
                                        if(array[1].equals(userStaticIP.getUserip()) && Integer.parseInt(array[2]) == userStaticIP.getUseripPrefix()){
                                            LOG.error("[Ip address user info] sheetNum="+sheetNum+",rowNum="+rowNum+",column=2, Ip + prefix("+array[1]+"/"+array[2]+") repeat");
                                            addError(new DataErrorInfo(sheetNum,rowNum,2,"Ip + 前缀"+ErrorType.REPEAT.getDescription()));
                                            isExit=true;
                                            break loop1;
                                        }
                                    }
                                    if(!isExit){
                                        exitIps.add(new UserStaticIP(array[1],Integer.parseInt(array[2])));
                                    }
                                }
                            }else {
                                addError(new DataErrorInfo(sheetNum,rowNum,1, ErrorType.PATTERN_ERROR.getDescription() ));
                                continue;
                            }
                        }
                    }
                }
            }
        }
        exitIps.clear();
    }

    /**
     * 获取已经存在的IP
     * @return
     */
    private List<UserStaticIP> getExietIP(){
        return getUserStaticIPMapper().getExitList(new UserStaticIP());
    }

    private UserStaticIPMapper getUserStaticIPMapper(){
        return (UserStaticIPMapper)SpringUtil.getBean("userStaticIPMapper");
    }
}
