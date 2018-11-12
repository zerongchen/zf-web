package com.aotain.zongfen.validate;

import com.aotain.zongfen.dto.common.MergedExcelObject;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.dataimport.DataErrorInfo;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>处理Excel 文件</p>
 * @author chenzr
 * @since 2018-01-04
 * @version 2.0
 */
@Setter
@Getter
public abstract class ImportMgr {

	private static final Logger LOG = LoggerFactory.getLogger(ImportMgr.class);
	/**
	 * 校验的返回实体多文件
	 */
	protected Map<String,ImportResultList>  importResultMap= new HashMap<>();

	/**
	 * 校验的返回实体单文件
	 */
	protected ImportResultList importResultList = new ImportResultList();

	/**
	 * construct map for InputStream ,with file name ,couple file
	 * @param input
	 * @return
	 */
	private Map<Integer,Map<Integer, String[]>> readDataFromStream(String fileName,InputStream input) throws ImportException {
        try {
        	Workbook wb = ExcelUtil.getWorkBook(input);

            Map<Integer,Map<Integer, String[]>> sheetDataMap = new TreeMap<Integer,Map<Integer, String[]>>(); //每个sheet对应数据集

            if(!checkSheetNum(wb)){ // 检查sheet的个数
            	buildErrorResult(fileName,fileName +":"+ getSheetErrorMsg());
            	return null;
            }
			if(!checkFileLimit(wb)){ // 检查总行数
				buildErrorResult(fileName,fileName +":"+ getFiletErrorMsg());
				return null;
			}
			//组装数据
			mapValue(wb,sheetDataMap);

			if (sheetDataMap.isEmpty()){
				getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
				getImportResultList().setDescribtion(ImportConstant.EXCEL_EMPTY_WARN);
			}
			if(getImportResultList().hasError()){
				throw new ImportException(getImportResultList());
			}

            input.close();
            return sheetDataMap;
        } catch (FileNotFoundException e) {  
          	LOG.error("readDataFromStream , file no found",e);
    		return null;
        } catch (IOException e) {
			LOG.error("readDataFromStream , IO error",e);
    		return null;
        }
    }

	/**
	 * construct map for InputStream without file name ,single file
	 * @param input
	 * @return
	 */
	public Map<Integer,Map<Integer, String[]>> readDataFromStream(InputStream input) throws ImportException {
		try {
			Workbook wb = ExcelUtil.getWorkBook(input);

			Map<Integer,Map<Integer, String[]>> sheetDataMap = new TreeMap<Integer,Map<Integer, String[]>>(); //每个sheet对应数据集
			if(!checkSheetNum(wb)){ // 检查sheet的个数
				buildErrorResult( getSheetErrorMsg());
				return null;
			}
			if(!checkFileLimit(wb)){ // 检查总行数
				buildErrorResult(getFiletErrorMsg());
				return null;
			}

			//组装数据
			mapValue(wb,sheetDataMap);
			input.close();

			if (sheetDataMap.isEmpty()){
				getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
				getImportResultList().setDescribtion(ImportConstant.EXCEL_EMPTY_WARN);
			}
			if(getImportResultList().hasError()){
				LOG.error(" failure ");
				throw new ImportException(getImportResultList());
			}
			return sheetDataMap;
		} catch (FileNotFoundException e) {
			LOG.error("readDataFromStream , file no found ",e);
			return null;
		} catch (IOException e) {
			LOG.error("readDataFromStream , IO error ",e);
			return null;
		}
	}


	/**
	 * 组装Workbook 数据
	 * @param wb
	 * @param sheetDataMap
	 */
	protected void mapValue(Workbook wb,Map<Integer,Map<Integer, String[]>> sheetDataMap) throws ImportException {
		for(int i = 0; i < wb.getNumberOfSheets(); i++){
			Sheet sheet = wb.getSheetAt(i);
			List<MergedExcelObject> list= getMergedRegionValue(sheet);
			if (list==null){
				sheetDataMap.put(i, dealData(sheet,i));
			}else {
				sheetDataMap.put(i, dealData(sheet,list,i));
			}
		}
	}

