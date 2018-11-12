package com.aotain.zongfen.validate.dataImport.general;

import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.ImportMgr;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("informationPushImportMgr")
@Scope("prototype")
public class InformationPushImportMgr extends ImportMgr{

    private Integer info_type;

    private InformationPushValidator informationPushValidator;


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
        return "空文件，请重新选择文件";
    }

    @Override
    public int getCellCountBySheetNo( int sheetNo ) {
        return 0;
    }


    /**
     * 校验器
     * @param map
     */
    public void validate(Map<Integer,Map<Integer, String[]>> map) throws ImportException {
        informationPushValidator.validate(map);
    }



    public void initInfoTypeParam(int info_type){
        this.info_type=info_type;
    }
    public void initValidator(){
        informationPushValidator  = (InformationPushValidator)SpringUtil.getBean("informationPushValidator");
        informationPushValidator.setInfo_type(info_type);
        informationPushValidator.setInformationPushImportMgr(this);
    }
}
