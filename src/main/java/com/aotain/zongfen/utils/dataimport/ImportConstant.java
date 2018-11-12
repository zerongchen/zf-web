package com.aotain.zongfen.utils.dataimport;

public class ImportConstant {
	/**
	 * 增量导入方式
	 */
	public static final int ADD_IMPORT_TYPE = 0;
	/**
	 * 全量导入方式
	 */
	public static final int ADD_ALL_IMPORT_TYPE = 1;
	/**
	 * 更新导入方式
	 */
	public static final int UPDATE_IMPORT_TYPE = 2;
	/**
	 * 删除导入方式
	 */
	public static final int DELETE_IMPORT_TYPE = 3;
	/**
	 * 新增或更新导入方式
	 */
	public static final int ADDORUPDATE_IMPORT_TYPE = 4; 
	/**
	 * 导入成功
	 */
	public static final int DATA_IMPORT_SUCCESS = 0;

	/**
	 * 导入失败
	 */
	public static final int DATA_IMPORT_FAIL = 1;
	/**
	 * 导入失败,文件归属名重复
	 */
	public static final int DATA_IMPORT_FAIL_DUPLICATNAME = 2;
	/**
	 * 读取出错
	 */
	public static final String EXCEL_READ_ERROR = "导入的文件读取出错";
	/**
	 * 格式出错
	 */
	public static final String EXCEL_FORMAT_ERROR = "文件格式出错";
	/**
	 * 文件工作表个数错误
	 */
	public static final String EXCEL_SHEET_NO_ERROR = "文件工作表个数错误";
	/**
	 * 导入类型未知
	 */
	public static final String USER_TYPE_UNKNOWN = "未知的导入用户类型";

	/**
	 * 导入文件条数超限
	 */
	public static final String EXCEL_ROWNUM_LIMIT = "导入文件条数超过限制1000条,或者文件为空";

	/**
	 * 导入文件为空
	 */
	public static final String EXCEL_EMPTY_WARN = "空文件，请重新选择文件";

	/**
	 * EXCEL 行中有空格
	 */
	public static final String EXIT_EMPTY_CELL = "列数不符，存在空值";



}
