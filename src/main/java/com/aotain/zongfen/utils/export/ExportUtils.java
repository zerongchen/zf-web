package com.aotain.zongfen.utils.export;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Repository;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.table.RtfCell;

/**
 * 导出工具类
 * @author yinzf
 * @createtime 2015年9月5日 下午10:48:25
 */
@Repository
public class ExportUtils<T> {

	/**
	 * 添加图片(pdf、word)
	 * @param baseModel
	 * @param document
	 * @return
	 */
	private void addImage(BaseModel<T> baseModel, Document document){
			try {
				List<String> dataURLs = baseModel.getDataURLs();
				if(CollectionUtils.isNotEmpty(dataURLs)){
					for(String dataURL : dataURLs){
						Image image = Image.getInstance(Base64Utils.decode(dataURL));
						image.scalePercent(50); //设定图片百分比
						document.add(image);
					}
				}else{
					document.add(new Chunk(""));
				}
			} catch (BadElementException e) {
				DamsLogUtils.otherLog.error(e);
			} catch (MalformedURLException e) {
				DamsLogUtils.otherLog.error(e);
			} catch (IOException e) {
				DamsLogUtils.otherLog.error(e);
			} catch (DocumentException e) {
				DamsLogUtils.otherLog.error(e);
			}
	}

