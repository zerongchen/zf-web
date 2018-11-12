package com.aotain.zongfen.validate.dataImport.general;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.aotain.common.policyapi.model.CommonCookie;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.ImportMgr;

@Component("cookieImportMgr")
@Scope("prototype")
public class CookieImportMgr extends ImportMgr{
	
	private static Logger logger = LoggerFactory.getLogger(CookieImportMgr.class);

	private String errorMsg;
	
	@Override
	public boolean checkCellNum(int sheetNum, int cells) {
		// TODO Auto-generated method stub
		return cells>0;
	}

	@Override
	public boolean checkSheetNum(Workbook wb) {
		
		return wb.getNumberOfSheets()>0;
	}

	@Override
	public boolean checkFileLimit(Workbook wb) {
		int rowNo = 0;
        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            rowNo += sheet.getPhysicalNumberOfRows()-1;
        }
		return rowNo>0;
	}

	@Override
	public String getSheetErrorMsg() {
		// TODO Auto-generated method stub
		return ImportConstant.EXCEL_SHEET_NO_ERROR;
	}

	@Override
	public String getFiletErrorMsg() {
		// TODO Auto-generated method stub
		return ImportConstant.EXCEL_ROWNUM_LIMIT;
	}

	@Override
	public int getCellCountBySheetNo(int sheetNo) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	//普通无校验导入
	public Map<String, List<CommonCookie>> readDataFromStreas(Map<String,InputStream> map) throws ImportException {
		boolean hasError = false;
		if(map!=null && map.size()>0){
			Map<String, List<CommonCookie>> fileMap = new HashMap<>();
			for(Map.Entry<String,InputStream> entry : map.entrySet()){
				List<CommonCookie> singleMap = readDataFromStreams(entry.getKey(),entry.getValue());
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
	
	public List<CommonCookie> readDataFromStreams(String fileName,InputStream input) throws ImportException{
        try {
        	Workbook wb = ExcelUtil.getWorkBook(input);
        	List<CommonCookie> sheetDataMap = new ArrayList<CommonCookie>(); //每个sheet对应数据集
            if(!checkSheetNum(wb)){ // 检查sheet的个数
            	buildErrorResult(fileName,fileName +":"+ getSheetErrorMsg());
            	return null;
            }
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
            	Sheet sheet = wb.getSheetAt(i);
        		int cells = 0; 
	            for(Row row : sheet){
	            	int rowNum = row.getRowNum();
	            	if(rowNum == 0){//验证表头对应名字是否正确
	            		cells = row.getPhysicalNumberOfCells();
	            		if(cells < 2) {
	            			setErrorMsg("模板错误，导入失败！");
	            			return null;
	            		}
	            		String[] hedaDatas = new String[cells];
	            		for(Cell cell : row){
		                    ExcelUtil.formateCellValue(cell,hedaDatas);
		                }
	            		if(!"网址".equals(hedaDatas[0]) || !"Key值".equals(hedaDatas[1])) {
		                    	setErrorMsg("模板错误，导入失败！");
		                    	return null;
			                }
	            		continue;
	            	}	       
	            	String[] cellDatas = new String[cells];
	            	int j = 0;
	                for(Cell cell : row){
	                	if(j >= cells){ //如果数据行的单元格数大于表头的单元格数则舍弃
	                		break;
	                	}
	                    ExcelUtil.formateCellValue(cell,cellDatas);
	                    if(cellDatas[0] == null) {//第一个为空的不加入结果集
		                	break;
		                }
	                	j++;
	                }
	                if(cellDatas[0] == null) {//第一个为空的不加入结果集
	                	continue;
	                }
	                if(!StringUtils.isEmpty(cellDatas[0]) && !StringUtils.isEmpty(cellDatas[1])) {
	                	CommonCookie cookie = new CommonCookie();
	                	cookie.setCookieHostName(cellDatas[0]);
	                	cookie.setCookieKeyValue(cellDatas[1]);
                    	sheetDataMap.add(cookie);
                    }
	            }  
            }
            input.close();
            return sheetDataMap;
        } catch (FileNotFoundException e) {  
        	logger.error("file error",e);
    		return null;
        } catch (IOException e) {
        	logger.error("file io error",e); 
    		return null;
        }
    
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
