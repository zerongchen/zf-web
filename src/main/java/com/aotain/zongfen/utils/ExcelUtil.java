package com.aotain.zongfen.utils;

import com.aotain.zongfen.annotation.ExpSheet;
import com.aotain.zongfen.annotation.Export;
import com.aotain.zongfen.comparator.FieldComparator;
import com.aotain.zongfen.dto.common.MergedExcelObject;
import com.aotain.zongfen.model.dataimport.DataErrorInfo;
import com.aotain.zongfen.model.dataimport.ImportResult;
import com.aotain.zongfen.utils.dataimport.ErrorType;
import com.aotain.zongfen.utils.dataimport.FileType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 	<p>Excel 工具类</p>
 *  @author chenzr
 *  @since 2018-01-09
 *  @version 2.0
 */
public class ExcelUtil {

	private static final Logger LOG= LoggerFactory.getLogger(ExcelUtil.class);

	public static List<String[]> readExcelStream(InputStream inp, int startRow) {
		try {
			List<String[]> list = new ArrayList<String[]>();
			Workbook wb = getWorkBook(inp);
			// System.out.println("sheet:"+wb.getNumberOfSheets());
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				for (Row row : sheet) {
					if (row.getRowNum() < startRow) {
						continue;
					}
					String[] str = new String[row.getLastCellNum()];
					for (Cell cell : row) {
						formateCellValue(cell, str);
					}
					list.add(str);
				}
			}
			inp.close();
			return list;
		} catch (FileNotFoundException e) {
			LOG.error("readExcelStream error",e);
			return null;
		} catch (IOException e) {
			LOG.error("readExcelStream error",e);
			return null;
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param input
	 *            输入流
	 * @return 数据集
	 */
	public static Map<Integer, Map<Integer, String[]>> readExcelFromStream(InputStream input) {
		try {
			// long s1 = System.nanoTime();
			Workbook wb = getWorkBook(input);
			Map<Integer, Map<Integer, String[]>> sheetDataMap = new HashMap<Integer, Map<Integer, String[]>>(); // 每个sheet对应数据集

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				int totalRow = sheet.getPhysicalNumberOfRows();
				if (totalRow <= 1) {
					continue;
				}
				Map<Integer, String[]> rowDataMap = new HashMap<Integer, String[]>(); // 每行对应的数据集
				int cells = 0;
				for (Row row : sheet) {
					int rowNum = row.getRowNum();
					// 跳过第0行(表头)
					if (rowNum == 0) {
						cells = row.getPhysicalNumberOfCells();
						continue;
					}
					String[] cellDatas = new String[cells];
					int j = 0;
					for (Cell cell : row) {
						// 如果数据行的单元格数大于表头的单元格数则舍弃
						if (j >= cells) {
							break;
						}
						formateCellValue(cell, cellDatas);
						j++;
					}
					rowDataMap.put(rowNum, cellDatas);
				}
				sheetDataMap.put(i, rowDataMap);
			}
			input.close();
			return sheetDataMap;
		} catch (FileNotFoundException e) {
			LOG.error("",e);
			return null;
		} catch (IOException e) {
			LOG.error("",e);
			return null;
		}
	}