	/**
	 *  处理存在合并单元格的Sheet数据
	 * @param sheet
	 * @param list
	 * @param sheetNum
	 * @return
	 */
	protected Map<Integer, String[]> dealData(Sheet sheet, List<MergedExcelObject> list, int sheetNum) throws ImportException {
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		int cells = 0;
		for(Row row : sheet){
			int rowNum = row.getRowNum();
			if(rowNum == 0){ //跳过第0行(表头)
				cells = row.getPhysicalNumberOfCells();
				if(!checkCellNum(sheetNum,cells)){ // 检查每个sheet中的cell的个数
					addError(new DataErrorInfo(sheetNum, -1, -1, getSheetErrorMsg(sheetNum)));
					break;
				}else{
					continue;
				}
			}
			String[] cellDatas = new String[cells];
			int j = 0;
			boolean flag = false;
			for(Cell cell : row){
				if(j >= cells){ //如果数据行的单元格数大于表头的单元格数则舍弃,抛出异常
//					buildErrorResult(sheetNum);
					break;
				}
				ExcelUtil.formateCellValue(cell,cellDatas,list,rowNum);
				j++;
			}

			map.put(rowNum, cellDatas);
		}

		return map;
	}

	/**
	 * 处理没有合并单元格的Sheet数据
	 * @param sheet
	 * @param sheetNum
	 * @return
	 */
	protected Map<Integer, String[]> dealData(Sheet sheet, int sheetNum) throws ImportException {
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		int cells = 0;
		for(Row row : sheet){
			int rowNum = row.getRowNum();
			if(rowNum == 0){ //跳过第0行(表头)
				cells = row.getPhysicalNumberOfCells();
				if(!checkCellNum(sheetNum,cells)){ // 检查每个sheet中的cell的个数
					addError("",new DataErrorInfo(sheetNum, -1, -1, getSheetErrorMsg(sheetNum)));
					break;
				}else{
					continue;
				}
			}
			String[] cellDatas = new String[cells];
			int j = 0;
			for(Cell cell : row){
				if(j >= cells){ //如果数据行的单元格数大于表头的单元格数则舍弃
//	                buildErrorResult(sheetNum);
					break;
				}
				ExcelUtil.formateCellValue(cell,cellDatas);
				j++;
			}

			map.put(rowNum, cellDatas);
		}
		return map;
	}

