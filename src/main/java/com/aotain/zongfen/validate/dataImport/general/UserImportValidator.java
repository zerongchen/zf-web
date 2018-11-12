package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.model.dataimport.DataErrorInfo;
import com.aotain.zongfen.model.general.user.User;
import com.aotain.zongfen.utils.DataValidateUtils;
import com.aotain.zongfen.utils.dataimport.ErrorType;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.utils.general.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository(value="userImportValidator")
@Scope("prototype")
public class UserImportValidator {

    private static final Logger LOG = LoggerFactory.getLogger(UserImportValidator.class);
    private UserImportMgr userImportMgr;

    public UserImportMgr getUserImportMgr() {
        return userImportMgr;
    }

    public void setUserImportMgr( UserImportMgr user ) {
        this.userImportMgr = user;
    }

    public void addError(String fileName,DataErrorInfo dataErrorInfo) {
        userImportMgr.getImportResultList(fileName).setFileName(fileName);
        userImportMgr.getImportResultList(fileName).setResult(ImportConstant.DATA_IMPORT_FAIL);
        userImportMgr.getImportResultList(fileName).getDataErrorInfoList().add(dataErrorInfo);
    }

    public void setImportSuccessFlag(String fileName) {
        userImportMgr.getImportResultList(fileName).setFileName(fileName);
        userImportMgr.getImportResultList(fileName).setResult(ImportConstant.DATA_IMPORT_SUCCESS);
    }
    public boolean isError(String fileName){
        return userImportMgr.getImportResultList(fileName).hasError();
    }

    /**
     * 数据验证
     * @param file
     * @param map
     */
    public void validateData(String file,  Map<Integer, Map<Integer, String[]>> map){

//        String userTypeDesc = UserType.getUserTypeDesc(userType);

        for(Map.Entry<Integer,  Map<Integer, String[]>> entry : map.entrySet()){

            Map<Integer, String[]> chindMap =entry.getValue();
            for (Map.Entry<Integer, String[]> childEntry:chindMap.entrySet()){
                String[] values = childEntry.getValue();
                if(values.length==2){
                    if(StringUtils.isEmpty(values[0])){
                        addError(file,new DataErrorInfo( entry.getKey(), childEntry.getKey(), 0, ErrorType.NULL.getDescription()));
                    }
//                    else if(!values[0].equals(userTypeDesc)){
//                        addError(file,new DataErrorInfo( entry.getKey(), childEntry.getKey(), 0, ErrorType.TYPE_ERROR.getDescription()));
//
//                    }
                    if(StringUtils.isEmpty(values[1])){
                        addError(file,new DataErrorInfo( entry.getKey(), childEntry.getKey(), 1, ErrorType.NULL.getDescription()));
                    }else if(values[1].length()>50){
                        addError(file,new DataErrorInfo( entry.getKey(), childEntry.getKey(), 1, ErrorType.VALUE_OUT_OF_RANGE.getDescription()));
                    }else {
                        if(UserType.getUserType(values[0])==2){
                            if(!DataValidateUtils.validate(DataValidateUtils.IPV4_REGEXP,values[1])){
                                LOG.error(entry.getKey()+":"+childEntry.getKey()+"; value="+values[1]+", 格式不正确。");
                                addError(file,new DataErrorInfo( entry.getKey(), childEntry.getKey(), 1, ErrorType.PATTERN_ERROR.getDescription()));
                            }
                        }
                    }
                }
            }
        }
        if(!isError(file)){
            setImportSuccessFlag(file);
        }
        System.out.println(Thread.currentThread().getName()+"done"+UserImportValidator.class.hashCode());
    }

    /**
     * get 1st 2cd cell value witch is needed
     * @param map
     * @return
     */
    public  Map<String, List<User>> initUserMapInfo( Map<Integer, Map<Integer, String[]>> map){


        Map<String, List<User>> mapList = new HashMap<String, List<User>>();
        List<User> lists = new ArrayList<>();

        for(Map.Entry<Integer, Map<Integer, String[]>> entry:map.entrySet()){
            if(entry.getValue()!=null){
                for(Map.Entry<Integer, String[]> childEy:entry.getValue().entrySet()){
                    if(childEy.getValue().length==2){
                        int userType = UserType.getUserType(childEy.getValue()[0]);
                        if(userType==0){
                            continue;
                        }
                        User user = new User();
                        user.setUserType(userType);
                        user.setUserName(childEy.getValue()[1]);
                        lists.add(user);
                    }
                }
            }
        }
        mapList.put("userList",lists);
        return mapList;
    }

}
