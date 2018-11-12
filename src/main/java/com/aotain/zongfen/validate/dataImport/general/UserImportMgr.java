package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.ImportMgr;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("userImportMgr")
@Scope("prototype")
public class UserImportMgr extends ImportMgr {


    private UserImportValidator userImportValidator;


    private ExecutorService threadPool =null;

    @Override
    public boolean checkCellNum( int sheetNum, int cells ) {
        return cells>0;
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
        return rowNo<=1000 && rowNo>0;
    }

    @Override
    public String getSheetErrorMsg() {
        return ImportConstant.EXCEL_SHEET_NO_ERROR;
    }

    @Override
    public String getFiletErrorMsg() {
        return ImportConstant.EXCEL_ROWNUM_LIMIT;
    }

    @Override
    public int getCellCountBySheetNo( int sheetNo ) {
        return 0;
    }


    public void validateData(final Map<String,Map<Integer, Map<Integer, String[]>>> map) throws ImportException {

        threadPool= Executors.newCachedThreadPool();
        final CountDownLatch cdl = new CountDownLatch(map.size());
        for(final Map.Entry<String,Map<Integer, Map<Integer, String[]>>> entry:map.entrySet()){
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    userImportValidator.validateData(entry.getKey(),entry.getValue());
                    cdl.countDown();
                }
            });
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();

        Map<String, ImportResultList> importResultMap = this.getImportResultMap();
        boolean importHasError = false;
        for (Map.Entry<String,Map<Integer, Map<Integer, String[]>>> stringMapEntry:map.entrySet()){

            if(!importResultMap.get(stringMapEntry.getKey()).hasError()){
                if(importResultMap.get(stringMapEntry.getKey())!=null){
                    importResultMap.get(stringMapEntry.getKey()).setDatas(this.initStoreData(stringMapEntry.getValue()));
                }
            }else {
                importHasError = true;
            }
        }

        if(importHasError){
            throw new ImportException((new ArrayList<ImportResultList>(getImportResultMap().values())));
        }
        System.out.println("done");

    }

    public Map<String, List> initStoreData(Map<Integer, Map<Integer, String[]>> map) {
        Map<String, List> returnMap = new HashMap<String, List>();
        returnMap.putAll(userImportValidator.initUserMapInfo(map));
        return returnMap;
    }

    public void initValidator() {
        userImportValidator = (UserImportValidator)SpringUtil.getBean("userImportValidator");
        userImportValidator.setUserImportMgr(this);

    }


}