	/**
	 * construct map for InputStream Map , key is file name
	 * @param map
	 * @return
	 */
	public Map<String,Map<Integer,Map<Integer, String[]>>> readDataFromStream(Map<String,InputStream> map) throws ImportException {
		boolean hasError = false;
		if(map!=null && map.size()>0){
			Map<String,Map<Integer,Map<Integer, String[]>>> fileMap = new HashMap<>();
			for(Map.Entry<String,InputStream> entry : map.entrySet()){
				Map<Integer,Map<Integer, String[]>> singleMap = readDataFromStream(entry.getKey(),entry.getValue());
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

	/**
	 * 校验Excel表格的列数
	 * @param sheetNum
	 * @param cells
	 * @return
	 */
	public abstract boolean checkCellNum(int sheetNum, int cells);

	/**
	 * 校验Excel的Sheet数目
	 * @param wb
	 * @return
	 */
	public abstract boolean checkSheetNum(Workbook wb);
	/**
	 * 校验Excel总行数
	 * @param wb
	 * @return
	 */
	public abstract boolean checkFileLimit(Workbook wb);
	/**
	 * 获取默认的sheet格式错误信息
	 * @return
	 */
	public abstract String getSheetErrorMsg();

	/**
	 * 获取默认的文件限制信息
	 * @return
	 */
	public abstract String getFiletErrorMsg();
	/**
	 * 根据sheetNo获取正常的cells数量
	 * @return
	 */
	public abstract int getCellCountBySheetNo(int sheetNo); 
	/**
	 * 获取哪一个sheet的cells数量错误
	 * @param sheetNo
	 * @return
	 */
	public String getSheetErrorMsg(int sheetNo) { 

			return " 第" + (sheetNo + 1) + "个sheet的列数不对，跟模板不符合";

	}

	/**
	 * 适用于多文件
	 * @param fileName
	 * @param describtion
	 * @throws ImportException
	 */
	public void buildErrorResult(String fileName , String describtion) throws ImportException {
		getImportResultList(fileName).setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList(fileName).setFileName(fileName);
		getImportResultList(fileName).setDescribtion(describtion);
		throw new ImportException(Arrays.asList(getImportResultList(fileName)));
	}
	/**
	 * 适用于单文件
	 * @param describtion
	 * @throws ImportException
	 */
	public void buildErrorResult(String describtion) throws ImportException {
		getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList().setDescribtion(describtion);
		throw new ImportException(getImportResultList());
	}

	/**
	 *  适用于多文件
	 * @param fileName
	 * @param sheetNo
	 */
	public void buildErrorResult(String fileName,int sheetNo) throws ImportException {
		getImportResultList(fileName).setFileName(fileName);
		getImportResultList(fileName).setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList(fileName).setDescribtion(getSheetErrorMsg(sheetNo)); //
		throw new ImportException(getImportResultList());
	}
	/**
	 *  适用于单文件
	 * @param sheetNo
	 */
	public void buildErrorResult(int sheetNo) throws ImportException {
		getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList().setDescribtion(getSheetErrorMsg(sheetNo)); //
		throw new ImportException(getImportResultList());
	}

	/**
	 * 适用于多文件
	 * @param fileName
	 * @param dataErrorInfo
	 */
	public void addError(String fileName,DataErrorInfo dataErrorInfo){
		getImportResultList(fileName).setFileName(fileName);
		getImportResultList(fileName).setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList(fileName).getDataErrorInfoList().add(dataErrorInfo);
	}

	/**
	 * 适用于单文件
	 * @param dataErrorInfo
	 */
	public void addError(DataErrorInfo dataErrorInfo){
		getImportResultList().setResult(ImportConstant.DATA_IMPORT_FAIL);
		getImportResultList().getDataErrorInfoList().add(dataErrorInfo);
	}

	public synchronized ImportResultList getImportResultList( String key){

		ImportResultList importResult =null;
		if(importResultMap==null){
			importResultMap = new HashMap<>();
		}
		if(importResultMap.get(key)==null){
			importResult = new ImportResultList();
			importResultMap.put(key,importResult);
		}else{
			importResult = importResultMap.get(key);
		}
		return importResult;
	}

	/**
	 * 该表格是否存在合并单元格
	 * @param sheet
	 * @return
	 */
	private boolean hasMerged(Sheet sheet){
		return sheet.getNumMergedRegions() > 0;
	}

	/**
	 * 获取合并单元格的值
	 * @param sheet
	 * @return 返回值为null,该Sheet没有合并单元格
	 */
	protected List<MergedExcelObject> getMergedRegionValue(Sheet sheet){
		List<MergedExcelObject> list = null;
		int sheetMergeCount = sheet.getNumMergedRegions();
		if( sheetMergeCount > 0 ){
			list = new ArrayList<MergedExcelObject>();
			for(int i = 0 ; i < sheetMergeCount ; i++){
				CellRangeAddress ca = sheet.getMergedRegion(i);
				int firstColumn = ca.getFirstColumn();
				int lastColumn = ca.getLastColumn();
				int firstRow = ca.getFirstRow();
				int lastRow = ca.getLastRow();
				Row fRow = sheet.getRow(firstRow);
				Cell fCell = fRow.getCell(firstColumn);
				String value = getCellValue(fCell);
				list.add(new MergedExcelObject(firstRow,lastRow,firstColumn,lastColumn,value));
			}
		}
		return list;
	}


	/**
	 * 获取单元格的值
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell){

		if(cell == null) return "";

		switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					return  String.valueOf(cell.getNumericCellValue());
				} else {
					BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					bd.setScale(0, BigDecimal.ROUND_HALF_UP);
					return StringUtils.trim(bd + "");
				}
			case STRING:
				return cell.getStringCellValue();
			case FORMULA:
				return cell.getCellFormula() ;
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
		}
		return "";
	}
}
