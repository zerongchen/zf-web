package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.dataimport.DataErrorInfo;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.utils.DomainCheck;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository(value="informationPushValidator")
@Scope("prototype")
public class InformationPushValidator {

    private InformationPushImportMgr informationPushImportMgr;

    private Integer info_type;

    public Integer getInfo_type() {
        return info_type;
    }

    public void setInfo_type( Integer info_type ) {
        this.info_type = info_type;
    }

    public InformationPushImportMgr getInformationPushImportMgr() {
        return informationPushImportMgr;
    }

    public void setInformationPushImportMgr( InformationPushImportMgr informationPushImportMgr ) {
        this.informationPushImportMgr = informationPushImportMgr;
    }

    public void addError(DataErrorInfo dataErrorInfo) {
            informationPushImportMgr.getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
            informationPushImportMgr.getImportResultList().getDataErrorInfoList().add(dataErrorInfo);
    }


    public void validate(Map<Integer, Map<Integer, String[]>> map) throws ImportException {

        Set<String > set = new HashSet<>();
            for(Map.Entry<Integer, Map<Integer, String[]>> sheetEntry:map.entrySet()){
                if(sheetEntry.getValue()==null) continue;
                for(Map.Entry<Integer, String[]> rowEntry:sheetEntry.getValue().entrySet()){
                    if(rowEntry.getValue()==null) continue;
                    String[] values = rowEntry.getValue();
                    if(values.length>0){
                        String value = values[0];
                        if(info_type==1 || info_type ==2){
                            if(!StringUtils.isEmpty(value)) {
                                String check = "";
                                if (value.startsWith("http://")||value.startsWith("https://")
                                        ||value.startsWith("ftp://")||value.startsWith("sftp://")){
                                    check = "";
                                } else {
                                    check = DomainCheck.generalMatch(value);
                                }
                                if (!StringUtils.isEmpty(check)) {
                                    addError(new DataErrorInfo(sheetEntry.getKey(), rowEntry.getKey(), 1, check));
                                } else {
                                    set.add(value);
                                }
                            }
                        }else{
                            if(!StringUtils.isEmpty(value)){
                                set.add(value);
                            }
                        }

                    }
                }
            }
        if(set.size()==0){
            informationPushImportMgr.buildErrorResult(informationPushImportMgr.getFiletErrorMsg());
        }
        ImportResultList resultList = informationPushImportMgr.getImportResultList();
        if(resultList.hasError()){
            throw new ImportException(resultList);
        }

        List<String> list = new ArrayList();
        list.addAll(set);
        Map map1 = new HashMap<String, List>();
        map1.put("1",list);
        resultList.setDatas(map1);

        System.out.println("InformationPushValidator done "+ InformationPushValidator.class.hashCode());
    }
}
