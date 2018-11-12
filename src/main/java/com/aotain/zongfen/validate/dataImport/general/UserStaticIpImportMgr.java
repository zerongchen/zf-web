package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.common.policyapi.model.msg.IpMsg;
import com.aotain.zongfen.dto.general.user.IpUserDTO;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.GenIPAddress;
import com.aotain.zongfen.model.general.user.UserStaticIP;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.ImportMgr;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Component("userStaticIpImportMgr")
public class UserStaticIpImportMgr extends ImportMgr{

    private Integer operate;

    public Integer getOperate() {
        return operate;
    }

    public void setOperate( Integer operate ) {
        this.operate = operate;
    }

    @Override
    public boolean checkCellNum( int sheetNum, int cells ) {
        return true;
    }

    @Override
    public boolean checkSheetNum( Workbook wb ) {
        return wb.getNumberOfSheets()>0;
    }

    @Override
    public boolean checkFileLimit( Workbook wb ) {
        int rowNo = 0;
        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            rowNo += sheet.getPhysicalNumberOfRows()-1;
        }
        return rowNo>0;
    }

    @Override
    public String getSheetErrorMsg() {
        return ImportConstant.EXCEL_SHEET_NO_ERROR;
    }

    @Override
    public String getFiletErrorMsg() {
        return ImportConstant.EXCEL_EMPTY_WARN;
    }

    @Override
    public int getCellCountBySheetNo( int sheetNo ) {
        return 0;
    }

    public void validate(Map<Integer,Map<Integer, String[]>> map) throws ImportException {
        getUserStaticIpValidator(getOperate()).validate(map);
        if(getImportResultList().hasError()){
            throw new ImportException(getImportResultList());
        }
        Map<String,List> dataMap = new HashMap<String,List>();
        for (Map.Entry<Integer,Map<Integer, String[]>> entry:map.entrySet()){
            Map<Integer, String[]> childMap = entry.getValue();
            if(childMap==null || childMap.isEmpty()) continue;
            for (Map.Entry<Integer, String[]> childEntry:childMap.entrySet()){
                String[] array = childEntry.getValue();
                List<IpMsg> ips = dataMap.get(array[0]);
                if(ips==null){
                    ips = new ArrayList<IpMsg>();
                    IpMsg ipMsg = new IpMsg();
                    ipMsg.setUserIp(array[1]);
                    ipMsg.setUserIpPrefix(Integer.parseInt(array[2]));
                    ips.add(ipMsg);
                    dataMap.put(array[0],ips);
                }else {
                    IpMsg ipMsg = new IpMsg();
                    ipMsg.setUserIp(array[1]);
                    ipMsg.setUserIpPrefix(Integer.parseInt(array[2]));
                    ips.add(ipMsg);
                    dataMap.put(array[0],ips);
                }
//                list.add(new UserStaticIP(array[0],array[1],Integer.parseInt(array[2]),1));
            }
        }
        if (dataMap.isEmpty()){
            getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
            getImportResultList().setDescribtion(ImportConstant.EXCEL_EMPTY_WARN);
            throw new ImportException(getImportResultList());
        }
        setData(dataMap);
    }

    private void setData(Map<String,List> map ){
        getImportResultList().setDatas(map);
        getImportResultList().setResult(ImportConstant.DATA_IMPORT_SUCCESS);
        getImportResultList().setDescribtion("导入成功");
    }

    private UserStaticIpValidator getUserStaticIpValidator(int operate){
        UserStaticIpValidator userStaticIpValidator = (UserStaticIpValidator) SpringUtil.getBean("userStaticIpValidator");
        userStaticIpValidator.setUserStaticIpImportMgr(this);
        userStaticIpValidator.setOperate(operate);
        return userStaticIpValidator;
    }

    //普通无校验导入
    public Map<String, List<IpUserDTO>> readDataFromStreams(Map<String,InputStream> map) throws ImportException {
        boolean hasError = false;
        if(map!=null && map.size()>0){
            Map<String, List<IpUserDTO>> fileMap = new HashMap<>();
            for(Map.Entry<String,InputStream> entry : map.entrySet()){
               // List<IpUserDTO> singleMap = readDataFromStreams(entry.getKey(),entry.getValue());
                List<IpUserDTO> singleMap = null;
                if(singleMap!=null){
                    fileMap.put(entry.getKey(),singleMap);
                }
                if(getImportResultList(entry.getKey()).hasError() ){
                    hasError = true;
                }

            }
            if(hasError){
                throw new ImportException(new ArrayList<ImportResultList>(getImportResultMap().values()));
            }
            return fileMap;
        }
        return null;
    }

}

