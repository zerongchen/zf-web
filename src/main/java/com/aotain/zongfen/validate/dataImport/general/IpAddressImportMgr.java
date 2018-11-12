package com.aotain.zongfen.validate.dataImport.general;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.aotain.zongfen.dto.common.MergedExcelObject;
import com.aotain.zongfen.utils.IPUtil;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.model.dataimport.ImportResultList;
import com.aotain.zongfen.model.general.GenIPAddress;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.dataimport.ImportConstant;
import com.aotain.zongfen.validate.ImportMgr;

@Component("ipAddressImportMgr")
@Scope("prototype")
public class IpAddressImportMgr extends ImportMgr{

	private static final Logger LOG = LoggerFactory.getLogger(IpAddressImportMgr.class);

	private String errorMsg;

	private StringBuilder resultMsg = new StringBuilder();

	public static volatile Double process=0.0;

	public StringBuilder getResultMsg() {
		return resultMsg;
	}

	public void addResultMsg(String sb){
		resultMsg.append(sb).append("\n");
	}
	public void clearResultMsg(){
		resultMsg = new StringBuilder();
	}
	@Override
	public boolean checkCellNum(int sheetNum, int cells) {
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
	

	/**
	 * 
	* @Title: validateIpV4 
	* @Description: 验证IPv4(这里用一句话描述这个方法的作用) 
	* @param @param wb
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	private boolean validateIpV4(Workbook wb) {
		// IPv4
		Row row4 = wb.getSheet("地址-IPv4").getRow(0);
		if (row4.getPhysicalNumberOfCells() < 5) {
			setErrorMsg("模板错误，导入失败！");
			LOG.error("模板错误，导入失败！ current PhysicalNumberOfCells="+row4.getPhysicalNumberOfCells()+",require 5");
			return false;
		}
		String[] hedaDatas4 = new String[row4.getPhysicalNumberOfCells()];
		for (Cell cell : row4) {
			ExcelUtil.formateCellValue(cell, hedaDatas4);
		}
		if (!"省份".equals(hedaDatas4[0].trim()) || !"区域名称".equals(hedaDatas4[1].trim()) || !"区域ID".equals(hedaDatas4[2].trim())
				|| !"起始地址".equals(hedaDatas4[3].trim()) || !"终止地址".equals(hedaDatas4[4].trim())) {
			setErrorMsg("地址-IPv4的模板表头名称不对，导入失败！");
			LOG.error("地址-IPv6的模板表头名称不对，导入失败！ current data is:"+hedaDatas4[0].trim()+"|"+hedaDatas4[1].trim()+"|"+hedaDatas4[2].trim()+"|"+hedaDatas4[3].trim()+"|"+hedaDatas4[4].trim());
			LOG.error("地址-IPv6的模板表头名称不对，导入失败！ require data is:省份|区域名称|区域ID|起始地址|终止地址");
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: validateIpV6 
	* @Description: 验证IPv6(这里用一句话描述这个方法的作用) 
	* @param @param wb
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	private boolean validateIpV6(Workbook wb) {
		// IPv4
		Row row6 = wb.getSheet("地址-IPv6").getRow(0);
		if (row6.getPhysicalNumberOfCells() < 5) {
			setErrorMsg("模板错误，导入失败！");
			LOG.error("模板错误，导入失败！ current PhysicalNumberOfCells="+row6.getPhysicalNumberOfCells()+",require 5");
			return false;
		}
		String[] hedaDatas6 = new String[row6.getPhysicalNumberOfCells()];
		for (Cell cell : row6) {
			ExcelUtil.formateCellValue(cell, hedaDatas6);
		}
		if (!"省份".equals(hedaDatas6[0].trim()) || !"城域网".equals(hedaDatas6[1].trim()) || !"城域网ID".equals(hedaDatas6[2].trim())
				|| !"起始地址".equals(hedaDatas6[3].trim()) || !"终止地址".equals(hedaDatas6[4].trim())) {
			setErrorMsg("地址-IPv6的模板表头名称不对，导入失败！");
			LOG.error("地址-IPv6的模板表头名称不对，导入失败！ current data is:"+hedaDatas6[0].trim()+"|"+hedaDatas6[1].trim()+"|"+hedaDatas6[2].trim()+"|"+hedaDatas6[3].trim()+"|"+hedaDatas6[4].trim());
			LOG.error("地址-IPv6的模板表头名称不对，导入失败！ require data is:省份|城域网|城域网ID|起始地址|终止地址");
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: readDataFromStreams 
	* @Description: 存取数据，这个方法很关键(这里用一句话描述这个方法的作用) 
	* @param @param fileName
	* @param @param input
	* @param @return
	* @param @throws ImportException    设定文件 
	* @return List<GenIPAddress>    返回类型 
	* @throws
	 */
	public Map<String,List<GenIPAddress>> readDataFromStreams(String fileName,InputStream input) throws Exception{
		Map<String,List<GenIPAddress>> lists = new HashMap<>();
        	Workbook wb = ExcelUtil.getWorkBook(input);
        	List<GenIPAddress> sheetDataMap4 = new ArrayList<GenIPAddress>(); //每个sheet对应数据集
        	List<GenIPAddress> sheetDataMap6 = new ArrayList<GenIPAddress>(); //每个sheet对应数据集
            if(!checkSheetNum(wb)){ // 检查sheet的个数
            	buildErrorResult(fileName,fileName +":"+ getSheetErrorMsg());
            	LOG.error("请检查sheet的个数");
				throw new RuntimeException("Please check the number of sheet");
            }
            Date updateTime = new Date();
            //验证sheet对应名字是否正确
            if(wb.getSheet("地址-IPv4")==null && wb.getSheet("地址-IPv6")==null) {
            	setErrorMsg("模板sheet名称错误，导入失败！");
				LOG.error("模板sheet名称错误,required sheet name is 地址-IPv4|地址-IPv6,导入失败！");
				throw new RuntimeException("Template sheet name error, import failure");
            }
            //IPv4 和 IPv6的表头名称验证
           if(!validateIpV4(wb) || !validateIpV6(wb)) {
			   LOG.error("IPv4 和 IPv6的表头名称验证 没有通过,导入失败。");
			   throw new RuntimeException("The header name validation of IPv4 and IPv6 failed, and import failed");
           }
//			Map<Integer,Map<Integer, String[]>> sheetDataMap = new HashMap<>();
//			mapValue(wb,sheetDataMap);
           for(int i = 0; i < wb.getNumberOfSheets(); i++){
            	Sheet sheet = wb.getSheetAt(i);

			   List<MergedExcelObject> list= getMergedRegionValue(sheet);
			   Map<Integer, String[]> map = new HashMap<>();
			   if (list==null){
				   map= dealData(sheet,i);
			   }else {
				   map = dealData(sheet,list,i);
			   }
			   for (String[] cellDatas:map.values()){
	            	GenIPAddress ipAddress = new GenIPAddress(); //每行对应的数据集
	                if(cellDatas[0] == null) {//第一个为空的不加入结果集
	                	continue;
	                }
	                if(!StringUtils.isEmpty(cellDatas[0]) && !StringUtils.isEmpty(cellDatas[1]) && !StringUtils.isEmpty(cellDatas[2])
	                		&& !StringUtils.isEmpty(cellDatas[3]) && !StringUtils.isEmpty(cellDatas[4]) ) {
	                	ipAddress.setProvinceName(cellDatas[0]);
                    	ipAddress.setAreaName(cellDatas[1]);
                    	ipAddress.setAreaId(cellDatas[2]);
                    	ipAddress.setStartIp(cellDatas[3]);
                    	ipAddress.setEndIp(cellDatas[4]);
                    	ipAddress.setUpdateTime(updateTime);
                    	
                    	if(wb.getSheetAt(i).getSheetName().indexOf("IPv4")>0) {
                    		ipAddress.setIpType("0x04");
                    		sheetDataMap4.add(ipAddress);
                    	}else if(wb.getSheetAt(i).getSheetName().indexOf("IPv6")>0) {
                    		ipAddress.setIpType("0x06");
                    		sheetDataMap6.add(ipAddress);
                    	}
                    }
	            }  
            }
			lists.put("0x04",sheetDataMap4);
			lists.put("0x06",sheetDataMap6);
            input.close();
            return lists;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 *
	 * @param list
	 */
	public List<GenIPAddress> checkSelf(List<GenIPAddress> list) {
		process =0.0;
		int length=list.size();
		int count=0;

		List<GenIPAddress> retrList=new ArrayList<>();
		List<Integer>  existIndex = new ArrayList<>();
		boolean b=false;
		for(int i=0;i<length;i++){
			process=((i+1)/length)*0.6;
			LOG.debug("current process="+process);
			GenIPAddress m1 = list.get(i);
			b=false;
			if(existIndex.contains(i))continue;

			for(int j=(i+1);j<length;j++){
				if(b) continue;
				GenIPAddress  m2 = list.get(j);
				//ipType不一样直接跳过。
				if(!m1.getIpType().equals(m2.getIpType()))continue;

				try {
					Long m1startIp = IPUtil.StringToLong(m1.getStartIp());
					Long m1endIp = IPUtil.StringToLong(m1.getEndIp());
					if(m1startIp>m1endIp){
						addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
						b=true;
						continue;
					}
				} catch (Exception e) {
					addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",IP格式有错误");
					b=true;
					continue;
				}
				try {
					Long m2startIp = IPUtil.StringToLong(m2.getStartIp());
					Long m2endIp = IPUtil.StringToLong(m2.getEndIp());
					if(m2startIp>m2endIp){
						addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
						b=true;
						continue;
					}
				} catch (Exception e) {
					if(!existIndex.contains(j)){
						addResultMsg("第"+(j+2)+"行:"+m2.getErrorMsg()+",IP格式有错误");
					}
					existIndex.add((j));
					continue;
				}
				/**************************************************************************************************************/


				if("0X04".equals(m1.getIpType())){
					String msg = "地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP冲突";
					//2 城域网ID不同，起始IP、终止IP存在相同或者包含关系，需要生成核验异常文件


					Long sIp1 = IPUtil.StringToLong(m1.getStartIp());
					Long eIp1 = IPUtil.StringToLong(m1.getEndIp());
					Long sIp2 = IPUtil.StringToLong(m2.getStartIp());
					Long eIp2 = IPUtil.StringToLong(m2.getEndIp());
					boolean test = IPUtil.checkTwoIp(sIp1,eIp1,sIp2,eIp2);
					if(test){
						//两个记录区域id不一样
						if(!m1.getAreaId().equals(m2.getAreaId())){
								addResultMsg(msg);
								b=true;
								existIndex.add((j));
								continue;
							//两个记录区域id一样
						}else{
							if(sIp1.intValue()==sIp2.intValue()&&eIp1.intValue()==eIp2.intValue()){
								addResultMsg("地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP段存在重复");
								existIndex.add((j));
								continue;
							}
								Long startIp=sIp1<sIp2?sIp1:sIp2;
								Long endIp=eIp1>eIp2?eIp1:eIp2;
								m1.setStartIp(IPUtil.longToIP(startIp));
								m1.setEndIp(IPUtil.longToIP(endIp));
								addResultMsg("地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP存在包含，合并为:"+m1.getErrorMsg());
								existIndex.add((j));
								continue;
						}
					}

				}else if("0X06".equals(m1.getIpType())){
					String msg = "地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP冲突";
					boolean h=!(m2.getStartIp().compareTo(m1.getEndIp())>0||m1.getStartIp().compareTo(m2.getEndIp())>0);
					if(h){
						//两个记录区域id不一样
						if(!m1.getAreaId().equals(m2.getAreaId())){
							addResultMsg(msg);
							b=true;
							existIndex.add((j));
							continue;
							//两个记录区域id一样
						}else{
							if(m1.getStartIp().equals(m2.getStartIp())&&m1.getEndIp().equals(m2.getEndIp())){
								addResultMsg("地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP段存在重复");
								existIndex.add((j));
								continue;
							}
								String startIp=m1.getStartIp().compareTo(m2.getStartIp())<=0?m1.getStartIp():m2.getStartIp();
								String endIp=m1.getEndIp().compareTo(m2.getEndIp())>=0?m1.getEndIp():m2.getEndIp();
								m1.setStartIp(startIp);
								m1.setEndIp(endIp);
								addResultMsg("地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP存在包含，合并为:"+m1.getErrorMsg());
								existIndex.add((j));
								continue;
						}
					}
				}
			}
			if(!b){
				retrList.add(m1);
			}
		}

		return retrList;
	}

/**
 * 	/**
 *

public List<GenIPAddress> checkSelf2(Map<String,List<GenIPAddress>> map,String type) throws Exception{
	process =0.0;
	LOG.debug("process="+process);
	double hj = "0".equals(type)?1.0:0.6;
	List<GenIPAddress> retrList=new ArrayList<>();
	for(Map.Entry<String,List<GenIPAddress>> entry:map.entrySet()){
		String ipType = entry.getKey();
		List<GenIPAddress>  list = entry.getValue();
		int length=list.size();
		int count=0;

		//用于存放已经检验失败的记录索引。
		List<Integer>  existIndex = new ArrayList<>();
		boolean b=false;
		for(int i=0;i<length;i++){
			process=((i+1.0)/length)*hj;
			LOG.debug("current process="+process);
			GenIPAddress m1 = list.get(i);
			b=false;
			if(existIndex.contains(i))continue;

			try {
				if("0X04".equals(m1.getIpType())){
					Long m1startIp = IPUtil.StringToLong(m1.getStartIp());
					Long m1endIp = IPUtil.StringToLong(m1.getEndIp());
					if(m1startIp>m1endIp){
						addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
						b=true;
						continue;
					}
				}else if("0X06".equals(m1.getIpType())){
					String startIpV6 = m1.getStartIp().toLowerCase();
					String endIpV6 = m1.getEndIp().toLowerCase();
					if(startIpV6.compareTo(endIpV6)>0){
						addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
						b=true;
						continue;
					}
				}
			} catch (Exception e) {
				addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",IP格式有错误");
				b=true;
				continue;
			}

			for(int j=(i+1);j<length;j++){
				if(b) continue;
				GenIPAddress  m2 = list.get(j);
				//ipType不一样直接跳过。
				if(!m1.getIpType().equals(m2.getIpType()))continue;

				try {
					if("0X04".equals(m2.getIpType())){
						Long m2startIp = IPUtil.StringToLong(m2.getStartIp());
						Long m2endIp = IPUtil.StringToLong(m2.getEndIp());
						if(m2startIp>m2endIp){
							addResultMsg("第"+(j+2)+"行:"+m2.getErrorMsg()+",startIp不小于endIp");
							b=true;
							continue;
						}
					}else if("0X06".equals(m2.getIpType())){
						String startIpV6 = m2.getStartIp().toLowerCase();
						String endIpV6 = m2.getEndIp().toLowerCase();
						if(startIpV6.compareTo(endIpV6)>0){
							addResultMsg("第"+(j+2)+"行:"+m2.getErrorMsg()+",startIp不小于endIp");
							b=true;
							continue;
						}
					}
				} catch (Exception e) {
					if(!existIndex.contains(j)){
						addResultMsg("第"+(j+2)+"行:"+m2.getErrorMsg()+",IP格式有错误");
					}
					existIndex.add((j));
					continue;
				}

				if("0X04".equals(m1.getIpType())){
					String msg = "地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP冲突";
					//2 城域网ID不同，起始IP、终止IP存在相同或者包含关系，需要生成核验异常文件


					Long sIp1 = IPUtil.StringToLong(m1.getStartIp());
					Long eIp1 = IPUtil.StringToLong(m1.getEndIp());
					Long sIp2 = IPUtil.StringToLong(m2.getStartIp());
					Long eIp2 = IPUtil.StringToLong(m2.getEndIp());
					boolean test = IPUtil.checkTwoIp(sIp1,eIp1,sIp2,eIp2);
					if(test){
						//两个记录区域id不一样
						if(!m1.getAreaId().equals(m2.getAreaId())){
							addResultMsg(msg);
							b=true;
							existIndex.add((j));
							continue;
							//两个记录区域id一样
						}else{
							if(sIp1.intValue()==sIp2.intValue()&&eIp1.intValue()==eIp2.intValue()){
								addResultMsg("地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP段存在重复");
								existIndex.add((j));
								continue;
							}
							Long startIp=sIp1<sIp2?sIp1:sIp2;
							Long endIp=eIp1>eIp2?eIp1:eIp2;
							m1.setStartIp(IPUtil.longToIP(startIp));
							m1.setEndIp(IPUtil.longToIP(endIp));
							addResultMsg("地址-IPv4第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP存在包含，合并为:"+m1.getErrorMsg());
							existIndex.add((j));
							continue;
						}
					}

				}else if("0X06".equals(m1.getIpType())){
					String msg = "地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP冲突";
					boolean h=!(m2.getStartIp().compareTo(m1.getEndIp())>0||m1.getStartIp().compareTo(m2.getEndIp())>0);
					if(h){
						//两个记录区域id不一样
						if(!m1.getAreaId().equals(m2.getAreaId())){
							addResultMsg(msg);
							b=true;
							existIndex.add((j));
							continue;
							//两个记录区域id一样
						}else{
							if(m1.getStartIp().equals(m2.getStartIp())&&m1.getEndIp().equals(m2.getEndIp())){
								addResultMsg("地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP段存在重复");
								existIndex.add((j));
								continue;
							}
							String startIp=m1.getStartIp().compareTo(m2.getStartIp())<=0?m1.getStartIp():m2.getStartIp();
							String endIp=m1.getEndIp().compareTo(m2.getEndIp())>=0?m1.getEndIp():m2.getEndIp();
							m1.setStartIp(startIp);
							m1.setEndIp(endIp);
							addResultMsg("地址-IPv6第"+(i+2)+"行:"+m1.getErrorMsg()+" 与第"+(j+2)+"行:"+m2.getErrorMsg()+" IP存在包含，合并为:"+m1.getErrorMsg());
							existIndex.add((j));
							continue;
						}
					}
				}
			}
			if(!b){
				retrList.add(m1);
			}
		}
	}
	return retrList;
}
 */
	/**
	 *
	 */
	public List<GenIPAddress> checkSelf2(Map<String,List<GenIPAddress>> map,String type) throws Exception{
		process =0.0;
		LOG.debug("process="+process);
		double hj = "0".equals(type)?1.0:0.6;
		List<GenIPAddress> retrList=new ArrayList<>();
		for(Map.Entry<String,List<GenIPAddress>> entry:map.entrySet()){
			String ipType = entry.getKey();
			List<GenIPAddress>  list = entry.getValue();
			int length=list.size();
			int count=0;

			//用于存放已经检验失败的记录索引。
			List<Integer>  existIndex = new ArrayList<>();
			boolean b=false;
			for(int i=0;i<length;i++){
				process=((i+1.0)/length)*hj;
				LOG.debug("current process="+process);
				GenIPAddress m1 = list.get(i);
				b=false;
				if(existIndex.contains(i))continue;

				try {
					if("0x04".equals(m1.getIpType())){
						Long m1startIp = IPUtil.StringToLong(m1.getStartIp());
						Long m1endIp = IPUtil.StringToLong(m1.getEndIp());
						if(m1startIp>m1endIp){
							addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
							b=true;
							continue;
						}
					}else if("0x06".equals(m1.getIpType())){
						String startIpV6 = m1.getStartIp().toLowerCase();
						String endIpV6 = m1.getEndIp().toLowerCase();
						if(startIpV6.compareTo(endIpV6)>0){
							addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",startIp不小于endIp");
							b=true;
							continue;
						}
					}
				} catch (Exception e) {
					addResultMsg("第"+(i+2)+"行:"+m1.getErrorMsg()+",IP格式有错误");
					b=true;
					continue;
				}
				if(!b){
					retrList.add(m1);
				}
			}
        }
		return retrList;
	}

}
