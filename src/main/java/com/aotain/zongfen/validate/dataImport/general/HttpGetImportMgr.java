package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.ImportMgr;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("httpGetImportMgr")
public class HttpGetImportMgr extends ImportMgr {

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
}