	/**
	 * 添加图片
	 * @param baseModel
	 * @param book 工作薄
	 */
	private void addImageForExcel(BaseModel<T> baseModel, XSSFWorkbook book){
		List<String> dataURLs = baseModel.getDataURLs();
		if(CollectionUtils.isNotEmpty(dataURLs)){
			Sheet sheet = book.createSheet("报表");
			XSSFDrawing dr = (XSSFDrawing) sheet.createDrawingPatriarch();
			int row1 = 2, row2 = 25;
			for(int i = 0; i < dataURLs.size(); i++){
				XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 100, 50, 2, row1, 15, row2);
				dr.createPicture(anchor, book.addPicture(Base64Utils.decode(dataURLs.get(i)), SXSSFWorkbook.PICTURE_TYPE_PNG));
				row1 += row2;
				row2 += row2;
			}
		}
	}

	/**
	 * 添加列表
	 * @param baseModel
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void addTableForPDF(BaseModel<T> baseModel, Document document){
		PDFModel<T> pdfModel = (PDFModel<T>) baseModel;
		List<T> datas = pdfModel.getDatas();
        if(CollectionUtils.isNotEmpty(datas)){
        	String[] headers = pdfModel.getHeaders();
        	String[] fields = pdfModel.getFields();
        	BaseFont bfChinese = null;
        	Font fontChinese = null;
        	try {
        		bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        		fontChinese = new Font(bfChinese, 8, Font.NORMAL);
        	} catch (DocumentException e) {
        		DamsLogUtils.otherLog.error(e);
        	} catch (IOException e) {
        		DamsLogUtils.otherLog.error(e);
        	}
        	PdfPTable table = new PdfPTable(headers.length);
            table.setTotalWidth(520);
//             float[] colWidths = baseModel.getColWidths();
//             if(colWidths != null && colWidths.length > 0){
//     	        try {
//     				table.setTotalWidth(baseModel.getColWidths());
//     			} catch (DocumentException e1) {
//     				e1.printStackTrace();
//     			}
//             }
             table.setLockedWidth(true);
             //table.setHorizontalAlignment(Element.ALIGN_CENTER);
			 table.setHorizontalAlignment(Element.ALIGN_CENTER);
			 table.getDefaultCell().setBorder(1);
//			 PdfPCell headerCell = new PdfPCell();
//			 headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//			 headerCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//			 headerCell.setMinimumHeight(40);
        	for (String header : headers) {
//        		headerCell.setPhrase(new Paragraph(header, fontChinese));
				table.addCell(createCell(header,fontChinese,40));
        	}
//        	PdfPCell contentCell = new PdfPCell();
//        	contentCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
//        	contentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//        	contentCell.setMinimumHeight(10);
        	int dataSize = datas.size();
        	for(int i = 0; i < dataSize; i++){
        		T t = datas.get(i);
        		Class<T> tClass = (Class<T>) t.getClass();
        		for(String field : fields){
        			try {
        				String getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
        				Method getMethod = tClass.getMethod(getMethodName, new Class[]{});
        				Object value = getMethod.invoke(t, new Object[]{});
        				value = value == null ? "" : value;
//        				contentCell.setPhrase(new Paragraph(value + "", fontChinese));
        				table.addCell(createCell(value + "", fontChinese,10));
        			} catch (NoSuchMethodException e) {
        				DamsLogUtils.otherLog.error(e);
        			} catch (SecurityException e) {
        				DamsLogUtils.otherLog.error(e);
        			} catch (IllegalAccessException e) {
        				DamsLogUtils.otherLog.error(e);
        			} catch (IllegalArgumentException e) {
        				DamsLogUtils.otherLog.error(e);
        			} catch (InvocationTargetException e) {
        				DamsLogUtils.otherLog.error(e);
        			}
        		}
        		if((i + 1) % pdfModel.getWriteNum() == 0 || i == dataSize - 1){
        			try {
        				document.add(table);
        				table.deleteBodyRows();
        				//table.setSkipFirstHeader(true);
        			} catch (DocumentException e) {
        				DamsLogUtils.otherLog.error(e);
        			}
        		}
        	}
        }
	}

	/**
	 * createCell for PDF
	 * @param value
	 * @param font
	 * @param align
	 * @return
	 */
	public PdfPCell createCell(String value,com.lowagie.text.Font font,int align){
		PdfPCell cell = new PdfPCell();
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setMinimumHeight(align);
		cell.setPhrase(new Phrase(value,font));
		return cell;
	}


	/**
	 * 添加列表(Itext实现，只支持doc)
	 * @param baseModel
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void addTableForWord(BaseModel<T> baseModel, Document document){
		WordModel<T> wordModel = (WordModel<T>) baseModel;
		List<T> datas = wordModel.getDatas();
		if(CollectionUtils.isNotEmpty(datas)){ //添加表头
			String[] headers = wordModel.getHeaders();
			String[] fields = wordModel.getFields();
			BaseFont bfChinese = null;
			try {
				bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			} catch (DocumentException e) {
				DamsLogUtils.otherLog.error(e);
			} catch (IOException e) {
				DamsLogUtils.otherLog.error(e);
			}
			Table table = null;
			try {
				table = new Table(headers.length);
				table.setWidth(100); //占页面宽度比例
				table.setBorderWidth(1); //边框宽度
			} catch (BadElementException e) {
				DamsLogUtils.otherLog.error(e);
			}
			Font headerFontChinese = new Font(bfChinese, 12, Font.BOLD);
			for (String header : headers) {
				try {
					com.lowagie.text.Cell cell = new com.lowagie.text.Cell(new Paragraph(header, headerFontChinese));
					cell.setHorizontalAlignment(RtfCell.ALIGN_CENTER);
					cell.setVerticalAlignment(RtfCell.ALIGN_MIDDLE);
					table.addCell(cell);
				} catch (BadElementException e) {
					DamsLogUtils.otherLog.error(e);
				}
			}
			Font contFontChinese = new Font(bfChinese, 8, Font.NORMAL);
			int dataSize = datas.size();
			for(int i = 0; i < dataSize; i++){ //添加表内容
				T t = datas.get(i);
        		Class<T> tClass = (Class<T>) t.getClass();
				for(String field : fields){
					try {
						String getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
						Method getMethod = tClass.getMethod(getMethodName, new Class[]{});
						Object value = getMethod.invoke(t, new Object[]{});
						value = value == null ? "" : value;
						try {
							com.lowagie.text.Cell cell = new com.lowagie.text.Cell(new Paragraph(value + "", contFontChinese));
							cell.setHorizontalAlignment(RtfCell.ALIGN_LEFT);
							cell.setVerticalAlignment(RtfCell.ALIGN_MIDDLE);
							table.addCell(cell);
						} catch (BadElementException e) {
							DamsLogUtils.otherLog.error(e);
						}
					} catch (NoSuchMethodException e) {
						DamsLogUtils.otherLog.error(e);
					} catch (SecurityException e) {
						DamsLogUtils.otherLog.error(e);
					} catch (IllegalAccessException e) {
						DamsLogUtils.otherLog.error(e);
					} catch (IllegalArgumentException e) {
						DamsLogUtils.otherLog.error(e);
					} catch (InvocationTargetException e) {
						DamsLogUtils.otherLog.error(e);
					}
				}
				if((i + 1) % wordModel.getWriteNum() == 0 || i == dataSize - 1){
        			try {
        				document.add(table);
        				table.deleteAllRows();
        			} catch (DocumentException e) {
        				DamsLogUtils.otherLog.error(e);
        			}
        		}
			}
		}
	}


	/**
	 * 添加列表(POI实现，支持docx、doc)
	 * @param baseModel
	 * @param document
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void addTableForWord(BaseModel<T> baseModel, XWPFDocument document){
		WordModel<T> wordModel = (WordModel<T>)baseModel;
		List<T> datas = wordModel.getDatas();
		if(CollectionUtils.isNotEmpty(datas)){
			String[] headers = wordModel.getHeaders();
			String[] fields = wordModel.getFields();
			XWPFTable table = document.createTable(datas.size() + 1, headers.length);
			List<XWPFTableRow> rows = table.getRows();
			int rowCt = 0;
			for(XWPFTableRow row : rows){
//        	CTTrPr trPr = row.getCtRow().addNewTrPr();
//        	CTHeight ht = trPr.addNewTrHeight();
//        	if(rowCt == 0){
//        		ht.setVal(BigInteger.valueOf(1720)); // set row height; units = twentieth of a point, 360 = 0.25"
//        	}else{
//        		ht.setVal(BigInteger.valueOf(360));
//        	}
				T t = null;
				Class<T> tClass = null;
				if(rowCt > 0){
					t = datas.get(rowCt - 1);
					tClass = (Class<T>) t.getClass();
				}
				List<XWPFTableCell> cells = row.getTableCells();
				int colCt = 0;
				for(XWPFTableCell cell : cells){
					XWPFParagraph para = cell.getParagraphs().get(0);
					XWPFRun rh = para.createRun();
					if (rowCt == 0) { // header row
						rh.setFontFamily("宋体");
						rh.setBold(true);
						rh.setFontSize(12);
						rh.setText(headers[colCt]);
						para.setAlignment(ParagraphAlignment.CENTER);
						//para.setVerticalAlignment(TextAlignment.CENTER);
					} else { // content row
						try {
							String field = fields[colCt];
							String getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
							Method getMethod = tClass.getMethod(getMethodName, new Class[]{});
							Object value = getMethod.invoke(t, new Object[]{});
							value = value == null ? "" : value;
							rh.setFontFamily("宋体");
							rh.setFontSize(8);
							rh.setText(value + "");
							para.setAlignment(ParagraphAlignment.LEFT);
						} catch (NoSuchMethodException e) {
							DamsLogUtils.otherLog.error(e);
						} catch (SecurityException e) {
							DamsLogUtils.otherLog.error(e);
						} catch (IllegalAccessException e) {
							DamsLogUtils.otherLog.error(e);
						} catch (IllegalArgumentException e) {
							DamsLogUtils.otherLog.error(e);
						} catch (InvocationTargetException e) {
							DamsLogUtils.otherLog.error(e);
						}
					}
					colCt++;
				}
				rowCt++;
			}
		}
	}

	/**
	 * 添加列表
	 * @param baseModel
	 * @param book 工作薄
	 */
	@SuppressWarnings("unchecked")
	private void addTableForExcel(BaseModel<T> baseModel, XSSFWorkbook book){
		String[] headers = baseModel.getHeaders();
		String[] fields = baseModel.getFields();
		List<T> datas = baseModel.getDatas();
		int dataSize = datas.size();
		int sheetNum = dataSize % 1048575 == 0 ? dataSize / 1048575 : dataSize / 1048575 + 1;
		CellStyle headStyle = createHeadCellStyle(book);
		CellStyle contentStyle = createContentCellStyle(book);
		for(int i = 0; i < sheetNum; i++){
			Sheet sheet = book.createSheet("列表" + (i + 1));
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
			for(int j = 0; j < headers.length; j++){
				Cell cell = row.createCell(j);
				cell.setCellValue(headers[j]);
				cell.setCellStyle(headStyle);
				sheet.autoSizeColumn(j);
			}
		}
		int k = 1, j = 1;
		for(int i = 0; i < datas.size(); i++,j++){
			if(i == (1048575 * k + 1)){
				j = 1; //数据行从第一行开始
				k++; //第k个sheet
			}
			Sheet sheet = book.getSheetAt(k);
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(j);
			T t = datas.get(i);
			Class<T> tClass = (Class<T>) t.getClass();
			for(int m = 0; m < fields.length; m++){
				try {
					String field = fields[m];
					Cell cell = row.createCell(m);
					String getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
					Method getMethod = tClass.getMethod(getMethodName, new Class[]{});
					Object value = getMethod.invoke(t, new Object[]{});
					value = value == null ? "" : value;

					 if (value instanceof Double) {
						 cell.setCellValue(Double.valueOf(value.toString()));
					 }
					 if (value instanceof Integer) {
						 cell.setCellValue(Integer.valueOf(value.toString()));
					 }
					 if (value instanceof Long) {
						 cell.setCellValue(Long.valueOf(value.toString()));
					 }
					 if(value instanceof String){
						 cell.setCellValue((String)value);
						 cell.setCellStyle(contentStyle);
					 }
					//cell.setCellStyle(contentStyle);
				} catch (IllegalAccessException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (IllegalArgumentException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (InvocationTargetException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (NoSuchMethodException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (SecurityException e) {
					DamsLogUtils.otherLog.error(e);
				}
			}
		}
	}

	/**
	 * 添加列表
	 * @param baseModel
	 * @param book 工作薄
	 */
	@SuppressWarnings("unchecked")
	private void addExcelOfTable(BaseModel<T> baseModel, SXSSFWorkbook book){
		String[] headers = baseModel.getHeaders();
		String[] fields = baseModel.getFields();
		List<T> datas = baseModel.getDatas();
		int dataSize = datas.size();
		int sheetNum = dataSize % 1048575 == 0 ? dataSize / 1048575 : dataSize / 1048575 + 1;
		sheetNum = sheetNum == 0?1:sheetNum;
		CellStyle headStyle = createHeadCellStyle(book);
		CellStyle contentStyle = createContentCellStyle(book);
		for(int i = 0; i < sheetNum; i++){
			SXSSFSheet sheet = book.createSheet("列表" + (i + 1));
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
			for(int j = 0; j < headers.length; j++){
				Cell cell = row.createCell(j);
				cell.setCellValue(headers[j]);
				cell.setCellStyle(headStyle);
				sheet.trackAllColumnsForAutoSizing();
				sheet.autoSizeColumn(j);
			}
		}
		int k = 0, j = 1;
		for(int i = 0; i < datas.size(); i++,j++){
			if(i == (1048575 * (k + 1) + 1)){
				j = 1; //数据行从第一行开始
				k++; //第k个sheet
			}
			Sheet sheet = book.getSheetAt(k);
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(j);
			T t = datas.get(i);
			Class<T> tClass = (Class<T>) t.getClass();
			for(int m = 0; m < fields.length; m++){
				try {
					String field = fields[m];
					Cell cell = row.createCell(m);
					String getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
					Method getMethod = tClass.getMethod(getMethodName, new Class[]{});
					Object value = getMethod.invoke(t, new Object[]{});
					value = value == null ? "" : value;
					if (value instanceof Double) {
						 cell.setCellValue(Double.valueOf(value.toString()));
					 }
					 if (value instanceof Integer) {
						 cell.setCellValue(Integer.valueOf(value.toString()));
					 }
					 if (value instanceof Long) {
						 cell.setCellValue(Long.valueOf(value.toString()));
					 }
					 if(value instanceof String){
						 cell.setCellValue((String)value);
						 cell.setCellStyle(contentStyle);
					 }
				//	cell.setCellValue(value + "");
				//	cell.setCellStyle(contentStyle);

				} catch (IllegalAccessException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (IllegalArgumentException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (InvocationTargetException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (NoSuchMethodException e) {
					DamsLogUtils.otherLog.error(e);
				} catch (SecurityException e) {
					DamsLogUtils.otherLog.error(e);
				}
			}
		}
	}

	 /**
     * 创建工作表表头单元格样式
     * @param book 工作簿
     * @return
     */
    private CellStyle createHeadCellStyle(Workbook book){
    	CellStyle cellStyle = book.createCellStyle();
    	cellStyle.setAlignment(HorizontalAlignment.CENTER); //居中对齐
		org.apache.poi.ss.usermodel.Font font = book.createFont();
		font.setBold(true);
		font.setFontName("宋体");
	    font.setFontHeightInPoints((short)12);
	    cellStyle.setFont(font);
	    //cellStyle.setFillBackgroundColor(HSSFColor.ORANGE.index);
	    //cellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
	    return cellStyle;
    }

    /**
     * 创建工作表内容单元格样式
     * @param book 工作簿
     * @return
     */
    private CellStyle createContentCellStyle(Workbook book){
    	DataFormat dataFormat = book.createDataFormat();
    	CellStyle cellStyle = book.createCellStyle();
    	cellStyle.setDataFormat(dataFormat.getFormat("text"));
    	cellStyle.setAlignment(HorizontalAlignment.LEFT); //左对齐
    	org.apache.poi.ss.usermodel.Font font = book.createFont();
    	font.setFontName("宋体");
    	font.setFontHeightInPoints((short)10);
	    cellStyle.setFont(font);
    	return cellStyle;
    }

    public static CellStyle createCellStyle(Workbook workbook, short groundColor){
    	CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(groundColor);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
    }

	/**
	 * 导出PDF
	 * @param baseModel
	 */
	public void exportPDF(BaseModel<T> baseModel){
		PDFModel<T> pdfModel = (PDFModel<T>) baseModel;
		Document document = new Document(pdfModel.getPageSize());
		try {
			PdfWriter.getInstance(document, pdfModel.getOs());
			document.open();
			addImage(pdfModel, document); //添加图片
			addTableForPDF(pdfModel, document); //添加列表
		} catch (DocumentException e) {
			DamsLogUtils.otherLog.error(e);
		} finally{
			document.close();
		}
	}

	/**
	 * 导出WORD
	 * @param baseModel
	 */
	public void exportWord(BaseModel<T> baseModel){
		WordModel<T> wordModel = (WordModel<T>) baseModel;
		Document document = new Document(wordModel.getPageSize());
		RtfWriter2.getInstance(document, wordModel.getOs());
		document.open();
		addImage(wordModel, document); //添加图片
		addTableForWord(wordModel, document); //添加列表
		document.close();
//		BufferedOutputStream bos = null;
//		try {
//			WordModel<T> wordModel = (WordModel<T>) baseModel;
//			bos = new BufferedOutputStream(baseModel.getOs());
//			XWPFDocument document = new XWPFDocument();
//			addTableForWord(wordModel, document); //添加列表
//			document.write(bos);
//		} catch (IOException e) {
//			DamsLogUtils.otherLog.error(e);
//		} finally{
//			IOUtils.closeQuietly(bos);
//		}
	}

	/**
	 * 导出EXCEL
	 * @param baseModel
	 */
	public void exportExcel(BaseModel<T> baseModel){
		ExcelModel<T> excelModel = (ExcelModel<T>)baseModel;
		BufferedOutputStream bos = new BufferedOutputStream(baseModel.getOs());
		try {
			XSSFWorkbook book = new XSSFWorkbook(); // keep excelModel.getRowAccessWindowSize() rows in memory, exceeding rows will be flushed to disk
			addImageForExcel(excelModel, book);
			addTableForExcel(baseModel, book);
			book.write(bos);
			IOUtils.closeQuietly(bos);
			book.close(); // dispose of temporary files backing this workbook on disk
		} catch (IOException e) {
			DamsLogUtils.otherLog.error(e);
		}
	}

	/**
	 * 导出EXCEL
	 * @param baseModel
	 */
	public void exportForExcel(BaseModel<T> baseModel){
		ExcelModel<T> excelModel = (ExcelModel<T>)baseModel;
		BufferedOutputStream bos = new BufferedOutputStream(baseModel.getOs());
		try {
			SXSSFWorkbook book = new SXSSFWorkbook(excelModel.getRowAccessWindowSize()); // keep excelModel.getRowAccessWindowSize() rows in memory, exceeding rows will be flushed to disk
			book.setCompressTempFiles(true);
			addExcelOfTable(baseModel, book);
			book.write(bos);
			IOUtils.closeQuietly(bos);
			book.dispose(); // dispose of temporary files backing this workbook on disk
		} catch (IOException e) {
			DamsLogUtils.otherLog.error(e);
		}
	}

}
