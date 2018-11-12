package com.aotain.zongfen.service.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.aotain.zongfen.cache.CommonCache;
import com.aotain.zongfen.mapper.general.ClassFileInfoMapper;
import com.aotain.zongfen.model.general.ClassFileInfo;
import com.aotain.zongfen.model.general.ClassFileInfoKey;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.WebParamUtils;

@Service
public class CommonServiceImpl implements CommonService{

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private ClassFileInfoMapper classFileInfoMapper;

    @Autowired
    private CommonCache commonCache;

    @Override
    public void exportTemplete(HttpServletRequest request,HttpServletResponse response,final String fileName) {

        InputStream input = null;
        OutputStream output = null;
        try {
            String filePath = System.getProperty("user.dir")+"/filetemplete/";
            File folder = new File(filePath);
            File[] templates = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (FilenameUtils.getBaseName(name).equals(fileName)) {
                        return true;
                    }
                    return false;
                }
            });
            File fileTemplete = templates[0];

            String fileNameDisplay = getFileNameDisplay(fileName, request) + "." + FilenameUtils.getExtension(fileTemplete.getAbsolutePath());
            response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
            input = new BufferedInputStream(new FileInputStream(fileTemplete));
            output = new BufferedOutputStream(response.getOutputStream());
            IOUtils.copy(input, output);
            output.flush();
        } catch (Exception e) {
            logger.error("exportTemplete error",e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public void exportTemplete(HttpServletRequest request,HttpServletResponse response,final String fileName,final String filePath) {

        InputStream input = null;
        OutputStream output = null;
        try {
            /*String filePath = System.getProperty("user.dir")+"/filetemplete/";*/
            File folder = new File(filePath);
            File[] templates = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (FilenameUtils.getBaseName(name).trim().equals(fileName)) {
                        return true;
                    }
                    return false;
                }
            });
            if(templates.length>0){
                File fileTemplete = templates[0];
                String fileNameDisplay = getFileNameDisplay(fileName, request) + "." + FilenameUtils.getExtension(fileTemplete.getAbsolutePath());
                response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
                input = new BufferedInputStream(new FileInputStream(fileTemplete));
                output = new BufferedOutputStream(response.getOutputStream());
                IOUtils.copy(input, output);
                output.flush();
            }else{
                logger.error("no file to download,fileName="+fileName);
                response.sendRedirect(request.getContextPath()+"/userbehavioranalysis/ddosirregular/index");
            }
        } catch (Exception e) {
            logger.error("exportTemplete error",e);
            try {
                response.sendRedirect(request.getContextPath()+"/userbehavioranalysis/ddosirregular/index");
            } catch (IOException e1) {
                logger.error("sendRedirect error",e);
            }
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public String exportData( List<List<?>> dataList, List<Class<?>> classList, String fileName, HttpServletResponse response, HttpServletRequest request) {
        try {
            OutputStream output = response.getOutputStream();
            fileName = fileName+ DateUtils.formatCurrDateyyyyMMddHHmmss() +".xlsx";
            String fileNameDisplay = getFileNameDisplay(fileName, request) ;
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
            ExcelUtil.createExcel(dataList, classList,output,fileNameDisplay);
            return fileName;
        } catch (Exception e) {
            logger.error("export "+fileName+" data "+e);
            return "";
        }

    }

    private String getFileNameDisplay(String fileName, HttpServletRequest request) throws Exception {
        if ("FF".equals(WebParamUtils.getBrowser(request))) { // 针对火狐浏览器处理方式不一样了
            return new String(fileName.getBytes("UTF-8"),  "iso-8859-1");
        }
        return toUtf8String(fileName); // 解决汉字乱码
    }

    @Override
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
                    logger.error("toUtf8String error",ex);
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
	@Override
	public void uploadFile(HttpServletRequest request, HttpServletResponse response,final String fileName) {

        InputStream input = null;
        OutputStream output = null;
        try {
            String filePath = System.getProperty("user.dir")+"/downLoad/";
            File folder = new File(filePath);
            File[] templates = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (FilenameUtils.getBaseName(name).equals(fileName)) {
                        return true;
                    }
                    return false;
                }
            });
            File fileTemplete = null;
            if(templates.length<=0) {
            	fileTemplete = new File(filePath+fileName+"-1.xlsx");
            }else {
            	fileTemplete = templates[0];
            }
            
            String fileNameDisplay = getFileNameDisplay(fileName, request) +DateUtils.formatDateyyyyMMdd(new Date())+"." + FilenameUtils.getExtension(fileTemplete.getAbsolutePath());
            response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
            input = new BufferedInputStream(new FileInputStream(fileTemplete));
            output = new BufferedOutputStream(response.getOutputStream());
            IOUtils.copy(input, output);
            output.flush();
        } catch (Exception e) {
            logger.error("uploadFile error ",e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

	@Override
	public void uploadFile(HttpServletRequest request, HttpServletResponse response,final String fileName,String realName) {

        InputStream input = null;
        OutputStream output = null;
        try {
            String filePath = System.getProperty("user.dir")+"/downLoad/";
            File folder = new File(filePath);
            File[] templates = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (FilenameUtils.getBaseName(name).equals(fileName)) {
                        return true;
                    }
                    return false;
                }
            });
            File fileTemplete = null;
            if(templates.length<=0) {
            	fileTemplete = new File(filePath+fileName+"-1.xlsx");
            }else {
            	fileTemplete = templates[0];
            }
            
            String fileNameDisplay = getFileNameDisplay(realName, request) +DateUtils.formatDateyyyyMMdd(new Date())+"." + FilenameUtils.getExtension(fileTemplete.getAbsolutePath());
            response.addHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
            input = new BufferedInputStream(new FileInputStream(fileTemplete));
            output = new BufferedOutputStream(response.getOutputStream());
            IOUtils.copy(input, output);
            output.flush();
        } catch (Exception e) {
            logger.error("uploadFile error",e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }
	
    @Override
    public String getFileNameByFilterAndRemove(String path, String str ) {


        String filePath = path;
        File folder = new File(filePath);
        final String filterString  = str;
        File[] templates = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (FilenameUtils.getBaseName(name).indexOf(filterString)>-1) {
                    return true;
                }
                return false;
            }
        });
        if(templates.length<1){
            return null;
        }
        File file = templates[0];
        file.delete();
        return file.getName();
    }

    @Override
    public String getFileNameByType( String path, Integer classType, final Integer file_type ) {

        String filePath = path;
        ClassFileInfo classFileInfo = new ClassFileInfo();
        
        classFileInfo.setMessageType(classType);
        classFileInfo.setFileType(file_type);
        ClassFileInfoKey classFileInfoKey = new ClassFileInfoKey();
        classFileInfoKey.setFileType(file_type);
        classFileInfoKey.setMessageType(classType);
        ClassFileInfo info = classFileInfoMapper.selectByPrimaryKey(classFileInfoKey);
        
        if(info==null){
            return null;
        }
        File folder = new File(filePath);
        final String fileName  = info.getClassFileName();
        File[] templates = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (fileName.substring(0,fileName.lastIndexOf(".")).equals(FilenameUtils.getBaseName(name))) {
                    return true;
                }
                return false;
            }
        });
        if(templates.length>0){
            File file = templates[0];
//            file.delete();
        }

        return fileName;
    }

	@Override
	public void downLoadFile(HttpServletRequest request, HttpServletResponse response,String path, String fileName) {
		InputStream input = null;
        OutputStream output = null;
        try {
            String filePath = System.getProperty("user.dir")+"/"+path+"/"+fileName;
            File fileTemplete = new File(filePath);
            String fileNameDisplay = getFileNameDisplay(fileName, request);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileNameDisplay);
            input = new BufferedInputStream(new FileInputStream(fileTemplete));
            output = new BufferedOutputStream(response.getOutputStream());
            IOUtils.copy(input, output);
            output.flush();
        } catch (Exception e) {
            logger.error("download error "+e);
            constructErrorMsg(input,output,response);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
	}
	
    @Override
    public String exportData( List<List<?>> dataList, List<Class<?>> classList, String fileName) {
    	String newFileName = "";
    	try {
        	String path = System.getProperty("user.dir")+"/downLoad/";
        	File directory = new File(path);
        	File[] files = directory.listFiles();
			for(File temFile:files) {
				String temName = temFile.getPath().substring(temFile.getPath().lastIndexOf(System.getProperty("file.separator")));
				if(temName.indexOf(fileName)>0) {
					temFile.delete();
				}
			}
			newFileName = fileName+DateUtils.formatDateyyyyMMdd(new Date())+".xlsx";
            ExcelUtil.createExcel(dataList, classList,path,newFileName);
        } catch (Exception e) {
           logger.error("exportData",e);
        }
        return newFileName;
    }

    /**
     * 根据UserId 获取用户组名
     * @param userGroupId
     * @return
     */
    @Override
    public String getUserGroupName(Long userGroupId){
        String userName = "";
        try {
            userName = commonCache.getUserGroupNameCache().get(userGroupId);
            if(StringUtils.isEmpty(userName)){
                commonCache.refreshCache();
                userName = commonCache.getUserGroupNameCache().get(userGroupId);
            }
        } catch (Exception e) {
            logger.error("while getting usergroup name error"+e);

        }
        return userName;
    }

    /**
     *  根据appType 取应用大类名称
     * @param appType
     * @return
     */
    @Override
    public String getAppTypeName(Integer appType){
        String name = "";
        try {
            name = commonCache.getAppTypeNameCache().get(appType);
            if(StringUtils.isEmpty(name)){
                commonCache.refreshAppCache();
                name = commonCache.getAppTypeNameCache().get(appType);
            }
        } catch (Exception e) {
            logger.error("while getting appType name error"+e);
        }
        return name;
    }

    private void constructErrorMsg(InputStream in,OutputStream out, HttpServletResponse response){
        try {
            String msg = "<a href=\"javascript:history.go(-1);\">该文件不存在,或者已经被移走了</a>";
            InputStream   in_msg   =   new   ByteArrayInputStream(msg.getBytes());
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            out = new BufferedOutputStream(response.getOutputStream());
            IOUtils.copy(in_msg, out);
        } catch (IOException e) {
           logger.error("write response "+e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

    }
    
    @Override
	public String saveFile(HttpServletRequest request,String fileName,String saveName) {
        MultipartFile file = ((MultipartRequest)request).getFile(fileName);
        try {
            saveName = saveName + String.valueOf(System.currentTimeMillis())+".xlsx";
            String filePath = System.getProperty("user.dir")+"/downLoad/"+ DateUtils.formatDateyyyyMMdd(new Date())+"/";
            File folder = new File(filePath);
            if(!folder.exists()) {
                folder.mkdir();
            }
            file.transferTo(new File(filePath+saveName));
            logger.info("导入,保存导入文件{"+file.getOriginalFilename()+"} 到 "+filePath+saveName+" success");
        } catch (Exception e) {
            logger.info("导入,保存导入文件{"+file.getOriginalFilename() +" failure,e="+e);
        }
        return saveName;
    }

    public String saveFile2(HttpServletRequest request,String fileName,String saveName) {
        MultipartFile file = ((MultipartRequest)request).getFile(fileName);
        try {
            saveName = saveName + String.valueOf(System.currentTimeMillis())+".xlsx";
            String filePath = System.getProperty("user.dir")+"/downLoad/"+ DateUtils.formatDateyyyyMMdd(new Date())+"/";
            File folder = new File(filePath);
            if(!folder.exists()) {
                folder.mkdir();
            }
            file.transferTo(new File(filePath+saveName));
            logger.info("导入,保存导入文件{"+file.getOriginalFilename()+"} 到 "+filePath+saveName+" success");
        } catch (Exception e) {
            logger.info("导入,保存导入文件{"+file.getOriginalFilename() +" failure,e="+e);
        }
        return saveName;
    }
}
