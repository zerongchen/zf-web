package com.aotain.zongfen.service.common;

import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.WebParamUtils;
import com.aotain.zongfen.utils.export.BaseModel;
import com.aotain.zongfen.utils.export.ExcelModel;
import com.aotain.zongfen.utils.export.ExportType;
import com.aotain.zongfen.utils.export.ExportUtils;
import com.aotain.zongfen.utils.export.PDFModel;
import com.aotain.zongfen.utils.export.WordModel;

@Service
public class ExportServiceImpl<T> implements ExportService<T> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ExportUtils<T> exportUtils;
	
	@Override
	public String export(BaseModel<T> baseModel,HttpServletResponse response,HttpServletRequest request) {
		String fileName="";
		try {
			if(baseModel!=null) {
				//默认导出excel
				ExportType exportType = ExportType.valueOf(baseModel.getExportType());
				response.setContentType("application/octet-stream");

				if(baseModel.getFileName()==null) {
					fileName="export" + exportType.getSuffix();
					response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
				}else {
					fileName=baseModel.getFileName() + exportType.getSuffix();
					response.addHeader("Content-Disposition", "attachment; filename=" + getFileNameDisplay(fileName, request));
				}

				switch(exportType){
					case EXCEL:
						ExcelModel<T> excelModel = new ExcelModel<T>();
						excelModel.setDatas(baseModel.getDatas());
						excelModel.setFields(baseModel.getFields());
						excelModel.setHeaders(baseModel.getHeaders());
						
						excelModel.setOs(response.getOutputStream());
						excelModel.setDataURLs(baseModel.getDataURLs());
						//excelModel.setColWidths(baseModel.getColWidths());
						if(baseModel.getDataURLs()!=null && baseModel.getDataURLs().size()>0) {
							exportUtils.exportExcel(excelModel);
						}else {
							exportUtils.exportForExcel(excelModel);
						}
						break;
					case WORD:
						WordModel<T> wordModel = new WordModel<T>();
						wordModel.setDatas(baseModel.getDatas());
						wordModel.setFields(baseModel.getFields());
						wordModel.setHeaders(baseModel.getHeaders());
						wordModel.setDataURLs(baseModel.getDataURLs());
						wordModel.setFileName(baseModel.getFileName());
						wordModel.setOs(response.getOutputStream());
						//wordModel.setColWidths(baseModel.getColWidths());
						exportUtils.exportWord(wordModel);
						break;
					case PDF:
						PDFModel<T> pdfModel = new PDFModel<T>();
						pdfModel.setDatas(baseModel.getDatas());
						pdfModel.setFields(baseModel.getFields());
						pdfModel.setHeaders(baseModel.getHeaders());
						pdfModel.setDataURLs(baseModel.getDataURLs());
						pdfModel.setOs(response.getOutputStream());
						//pdfModel.setColWidths(baseModel.getColWidths());
						exportUtils.exportPDF(pdfModel);
						break;
					default:
						break;
				}
			}else {
				logger.error("export file empty");
			}
	} catch (Exception e) {
		logger.error("export fail-----"+e.getMessage());
	} finally {
			return fileName;
		}
	}

	private String getFileNameDisplay(String fileName, HttpServletRequest request) throws Exception {
		if ("FF".equals(WebParamUtils.getBrowser(request))) { // 针对火狐浏览器处理方式不一样了
			return new String(fileName.getBytes("UTF-8"),  "iso-8859-1");
		}
		return toUtf8String(fileName); // 解决汉字乱码
	}

	public String toUtf8String(String s){
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<s.length();i++){
			char c = s.charAt(i);
			if (c >= 0 && c <= 255){
				sb.append(c);
			}else{
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				}catch (Exception ex) {
					logger.error("change to utf-8 error ",ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) k += 2<<(8-1);
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}
}