	/**
	 * create workbook obj
	 * 
	 * @param input
	 * @return
	 */
	public static Workbook getWorkBook(InputStream input) {
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(input);
		} catch (IOException e) {
			LOG.error("",e);
		} catch (InvalidFormatException e) {
			LOG.error("",e);
		}
		return wb;
	}

	/**
	 * 读取文件
	 * 
	 * @param input
	 *            输入流
	 * @return 每个sheet对应所有行第零个单元格的值
	 */
	public static Map<Integer, Map<Integer, String>> readExcel(InputStream input) {
		try {
			Workbook wb = getWorkBook(input);
			Map<Integer, Map<Integer, String>> sheetDataMap = new HashMap<Integer, Map<Integer, String>>(); // 每个sheet对应数据集
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				int totalRow = sheet.getPhysicalNumberOfRows();
				if (totalRow <= 1) {
					continue;
				}
				Map<Integer, String> rowDataMap = new HashMap<Integer, String>(); // 每行第一个单元格的值
				for (Row row : sheet) {
					int rowNum = row.getRowNum();
					// 跳过第0行(表头)
					if (rowNum == 0) {
						continue;
					}
					String firstCellValue = "";
					int j = 0;
					for (Cell cell : row) {
						// int colNum = cell.getColumnIndex();
						// 如果数据行的单元格数大于表头的单元格数则舍弃
						if (j >= 1) {
							break;
						}
						formateCellValue(cell, firstCellValue);
						j++;
					}
					rowDataMap.put(rowNum, firstCellValue);
				}
				sheetDataMap.put(i, rowDataMap);
			}
			input.close();
			return sheetDataMap;
		} catch (FileNotFoundException e) {
			LOG.error("",e);
			return null;
		} catch (IOException e) {
			LOG.error("",e);
			return null;
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param input
	 *            输入流
	 * @return 每个sheet对应所有行第零个单元格的值
	 */
	public static Map<Integer, Map<Integer, String>> readExcelUnity(InputStream input, int houseinfocnum, int framecnum,
			int gatewaycnum, int ipsegcnum) {
		try {
			// long s1 = System.nanoTime();
			Workbook wb = getWorkBook(input);
			Map<Integer, Map<Integer, String>> sheetDataMap = new HashMap<Integer, Map<Integer, String>>(); // 每个sheet对应数据集
			int sheetnum = 0;
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				int totalRow = sheet.getPhysicalNumberOfRows();

				Map<Integer, String> rowDataMap = new HashMap<Integer, String>(); // 每行第一个单元格的值
				if (totalRow <= 1) {
					sheetDataMap.put(i, rowDataMap);
					sheetnum++;
					continue;
				}
				for (Row row : sheet) {

					int rowNum = row.getRowNum();
					// 跳过第0行(表头)
					if (rowNum == 0) {
						continue;
					}
					String firstCellValue = "";
					int j = 0;
					for (Cell cell : row) {
						// int colNum = cell.getColumnIndex();
						// 如果数据行的单元格数大于表头的单元格数则舍弃
						if (sheetnum == 0) {
							if (j > houseinfocnum) {
								break;
							}
						} else if (sheetnum == 1) {
							if (j > framecnum) {
								break;
							}
						} else if (sheetnum == 2) {
							if (j > gatewaycnum) {
								break;
							}
						} else if (sheetnum == 3) {
							if (j > ipsegcnum) {
								break;
							}
						} else
							break;

						/*
						 * if(j >= 1){ break; }
						 */
						if ((sheetnum == 0 && j == houseinfocnum) || (sheetnum == 1 && j == framecnum)
								|| (sheetnum == 2 && j == gatewaycnum) || (sheetnum == 3 && j == ipsegcnum)) {
							formateCellValue(cell, firstCellValue);
							j++;
							rowDataMap.put(rowNum, firstCellValue);
						} else {
							j++;
						}
					}
				}
				sheetDataMap.put(i, rowDataMap);
				sheetnum++;
			}
			input.close();
			return sheetDataMap;
			// long s2 = System.nanoTime();
			// System.out.println(" takes time:" + (s2-s1));
		} catch (FileNotFoundException e) {
			// DamsLog.opLog.error(e);
			LOG.error("readExcelUnity error",e);
			return null;
		} catch (IOException e) {
			// DamsLog.opLog.error(e);
			LOG.error("readExcelUnity error",e);
			return null;
		}
	}

	/**
	 * 获取文件类型
	 * 
	 * @param input
	 *            输入流
	 * @return
	 */
	public static FileType getFileType(InputStream input) {
		FileType fileType = null;
		try {
			input.mark(1024);
			fileType = FileTypeValidateUtils.getType(input);
			input.reset();
		} catch (IOException e) {
			LOG.error("getFileType error",e);
			// DamsLog.opLog.error(e);
		}
		return fileType;
	}

	/**
	 * 获取输入流
	 * 
	 * @param request
	 *            请求对象
	 * @return
	 */
	public static InputStream getInputStream(HttpServletRequest request) {
		InputStream input = null;
		try {
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> mFiles = multipartHttpservletRequest.getFiles("importFile");
			input = new BufferedInputStream(mFiles.get(0).getInputStream());
		} catch (IOException e) {
			LOG.error("getInputStream error",e);
		}
		return input;
	}

	/**
	 * Get inputstream List from flies
	 * 
	 * @param request
	 * @param inputSteamName
	 * @return
	 */
	public static Map<String, InputStream> getInputStreamList(HttpServletRequest request, String inputSteamName) {
		InputStream is = null;
		// List<InputStream> lIs = new ArrayList<>();
		Map<String, InputStream> map = new HashMap<>();
		try {
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> mFiles = multipartHttpservletRequest.getFiles(inputSteamName);
			if (mFiles != null && mFiles.size() > 0) {
				for (MultipartFile file : mFiles) {
					LOG.info("process file:"+file.getOriginalFilename() + "--" + file.getName());
					is = new BufferedInputStream(file.getInputStream());
					map.put(file.getOriginalFilename(), is);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("getInputStreamList error ",e);
		}
		return map;
	}
	public static Map<String, InputStream> getInputStreamListBySaveFile(File saveFile, String originalFileName) {
		InputStream is = null;
		// List<InputStream> lIs = new ArrayList<>();
		Map<String, InputStream> map = new HashMap<>();

		try {
			is = new BufferedInputStream(new FileInputStream(saveFile));
			map.put(originalFileName, is);
			LOG.error("文件{"+saveFile+"}转换为文件流成功,map="+map);
		} catch (FileNotFoundException e) {
			LOG.error("文件{"+saveFile+"}转换为文件流失败",e);
		}
		return map;
	}
	/**
	 * 获取输入流
	 * 
	 * @param request
	 *            请求对象
	 * @param inputSteamName
	 *            前台的input的name
	 * @return
	 */
	public static InputStream getInputStream(HttpServletRequest request, String inputSteamName) {
		InputStream is = null;
		try {
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> mFiles = multipartHttpservletRequest.getFiles(inputSteamName);
			if (mFiles != null && mFiles.size() > 0) {
				is = new BufferedInputStream(mFiles.get(0).getInputStream());
			} else {
				return null;
			}
		} catch (Exception e) {
			// DamsLog.opLog.error(e);
			LOG.error("getInputStream error",e);
		}
		return is;
	}

	/**
	 * 获取输入流
	 * 
	 * @param request
	 *            请求对象
	 * @param inputSteamName
	 *            前台的input的name
	 * @return
	 */
	public static String getFileName(HttpServletRequest request, String inputSteamName) {
		String fileName = "";
		try {
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> mFiles = multipartHttpservletRequest.getFiles(inputSteamName);
			if (mFiles != null && mFiles.size() > 0) {
				fileName = mFiles.get(0).getOriginalFilename();
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("getFileName error ",e);
		}
		return fileName;
	}

	private static Map<String, Object> mapForAnnotition(Class<?> entityClass) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> fieldNames = new ArrayList<String>();
		List<String> titles = new ArrayList<String>();
		Field[] fields = entityClass.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			Export export = field.getAnnotation(Export.class);
			if (export != null) {
				list.add(field); // 过滤掉没有注解的字段
			}
		}
		Field[] secondFields = list.toArray(new Field[list.size()]);
		Arrays.sort(secondFields, new FieldComparator()); // 将secondFields中的元素以注解@Export中id属性排序
		for (Field field : secondFields) {
			Export export = field.getAnnotation(Export.class);
			fieldNames.add(field.getName());
			titles.add(export.title());
		}
		map.put("fieldNames", fieldNames);
		map.put("titles", titles);
		map.put("fields", secondFields);
		return map;
	}

	public static Map<String, Object> getObjectRejectMap(Object object, Field[] fields) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(object));
			} catch (Exception e) {
				Class clazz = object.getClass();
				Field[] superFields = clazz.getDeclaredFields();
				for (Field superField : superFields) {
					if (superField.getName().equals(field.getName())) {
						try {
							superField.setAccessible(true);
							map.put(superField.getName(), superField.get(object));
						} catch (Exception e2) {
							LOG.error("getObjectRejectMap error ",e);
						}
					}
				}
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static SXSSFWorkbook createExcel(List<?> list, Class<?> entityClass) {
		// long startTime = System.currentTimeMillis();
		Map<String, Object> map = mapForAnnotition(entityClass);
		List<String> titles = (List<String>) map.get("titles");
		List<String> fieldNames = (List<String>) map.get("fieldNames");
		Field[] fields = (Field[]) map.get("fields");
		SXSSFWorkbook book = new SXSSFWorkbook(100);
		book.setCompressTempFiles(true);
		SXSSFSheet sheet = book.createSheet();
		sheet.trackAllColumnsForAutoSizing();
		CellStyle contentCellStyle = ExcelUtil.createContentCellStyle(book);
		CellStyle dateStyle = ExcelUtil.createDateStyle(book);
		// 创建表头
		Row firstRow = sheet.createRow(0);
		for (int i = 0; i < titles.size(); i++) {
			Cell cell = firstRow.createCell(i);
			cell.setCellValue(titles.get(i));
			cell.setCellStyle(ExcelUtil.createHeadCellStyle(book));
			sheet.autoSizeColumn(i);
		}
		for (int j = 0; j < list.size(); j++) {
			Row row = sheet.createRow(j + 1);
			Map<String, Object> fieldValueMap = getObjectRejectMap(list.get(j), fields);
			for (int k = 0; k < titles.size(); k++) {
				CellStyle cellStyle = contentCellStyle;
				Cell cell = row.createCell(k);
				Object obj = fieldValueMap.get(fieldNames.get(k));
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue((Long) obj);
				} else if (obj instanceof Date) {
					cellStyle = dateStyle;
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Boolean) {
					cell.setCellValue((Boolean) obj);
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				} else if (obj instanceof Float) {
					cell.setCellValue((Float) obj);
				}
				cell.setCellStyle(cellStyle);
			}
		}
		
		return book;
	}

	public static void createExcel(List<?> list, Class<?> entityClass, String filePath, String fileName) {
		SXSSFWorkbook book = createExcel(list, entityClass);
		File file = new File(filePath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		String path = filePath + fileName;
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(path));
			book.write(bos);
			bos.flush();
			book.dispose();
		} catch (FileNotFoundException e) {
			LOG.error("createExcel error ",e);
			// DamsLog.opLog.error("filePath:" + path, e);
		} catch (IOException e) {
			// DamsLog.opLog.error(e);
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}

	/**
	 * 模板下载的工作表表头单元格样式
	 * 
	 * @param book
	 * @return
	 */
	public static CellStyle createRuleGroupHeadCellStyle(Workbook book) {
		CellStyle cellStyle = book.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中对齐
		HSSFFont font = (HSSFFont) book.createFont();
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		cellStyle.setFillBackgroundColor(HSSFColor.SEA_GREEN.index);
//		cellStyle.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
		
		cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());
		cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());
		
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	/**
	 * 模板下载的工作表表体设置文本格式
	 * 
	 * @param book
	 * @return
	 */
	public static CellStyle createRuleGroupTextCellStyle(Workbook book) {
		CellStyle cellStyle = book.createCellStyle();
		cellStyle.setDataFormat(book.createDataFormat().getFormat("@"));
		return cellStyle;
	}

	/**
	 * 创建工作表表头单元格样式
	 * 
	 * @param book
	 *            工作簿
	 * @return
	 */
	public static CellStyle createHeadCellStyle(Workbook book) {
		CellStyle cellStyle = book.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中对齐
		XSSFFont font = (XSSFFont) book.createFont();
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 创建工作表内容单元格样式
	 * 
	 * @param book
	 *            工作簿
	 * @return
	 */
	public static CellStyle createContentCellStyle(Workbook book) {
		DataFormat dataFormat = book.createDataFormat();
		CellStyle cellStyle = book.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("text"));
		cellStyle.setAlignment(HorizontalAlignment.LEFT); // 左对齐
		Font font = book.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 创建日期样式
	 * 
	 * @param book
	 *            工作簿
	 * @return
	 */
	public static CellStyle createDateStyle(Workbook book) {
		DataFormat dataFormat = book.createDataFormat();
		CellStyle cellStyle = book.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));
		cellStyle.setAlignment(HorizontalAlignment.LEFT); // 左对齐
		Font font = book.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 获取与obj值相同的对象
	 * 
	 * @param map
	 * @param index
	 *            单元格索引
	 * @param obj
	 * @return
	 */
	public static List<String> list(Map<Integer, Map<Integer, String[]>> map, int index, String obj) {
		Assert.notNull(obj, "The given obj must not be null!");
		List<String> list = new ArrayList<String>();
		Iterator<Map.Entry<Integer, Map<Integer, String[]>>> sheetIterator = map.entrySet().iterator();
		while (sheetIterator.hasNext()) {
			Map.Entry<Integer, Map<Integer, String[]>> sheetEntry = sheetIterator.next();
			Map<Integer, String[]> rowMap = sheetEntry.getValue();
			Iterator<Map.Entry<Integer, String[]>> rowIterator = rowMap.entrySet().iterator();
			while (rowIterator.hasNext()) {
				Map.Entry<Integer, String[]> rowEntry = rowIterator.next();
				String[] datas = rowEntry.getValue();
				String item = datas[index];
				if (obj.equals(item)) {
					list.add(item);
				}
			}
		}
		list.remove(obj);
		return list;
	}

	/**
	 *
	 * @param map
	 * @param index
	 * @param obj
	 * @return
	 */
	public static List<String> listUnity(Map<Integer, String[]> map, int index, String obj) {
		Assert.notNull(obj, "The given obj must not be null!");
		List<String> list = new ArrayList<String>();
		Map<Integer, String[]> rowMap = map;
		Iterator<Map.Entry<Integer, String[]>> rowIterator = rowMap.entrySet().iterator();
		while (rowIterator.hasNext()) {
			Map.Entry<Integer, String[]> rowEntry = rowIterator.next();
			String[] datas = rowEntry.getValue();
			String item = datas[index];
			if (obj.equals(item)) {
				list.add(item);
			}
		}
		list.remove(obj);
		return list;
	}

	/**
	 * 删除导入数据验证
	 * 
	 * @param map
	 * @return
	 */
	private static ImportResult validateForDel(Map<Integer, Map<Integer, String>> map) {
		ImportResult importResult = new ImportResult();
		Iterator<Map.Entry<Integer, Map<Integer, String>>> sheetIterator = map.entrySet().iterator();
		first: while (sheetIterator.hasNext()) {
			Map.Entry<Integer, Map<Integer, String>> sheetEntry = sheetIterator.next();
			int sheet = sheetEntry.getKey();
			Map<Integer, String> rowMap = sheetEntry.getValue();
			Iterator<Map.Entry<Integer, String>> rowIterator = rowMap.entrySet().iterator();
			while (rowIterator.hasNext()) {
				Map.Entry<Integer, String> rowEntry = rowIterator.next();
				int row = rowEntry.getKey();
				String jyzId = rowEntry.getValue();
				if (StringUtils.isBlank(jyzId)) {
					importResult.setDataErrorInfo(new DataErrorInfo(sheet, row, 0, ErrorType.NULL.getDescription()));
					break first;
				} else {
					boolean flag = DataValidateUtils.validate(DataValidateUtils.POSITIVE_NUMBER_REGEXP, jyzId);
					if (!flag) {
						importResult
								.setDataErrorInfo(new DataErrorInfo(sheet, row, 0, ErrorType.ILLEGAL.getDescription()));
						break first;
					}
				}
			}
		}
		return importResult;
	}

	/**
	 * 获取ID集
	 * 
	 * @param input
	 * @return
	 */
	public static Map<String, Object> getIDs(InputStream input) {
		Map<Integer, Map<Integer, String>> map = ExcelUtil.readExcel(input);
		ImportResult importResult = ExcelUtil.validateForDel(map);
		List<String> ids = new ArrayList<String>();
		if (importResult.getDataErrorInfo() == null) {
			Iterator<Map.Entry<Integer, Map<Integer, String>>> sheetIterator = map.entrySet().iterator();
			while (sheetIterator.hasNext()) {
				Map.Entry<Integer, Map<Integer, String>> sheetEntry = sheetIterator.next();
				Map<Integer, String> rowMap = sheetEntry.getValue();
				Iterator<Map.Entry<Integer, String>> rowIterator = rowMap.entrySet().iterator();
				while (rowIterator.hasNext()) {
					Map.Entry<Integer, String> rowEntry = rowIterator.next();
					ids.add(rowEntry.getValue());
				}
			}
		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("ids", ids);
		returnMap.put("importResult", importResult);
		return returnMap;
	}

	public static Map<String, Object> getIDsUnity(Map<Integer, String> map, int sheetnum) {

		ImportResult importResult = ExcelUtil.validateForDelUnity(map, sheetnum);
		List<String> ids = new ArrayList<String>();
		if (importResult.getDataErrorInfo() == null) {

			Map<Integer, String> rowMap = map;
			Iterator<Map.Entry<Integer, String>> rowIterator = rowMap.entrySet().iterator();
			while (rowIterator.hasNext()) {
				Map.Entry<Integer, String> rowEntry = rowIterator.next();
				ids.add(rowEntry.getValue());
			}
		}

		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("ids", ids);
		returnMap.put("importResult", importResult);
		return returnMap;
	}

	private static ImportResult validateForDelUnity(Map<Integer, String> map, int sheetnum) {
		ImportResult importResult = new ImportResult();

		Map<Integer, String> rowMap = map;
		Iterator<Map.Entry<Integer, String>> rowIterator = rowMap.entrySet().iterator();
		while (rowIterator.hasNext()) {
			Map.Entry<Integer, String> rowEntry = rowIterator.next();
			int row = rowEntry.getKey();
			String jyzId = rowEntry.getValue();
			if (StringUtils.isBlank(jyzId)) {
				importResult.setDataErrorInfo(new DataErrorInfo(sheetnum, row, 0, ErrorType.NULL.getDescription()));
				break;
			}
		}

		return importResult;
	}

	public static void createExcelWithTemplate(List<List<?>> dataList, List<Class<?>> classList, String filePath,
			String fileName) {
		InputStream is = null;
		BufferedOutputStream bos = null;
		try {
			is = new FileInputStream(filePath + File.separatorChar + fileName);
			SXSSFWorkbook book = new SXSSFWorkbook(new XSSFWorkbook(is), 100);
			book.setCompressTempFiles(true);
			Map<String, Object> retMap = mapForAnnotition(classList);
			List<String> sheetList = (List<String>) retMap.get("sheet");
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) retMap.get("fieldData");
			for (int i = 0; i < sheetList.size(); i++) {
				createExcelWithTemplate(book, i, sheetList.get(i), mapList.get(i), dataList.get(i));
			}
			bos = new BufferedOutputStream(new FileOutputStream(filePath + File.separatorChar + fileName));
			book.write(bos);
			bos.flush();
			book.dispose();
		} catch (Exception e) {
			LOG.error("createExcelWithTemplate error",e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bos);
		}
	}

	/**
	 * create EXCEL with temporary filePath
	 * 
	 * @param dataList
	 * @param classList
	 * @param fileName
	 */
	public static void createExcel(List<List<?>> dataList, List<Class<?>> classList, String filePath, String fileName) {

		SXSSFWorkbook book = new SXSSFWorkbook(100);
		book.setCompressTempFiles(true);
		Map<String, Object> retMap = mapForAnnotition(classList);
		List<String> sheetList = (List<String>) retMap.get("sheet");
		List<Map<String, Object>> mapList = (List<Map<String, Object>>) retMap.get("fieldData");

		for (int i = 0; i < sheetList.size(); i++) {
			createExcel(book, i, sheetList.get(i), mapList.get(i), dataList.get(i));
		}

		File file = new File(filePath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		String path = filePath + fileName;
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(path));
			book.write(bos);
			bos.flush();
			book.dispose();
		} catch (Exception e) {
			// DamsLog.opLog.error(e);
			LOG.error("createExcel error",e);
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}

	/**
	 * create EXCEL without temporary filePath
	 * 
	 * @param dataList
	 * @param classList
	 * @param fileName
	 */
	public static void createExcel(List<List<?>> dataList, List<Class<?>> classList, OutputStream outputStream,
			String fileName) {

		SXSSFWorkbook book = new SXSSFWorkbook(100);
		book.setCompressTempFiles(true);
		Map<String, Object> retMap = mapForAnnotition(classList);
		List<String> sheetList = (List<String>) retMap.get("sheet");
		List<Map<String, Object>> mapList = (List<Map<String, Object>>) retMap.get("fieldData");

		for (int i = 0; i < sheetList.size(); i++) {
			createExcel(book, i, sheetList.get(i), mapList.get(i), dataList.get(i));
		}
		try {
			book.write(outputStream);
			outputStream.flush();
			book.dispose();
		} catch (Exception e) {
			LOG.error("createExcel error ",e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	private static void createExcelWithTemplate(SXSSFWorkbook book, int sheetNum, String sheetName,
			Map<String, Object> map, List<?> list) {
		List<String> titles = (List<String>) map.get("titles");
		List<String> fieldNames = (List<String>) map.get("fieldNames");
		Field[] fields = (Field[]) map.get("fields");
		Sheet sheet = book.getSheet(sheetName);
		// book.setSheetName(sheetNum, sheetName);
		CellStyle contentCellStyle = ExcelUtil.createContentCellStyle(book);
		CellStyle dateStyle = ExcelUtil.createDateStyle(book);
		for (int j = 0; j < list.size(); j++) {
			Row row = sheet.createRow(j + 1);
			Map<String, Object> fieldValueMap = getObjectRejectMap(list.get(j), fields);
			for (int k = 0; k < titles.size(); k++) {
				CellStyle cellStyle = contentCellStyle;
				Cell cell = row.createCell(k);
				Object obj = fieldValueMap.get(fieldNames.get(k));
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue((Long) obj);
				} else if (obj instanceof Date) {
					cellStyle = dateStyle;
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Boolean) {
					cell.setCellValue((Boolean) obj);
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				} else if (obj instanceof Float) {
					cell.setCellValue((Float) obj);
				}
				cell.setCellStyle(cellStyle);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void createExcel(SXSSFWorkbook book, int sheetNum, String sheetName, Map<String, Object> map,
			List<?> list) {
		List<String> titles = (List<String>) map.get("titles");
		List<String> fieldNames = (List<String>) map.get("fieldNames");
		Field[] fields = (Field[]) map.get("fields");
		SXSSFSheet sheet = book.createSheet();
		sheet.trackAllColumnsForAutoSizing();
		book.setSheetName(sheetNum, sheetName);
		CellStyle contentCellStyle = ExcelUtil.createContentCellStyle(book);
		CellStyle dateStyle = ExcelUtil.createDateStyle(book);
		// 创建表头
		Row firstRow = sheet.createRow(0);
		for (int i = 0; i < titles.size(); i++) {
			Cell cell = firstRow.createCell(i);
			cell.setCellValue(titles.get(i));
			cell.setCellStyle(ExcelUtil.createHeadCellStyle(book));
			sheet.autoSizeColumn(i);
		}
		for (int j = 0; j < list.size(); j++) {
			Row row = sheet.createRow(j + 1);
			Map<String, Object> fieldValueMap = getObjectRejectMap(list.get(j), fields);
			for (int k = 0; k < titles.size(); k++) {
				CellStyle cellStyle = contentCellStyle;
				Cell cell = row.createCell(k);
				Object obj = fieldValueMap.get(fieldNames.get(k));
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue((Long) obj);
				} else if (obj instanceof Date) {
					cellStyle = dateStyle;
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Boolean) {
					cell.setCellValue((Boolean) obj);
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				} else if (obj instanceof Float) {
					cell.setCellValue((Float) obj);
				}
				cell.setCellStyle(cellStyle);
			}
		}
	}

	private static Map<String, Object> mapForAnnotition(List<Class<?>> classList) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		List<String> sheetList = new ArrayList<String>();
		for (Class<?> entityClass : classList) {
			// 获取class上的注解
			ExpSheet expSheet = entityClass.getAnnotation(ExpSheet.class);
			sheetList.add(expSheet.name());

			// 获取Field上的注解
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> fieldNames = new ArrayList<String>();
			List<String> titles = new ArrayList<String>();
			Field[] fields = entityClass.getDeclaredFields();
			List<Field> list = new ArrayList<Field>();
			for (Field field : fields) {
				Export export = field.getAnnotation(Export.class);
				if (export != null) {
					list.add(field); // 过滤掉没有注解的字段
				}
			}
			Field[] superFields = entityClass.getSuperclass().getDeclaredFields();
			for (Field superField : superFields) {
				Export export = superField.getAnnotation(Export.class);
				if (export != null) {
					list.add(superField); // 过滤掉没有注解的字段
				}
			}
			Field[] secondFields = list.toArray(new Field[list.size()]);
			Arrays.sort(secondFields, new FieldComparator()); // 将secondFields中的元素以注解@Export中id属性排序
			for (Field field : secondFields) {
				Export export = field.getAnnotation(Export.class);
				fieldNames.add(field.getName());
				titles.add(export.title());
			}
			map.put("fieldNames", fieldNames);
			map.put("titles", titles);
			map.put("fields", secondFields);

			mapList.add(map);
		}

		returnMap.put("sheet", sheetList);
		returnMap.put("fieldData", mapList);

		return returnMap;
	}

	/**
	 * formate cell value and construct array cellDatas
	 * 
	 * @param cell
	 * @param cellDatas
	 */
	public static void formateCellValue(Cell cell, String[] cellDatas) {
		if (cell != null) {
			int colNum = cell.getColumnIndex();
			switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					String dateStr = DateUtils.formatDateWT(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
					cellDatas[colNum] = dateStr;
				} else {
					BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					bd.setScale(0, BigDecimal.ROUND_HALF_UP);
					cellDatas[colNum] = StringUtils.trim(bd + "");
				}
				break;
			case STRING:
				cellDatas[colNum] = StringUtils.trim(cell.getStringCellValue()); // 全角的空格不能用trim
				break;
			default:
				break;
			}
		}

		/*
		 * if (cell!=null) { int colNum = cell.getColumnIndex(); switch
		 * (cell.getCellType()) { case Cell.CELL_TYPE_NUMERIC: if
		 * (HSSFDateUtil.isCellDateFormatted(cell)) { String dateStr =
		 * DateUtils.formatDateWT(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
		 * cellDatas[colNum] = dateStr; } else { BigDecimal bd = new
		 * BigDecimal(cell.getNumericCellValue()); bd.setScale(0,
		 * BigDecimal.ROUND_HALF_UP); cellDatas[colNum] = StringUtils.trim(bd + ""); }
		 * break; case Cell.CELL_TYPE_STRING: cellDatas[colNum] =
		 * StringUtils.trim(cell.getStringCellValue()); // 全角的空格不能用trim break; default:
		 * break; } }
		 */
	}

	/**
	 * formate cell value and construct array cellDatas
	 * 
	 * @param cell
	 * @param cellDatas
	 */
	public static void formateCellValue(Cell cell, String[] cellDatas, List<MergedExcelObject> list, int rowNum) {

		if (cell != null) {
			int colNum = cell.getColumnIndex();
			switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					String dataStr = String.valueOf(cell.getNumericCellValue());
					cellDatas[colNum] = formateMergedValue(list, rowNum, colNum, dataStr);
				} else {
					BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					bd.setScale(0, BigDecimal.ROUND_HALF_UP);
					String dataStr = StringUtils.trim(bd + "");
					cellDatas[colNum] = formateMergedValue(list, rowNum, colNum, dataStr);
				}
				break;
			case STRING:
				cellDatas[colNum] = formateMergedValue(list, rowNum, colNum,
						StringUtils.trim(cell.getStringCellValue())); // 全角的空格不能用trim
				break;
			default:
				cellDatas[colNum] = formateMergedValue(list, rowNum, colNum, "");
				break;
			}
		}
	}

	/**
	 * 单元格值为null的时候,若处于合并单元格之中,返回合并单元格的值
	 * 
	 * @param list
	 * @param rowNum
	 * @param colNum
	 * @param dataStr
	 * @return
	 */
	private static String formateMergedValue(List<MergedExcelObject> list, int rowNum, int colNum, String dataStr) {

		if (StringUtils.isEmpty(dataStr)) {
			for (MergedExcelObject obj : list) {
				if (rowNum >= obj.getFirstRow() && rowNum <= obj.getLastRow()) {
					if (colNum >= obj.getFirstColumn() && colNum <= obj.getLastColumn()) {
						dataStr = obj.getValue();
					}
				}
			}
		}
		return dataStr;
	}

	/**
	 * formate cell value and assignment value to cellData
	 * 
	 * @param cell
	 * @param cellData
	 */
	public static void formateCellValue(Cell cell, String cellData) {

		if (cell != null) {
			switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					String dateStr = DateUtils.formatDateDB(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
					cellData = dateStr;
				} else {
					BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					bd.setScale(0, BigDecimal.ROUND_HALF_UP);
					cellData = StringUtils.trim(bd + "");
				}
				break;
			case STRING:
				cellData = StringUtils
						.trim(cell.getStringCellValue().replaceAll("^[\\s　\\u00A0\r\n\t]*|[\\s　\\u00A0\r\n\t]*$", "")); // 全角的空格不能用trim
				break;
			default:
				break;
			}
		}
	}

}
