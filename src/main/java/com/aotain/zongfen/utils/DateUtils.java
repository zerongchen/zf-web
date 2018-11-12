package com.aotain.zongfen.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.collect.Lists;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aotain.zongfen.utils.basicdata.DateFormatConstant;

/**
 * 时间处理的工具类
 *
 */
public class DateUtils {


	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	private static int oneHourInSecond = 60*60;

	private static int oneDayInSecond = 60*60*24;

	private static int oneWeekInSecond = 60*60*24*7;


	public static int WEEK_DAY_NUM = 7;

	/**
	 * Cut time on the Date object including hour, min, sec and msec and return
	 * a new object contains year, month and day only.
	 * 
	 * @param date
	 *            The date to be trimmed
	 * @param unit
	 *            see Calendar.HOUR_OF_DAY, MINUTE, SECOND, etc
	 * @return
	 */
	public static Date trimDate(Date date, int unit) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			return trimDate(cal, unit).getTime();
		} else {
			return null;
		}
	}

	public static java.sql.Date addDays(java.sql.Date date2, int unit) {
		Calendar c = Calendar.getInstance();
		c.setTime(date2);
		c.add(Calendar.DAY_OF_MONTH, unit);
		date2.setTime(c.getTimeInMillis());
		return date2;
	}

	public static Calendar trimDate(Calendar cal, int unit) {
		Calendar calendar = (Calendar) cal.clone();

		switch (unit) {
		case Calendar.YEAR:
			calendar.set(Calendar.MONTH, 0);

		case Calendar.MONTH:
			calendar.set(Calendar.DAY_OF_MONTH, 0);

		case Calendar.DAY_OF_MONTH:
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);

		case Calendar.HOUR_OF_DAY:
			calendar.set(Calendar.MINUTE, 0);

		case Calendar.MINUTE:
			calendar.set(Calendar.SECOND, 0);

		case Calendar.SECOND:
			calendar.set(Calendar.MILLISECOND, 0);

		case Calendar.MILLISECOND:

			// clear nothing
			break;
		}

		return calendar;
	}

	public static String formatCurrDateyyyyMMddHHmmssSSS() {
		return formatDateTime(DateFormatConstant.DATETIME_WITHOUT_SEPARATOR_LONG_WITH_MILLIS, new Date()); 
	}

	/**
	 * 将当前日期设置为yyyyMMddHHmmss格式
	 * @return
	 */
	public static String formatCurrDateyyyyMMddHHmmss() {
		return formatDateTime(DateFormatConstant.DATETIME_WITHOUT_SEPARATOR_LONG, new Date());
	}

	public static String formatCurrDateyyyyMMddHH() {
		return formatDateTime(DateFormatConstant.DATETIME_WITHOUT_SEPARATOR_HOUR, new Date());
	}

	public static String formatDateyyyyMMdd(Date date) {
		return formatDateTime(DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT, date);
	}

	public static String formatDateyyyyMM(Date date) {
		return formatDateTime(DateFormatConstant.DATE_WITHOUT_SEPARATOR_SHORT_WITHOUTdd, date);
	}

	public static String formatDateyyyyMMddHH(Date date) {
		return formatDateTime(DateFormatConstant.DATETIME_WITHOUT_SEPARATOR_HOUR, date);
	}

	public static String formatDateTime(Date date) {
		return formatDateTime(DateFormatConstant.DATETIME_CHT, date);
	}

	public static String formatDateLongTime(Date date) {
		return formatDateTime(DateFormatConstant.DATETIME_CHS_LONG, date);
	}

	public static String formatDateTime(String format, Date date) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String formatNetricsDate(String formate, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate, Locale.ENGLISH);
		return sdf.format(d);
	}

	/**
	 * Return a proper formatted date string conform to Netrics Date
	 * standard(US)
	 * 
	 * <p>
	 * Notes: 1. Netrics date format follows US Locales
	 * 
	 * @param d
	 *            - any date
	 * @return String of the date comform to Netrics Standard
	 */
	public static String formatDate(Date d) {
		if (d == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		return sdf.format(d);
	}

	public static String formatDateHK(Date date) {
		return formatDateTime(DateFormatConstant.DATE_CHT_SLASH, date);
	}

	public static String formatDateDB(Date date) {
		return formatDateTime(DateFormatConstant.DATE_CHS_HYPHEN, date);
	}
	public static String formatDateWT(Date date) {
		return formatDateTime(DateFormatConstant.DATETIME_CHS, date);
	}

	public static Calendar changeTimeZone(Calendar date, TimeZone timezone) {
		if (!date.getTimeZone().equals(timezone)) {
			Calendar newDate = Calendar.getInstance(timezone);

			newDate.setTimeInMillis(date.getTimeInMillis());

			return newDate;
		}

		return date;
	}

	/**
	 * used computer default timezone
	 */
	public static Calendar changeTimeZone(Calendar date) {
		return changeTimeZone(date, TimeZone.getDefault());
	}

	/**
	 * pass object to automatically change call the Calendar fields timezone and
	 * not trim the time
	 * 
	 * @param object
	 * @param timeZone
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void changeInstanceFieldsTimeZone(Object object,
			TimeZone timeZone) throws IntrospectionException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor pd : pds) {
			if (pd.getPropertyType().equals(Calendar.class)
					&& (pd.getReadMethod().getParameterTypes().length == 0)) {
				Method getter = pd.getReadMethod();
				Method setter = pd.getWriteMethod();
				Object[] args = null;
				Calendar orginalc = (Calendar) getter.invoke(object, args);

				if (orginalc != null) {
					setter.invoke(object, new Object[] { changeTimeZone(orginalc, timeZone) });
				}
			}
		}
	}

	public static void changeInstanceFieldsTimeZone(Object object)
			throws IllegalArgumentException, IntrospectionException,
			IllegalAccessException, InvocationTargetException {
		changeInstanceFieldsTimeZone(object, TimeZone.getDefault());
	}

	public static Date parse(String format, String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			logger.error(""+e);
			return null;
		}
	}

	public static Calendar toCalendar(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			return cal;
		}
	}

	public static Date addDateOfMonth(Date date, int days) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}

	public static java.sql.Date addDateOfMonthForSqlDate(java.sql.Date date,
			int days) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return new java.sql.Date(calendar.getTime().getTime());
	}

	public static String toString(Calendar cal) {
		if (cal == null) {
			return null;
		} else {
			return "{" + cal.getTime().toString() + ", locale="
					+ cal.getTimeZone().getDisplayName() + "}";
		}
	}

	public static String toString(ArrayList<Calendar> cals) {
		if (cals == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(cals.getClass().getSimpleName());
		sb.append("{");

		for (int i = 0; i < cals.size(); i++) {
			sb.append(toString(cals.get(i)));

			if (i < (cals.size() - 1)) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static int getDayOfMonth(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfWeek(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_WEEK);
	}

	public static String getYYYY(Date date) {
		return String.valueOf(toCalendar(date).get(Calendar.YEAR));
	}

	public static String getMM(Date date) {
		return StringUtils.leftPad(String.valueOf(toCalendar(date).get(
				Calendar.MONTH) + 1), 2, "0");
	}

	public static String getDD(Date date) {
		return StringUtils.leftPad(String.valueOf(toCalendar(date).get(
				Calendar.DAY_OF_MONTH)), 2, "0");
	}

	public static int getYear(Date date) {
		return toCalendar(date).get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		return toCalendar(date).get(Calendar.MONTH);
	}

	public static int getMonthChina(Date date) {
		return toCalendar(date).get(Calendar.MONTH) + 1;
	}

	public static int getMonthChina(java.sql.Date date) {
		return toCalendar(date).get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfYear(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_YEAR);
	}

	public static int DateMinusDate(java.sql.Date d1, java.sql.Date d2) {
		long l1 = d1 == null ? 0 : d1.getTime();
		long l2 = d2 == null ? 0 : d2.getTime();
		return (int) ((l1 - l2) / (24 * 60 * 60 * 1000));
	}

	public static String toString(Calendar[] cals) {
		if (cals == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(cals.getClass().getSimpleName());
		sb.append("{");

		for (int i = 0; i < cals.length; i++) {
			sb.append(toString(cals[i]));

			if (i < cals.length) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Date parseDate(String yyyy, String mm, String dd)
			throws ParseException, NumberFormatException {
		Date bdate = null;

		if (yyyy == null || yyyy.trim().length() == 0)
			return null;
		if (mm == null || mm.trim().length() == 0)
			return null;
		if (dd == null || dd.trim().length() == 0)
			return null;
		if (mm.length() == 1)
			mm = "0" + mm;
		if (dd.length() == 1)
			dd = "0" + dd;

		try {
			Integer.parseInt(yyyy);
			Integer.parseInt(mm);
			Integer.parseInt(dd);
			bdate = parseDate(yyyy + "-" + mm + "-" + dd, "yyyy-MM-dd");
		} catch (NumberFormatException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		}
		return bdate;
	}

	public static Date parseDate(String strdate, String format)
			throws ParseException {
		Date bdate = null;
		try {
			if (null == strdate || StringUtils.isBlank(strdate)) {
				return bdate;
			}
			SimpleDateFormat dFormat = new SimpleDateFormat(format);
			dFormat.setLenient(false);
			bdate = new Date(dFormat.parse(strdate).getTime());
			// Sys.log.printDebug("Convert StringToDate : " + bdate);
		} catch (ParseException e) {

			throw e;
		}
		return bdate;
	}

	public static String parseDate(Date date, String format) {
		SimpleDateFormat dFormat = new SimpleDateFormat(format);
		dFormat.setLenient(false);
		String dateStr = dFormat.format(date);
		return dateStr;
	}

	/**
	 * 时间戳转为日期
	 * @param datetamp
	 * @return
	 */
	public static String getDateTime(Timestamp datetamp) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(datetamp);
		String dateStr = dateFormater.format(cal.getTime());
		return dateStr;
	}

	/**
	 * 时间戳转为日期
	 * @param datetamp
	 * @return
	 */
	public static String getDateTime(Timestamp datetamp,String formate) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(formate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(datetamp);
		String dateStr = dateFormater.format(cal.getTime());
		return dateStr;
	}

	/**
	 * String 转为时间戳
	 * @param strdate
	 * @return
	 * @throws ParseException
	 */
	public static Timestamp parseTimesTampSql(String strdate){
		return Timestamp.valueOf(strdate);
	}

	/**
	 * 日期转长整形时间戳
	 * @param dateString
	 * @return
	 */
	public static Long parse2TimesTamp(String dateString){
		SimpleDateFormat df=new SimpleDateFormat(DateFormatConstant.DATETIME_CHS);
		Date dt1 ;
		Long ts1 = 0l;
		if(StringUtils.isEmpty(dateString)){
			return 0l;
		}
		try {
			dt1 = df.parse(dateString);
			ts1 = dt1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts1/1000;
	}
	/**
	 * 日期转长整形时间戳
	 * @param dateString
	 * @return
	 */
	public static Long parse2TimesTamp(String dateString,String formate){
		SimpleDateFormat df=new SimpleDateFormat(formate);
		Date dt1 ;
		Long ts1 = 0l;
		if(StringUtils.isEmpty(dateString)){
			return 0l;
		}
		try {
			dt1 = df.parse(dateString);
			ts1 = dt1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts1/1000;
	}

	/**
	 * 长整形时间戳转日期字符串
	 * @param timesTamp
	 * @return
	 */
	public static String parse2DateString(Long timesTamp){
		SimpleDateFormat df=new SimpleDateFormat(DateFormatConstant.DATETIME_CHS);
		Date date = new Date(timesTamp*1000);
		return df.format(date);
	}

	/**
	 * 长整形时间戳转日期字符串
	 * @param timesTamp
	 * @return
	 */
	public static String parse2DateString(Long timesTamp , String formate){
		if (timesTamp==0) return "";
		SimpleDateFormat df=new SimpleDateFormat(formate);
		Date date = new Date(timesTamp*1000);
		return df.format(date);
	}

	public static java.sql.Date parseDateSql(String strdate, String format)
			throws ParseException {
		java.sql.Date bdate = null;
		try {
			if (null == strdate) {
				return bdate;
			}
			SimpleDateFormat dFormat = new SimpleDateFormat(format);
			dFormat.setLenient(false);
			bdate = new java.sql.Date(dFormat.parse(strdate).getTime());
			// Sys.log.printDebug("Convert StringToDate : " + bdate);
		} catch (ParseException e) {

			throw e;
		}
		return bdate;
	}

	/**
	 * dateformat use dd/MM/yyyy
	 * 
	 * @param strdate
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date parseDateSql(String strdate)
			throws ParseException {
		return parseDateSql(strdate, DateFormatConstant.DATE_CHT_SLASH);
	}

	public static java.sql.Date parseAllDateSql(String strdate)
			throws ParseException {
		java.sql.Date date = null;
		try {
			date = parseDateSql(strdate, DateFormatConstant.DATE_CHT_SLASH);
		} catch (ParseException e) {
			try {
				date = parseDateSql(strdate, DateFormatConstant.DATE_CHS_HYPHEN);
			} catch (ParseException e1) {
				try {
					date = parseDateSql(strdate,
							DateFormatConstant.DATE_ENG_SLASH);
				} catch (ParseException e2) {
					return parseDateSql(strdate,
							DateFormatConstant.DATE_CHT_SLASH);
				}
			}
		}
		return date;
	}

	public static Date parseDate(String yyyy, String MM, String dd, String HH,
			String mm, String ss) throws ParseException {
		Date bdate = null;

		if (yyyy == null || yyyy.trim().length() == 0)
			return null;
		if (MM == null || MM.trim().length() == 0)
			return null;
		if (dd == null || dd.trim().length() == 0)
			return null;
		if (HH == null || HH.trim().length() == 0)
			return null;
		if (mm == null || mm.trim().length() == 0)
			return null;
		if (ss == null || ss.trim().length() == 0)
			return null;
		if (MM.length() == 1)
			MM = "0" + MM;
		if (dd.length() == 1)
			dd = "0" + dd;
		if (HH.length() == 1)
			HH = "0" + HH;
		if (mm.length() == 1)
			mm = "0" + mm;
		if (ss.length() == 1)
			ss = "0" + ss;

		try {
			bdate = parseDate(yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm
					+ ":" + ss, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			throw e;
		}
		return bdate;
	}

	public static Date parseDate(String yyyy, String MM, String dd, String HH,
			String mm) throws ParseException, NumberFormatException {
		Date bdate = null;

		if (yyyy == null || yyyy.trim().length() == 0)
			return null;
		if (MM == null || MM.trim().length() == 0)
			return null;
		if (dd == null || dd.trim().length() == 0)
			return null;
		if (HH == null || HH.trim().length() == 0)
			return null;
		if (mm == null || mm.trim().length() == 0)
			return null;
		if (MM.length() == 1)
			MM = "0" + MM;
		if (dd.length() == 1)
			dd = "0" + dd;
		if (HH.length() == 1)
			HH = "0" + HH;
		if (mm.length() == 1)
			mm = "0" + mm;

		try {
			Integer.parseInt(yyyy);
			Integer.parseInt(MM);
			Integer.parseInt(dd);
			Integer.parseInt(HH);
			Integer.parseInt(mm);
			bdate = parseDate(yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm,
					"yyyy-MM-dd HH:mm");
		} catch (NumberFormatException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		}
		return bdate;
	}

	// 计算两个任意时间中间的间隔天敄1�7�1�7
	public static int getIntervalDays(Date startday, Date endday) {
		if (startday.after(endday)) {
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}

	public static String formatUsDateTime(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

		return sdf.format(date);
	}

	public static boolean checkDateFormat(String dateStringToCheck,
			String format) throws ParseException {

		boolean isDateFormatCorrect;

		try {
			parseDate(dateStringToCheck, format);
			isDateFormatCorrect = true;
		} catch (ParseException e) {
			isDateFormatCorrect = false;
			throw e;
		}

		return isDateFormatCorrect;

	}

	/*
	 * Compare two dates with year, month and date but ignore time
	 * compareDateIgnoreTime(Date startDate, Date endDate) -1 : startDate >
	 * endDate 0 : startDate == endDate 1 : startDate < endDate
	 */
	public static int compareDateIgnoreTime(Date startDate, Date endDate) {
		Calendar calStartDate = Calendar.getInstance();
		Calendar calEndDate = Calendar.getInstance();
		calStartDate.setTime(startDate);
		calEndDate.setTime(endDate);
		int yearOfStartDate = calStartDate.get(Calendar.YEAR);
		int yearOfEndDate = calEndDate.get(Calendar.YEAR);
		int monthOfStartDate = calStartDate.get(Calendar.MONTH);
		int monthOfEndDate = calEndDate.get(Calendar.MONTH);
		int dateOfStartDate = calStartDate.get(Calendar.DATE);
		int dateOfEndDate = calEndDate.get(Calendar.DATE);

		if (yearOfStartDate > yearOfEndDate) {
			return -1;
		} else if (yearOfStartDate == yearOfEndDate) {
			if (monthOfStartDate > monthOfEndDate) {
				return -1;
			} else if (monthOfStartDate == monthOfEndDate) {
				if (dateOfStartDate > dateOfEndDate) {
					return -1;
				} else if (dateOfStartDate == dateOfEndDate) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

	public static int daysDifference(Date fromDate, Date toDate) {
		return compareDateIgnoreTime(fromDate, toDate);
	}

	public static String getPatternByLocale(Locale locale) {
		if (locale == null) {
			return DateFormatConstant.DATE_ENG_SLASH;
		}
		if (Locale.US.equals(locale))
			return DateFormatConstant.DATE_ENG_SLASH;

		if (Locale.SIMPLIFIED_CHINESE.equals(locale))
			return DateFormatConstant.DATE_CHS_HYPHEN;

		if (Locale.TRADITIONAL_CHINESE.equals(locale))
			return DateFormatConstant.DATE_CHT_SLASH;

		return DateFormatConstant.DATE_ENG_SLASH;

	}

	public static Date makeDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	public static java.sql.Date makeSqlDate(int year, int month, int day) {
		Date date = makeDate(year, month, day);
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Date getCS_NULL_DATE() {
		return getNullDate();
	}

	public static java.sql.Date getNullDate() {
		return makeSqlDate(1111, 11, 11);
	}

	public static java.sql.Date getCS_9999_DATE() {
		return makeSqlDate(9999, 1, 1);
	}

	public static java.sql.Date getCS_CONVERT_DATE() {
		return makeSqlDate(2000, 10, 1);
	}

	public static int getDiffYear(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
	}

	public static boolean isDate(String format, String dateString) {
		if (StringUtils.isEmpty(dateString)) {
			return false;
		}
		parse(format, dateString);
		return true;
	}

	public static String getNullIfDefaultDate(String date) {
		if (date.equals("11/11/1111")) {
			date = "";
		}
		return date;
	}

	public static boolean isNullDate(Date date) {
		if (date == null) {
			return true;
		} else {
			String dateStr = DateUtils.formatDateHK(date);
			if ("11/11/1111".equals(dateStr)) {
				return true;
			}
		}
		return false;
	}

	// "dd/MM/yyyy" convert to "yyyy-MM-dd" "20/12/2012"-->"2012-12-20"
	public static String getConvertDate(String date) {
		if (date.contains("/")) {
			String[] str_arry = date.split("/");

			return str_arry[2] + "-" + str_arry[1] + "-" + str_arry[0];
		} else {
			return date;
		}

	}

	// "yyyy-MM-dd" convert to "dd/MM/yyyy" "2012-12-20"-->"20/12/2012"
	public static String getConvertDate2(String date) {
		if (date.contains("-")) {
			String[] str_arry = date.split("-");

			return str_arry[2] + "/" + str_arry[1] + "/" + str_arry[0];
		} else {
			return date;
		}

	}

	public static java.sql.Date newDate(java.sql.Date temp) {
		return temp != null ? new java.sql.Date(temp.getTime()) : null;
	}

	public static java.sql.Date newDate(Date temp) {
		return temp != null ? new java.sql.Date(temp.getTime()) : null;
	}

	public static String getStryyyyMMddTHHmmssSSS(Date d) {
		return parseDate(d, DateFormatConstant.DATETIME_WS_LONG);
	}

	public static String getyyyyMMddHHmm(Date d) {
		return parseDate(d, DateFormatConstant.DATETIME_WITHOUT_SECOND);
	}

	public static String getDateString(Object obj) {
		String defualtString = "";
		if (obj == null || "11/11/1111".equals(obj.toString())
				|| "11-11-1111".equals(obj.toString())) {
			return defualtString;
		}
		return getConvertDate2(obj.toString());
	}
	
	/**
	 * 获取日期
	 * @param date 当前日期
	 * @param type 获取类型 Calendar.DAY_OF_MONTH:天，Calendar.HOUR_OF_DAY：小时
	 * @param num 哪一天/小时 （-1：昨天/上一小时、+1：明天/下一小时、-2：前天/上上小时，+2：后天/下下小时;以此类推）
	 * @return
	 */
	public static Date getOtherDay(Date date, int type, int num){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(type, num);
		return calendar.getTime();
	} 
	
	/**
	 * 获取日期 
	 * @param time 原来的日期字符串
	 * @param format 字符串的日期格式
	 * @param type 操作的日期类型：年，月，日，时，分，秒等
	 * @param num 操作的日期的数量
	 * @return
	 * @throws Exception
	 */
	public static String getOtherTime(String time, String format, int type, int num) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date lastDate = sdf.parse(time); 
		Date otherDate = getOtherDay(lastDate, type, num); 
		String otherTime = sdf.format(otherDate); 
		return otherTime;
	}
	
	/**
	 * 获取日期相差的天数
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return 
	 */
	public static int getDatediff(Date startDate,Date endDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		long startMilliseconds = calendar.getTimeInMillis();
		calendar.setTime(endDate);
		long endMilliseconds = calendar.getTimeInMillis();
		Long diff = (endMilliseconds - startMilliseconds) / (1000*3600*24);
		return diff.intValue();
	}
	
	/**
	 * 获取小时差
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public static int getHourDiff(Date startDate,Date endDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		long startMilliseconds = calendar.getTimeInMillis();
		calendar.setTime(endDate);
		long endMilliseconds = calendar.getTimeInMillis();
		Long diff = (endMilliseconds - startMilliseconds) / (1000*3600);
		return diff.intValue();
	}
	
	/**
	 * 将长整形值字符串转换成日期对象
	 * @param date 日期对应长整形值字符串
	 * @return 日期对象
	 */
	public static Date getDateByStr(String date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(date));
		return calendar.getTime();
	}
	
	/**
	 * 将长整形值转换成日期对象
	 * @param date 日期对应长整形值
	 * @return 日期对象
	 */
	public static Date getDateByLong(long date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		return calendar.getTime();
	}
	
	public static boolean equalDateHour(Date startDate, Date endDate){
		if (endDate.before(startDate)) {
			return false;
		}
		Calendar s_calendar = Calendar.getInstance();
		s_calendar.setTime(startDate);
		Calendar e_calendar = Calendar.getInstance();
		e_calendar.setTime(endDate);
		int startHour = s_calendar.get(Calendar.HOUR_OF_DAY);
		int endHour = e_calendar.get(Calendar.HOUR_OF_DAY);
		
		return endHour - startHour >= 1 ? true : false;
	}
	


	/**
	 * 按照时分秒的格式，显示格式化当前
	 * @param time 单位秒
	 * @return
	 */
	public static String formatTime(long time) {
		long seconds = time;
		long hours = seconds / 3600;
		seconds = seconds % 3600; 
		StringBuilder sb = new StringBuilder();
		if(hours > 0) {
			sb.append(hours).append("小时");
		}
		long minits = seconds / 60;
		if(minits > 0) {
			sb.append(minits).append("分");
		}
		seconds  = seconds % 60;
		sb.append(seconds).append("秒");
		return sb.toString();
	}

	/**
	 * 获取过去24小时时间戳
	 * @return
	 */
	public static List<Long> getLastDayHourInTimeStamp(){
		List<Long> timeStamp = Lists.newArrayList();
		long endTimeStamp = DateUtils.parse2TimesTamp(DateUtils.formatCurrDateyyyyMMddHH(),"yyyyMMddHH");

		endTimeStamp -= oneHourInSecond;
		for(int i=24;i>0;i--){
			timeStamp.add(endTimeStamp);
			endTimeStamp = endTimeStamp - oneHourInSecond;
		}
		Collections.reverse(timeStamp);
		return timeStamp;
	}

	/**
	 * 获取过去1个月每天时间对应的时间戳
	 * @return
	 */
	public static List<Long> getLastMonthInTimeStamp(){
		List<Long> timeStamp = Lists.newArrayList();
		long endTimeStamp = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(new Date()),"yyyyMMdd");

		endTimeStamp -= oneDayInSecond;
		for(int i=30;i>0;i--){
			timeStamp.add(endTimeStamp);
			endTimeStamp = endTimeStamp - oneDayInSecond;

		}
		Collections.reverse(timeStamp);
		return timeStamp;
	}

	/**
	 * 获取过去10周周一的时间戳
	 * @return
	 */
	public static List<Long> getLastTenWeekInTimeStamp(){
		List<Long> timeStamp = Lists.newArrayList();
		Date firstDayofWeek = DateUtils.getThisWeekMonday(new Date());
		Calendar calendar = DateUtils.toCalendar(firstDayofWeek);
//		calendar.set(Calendar.DAY_OF_WEEK,1);
		long endTimeStamp = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMMdd(calendar.getTime()),"yyyyMMdd");
		endTimeStamp -= oneWeekInSecond;
		for(int i=10;i>0;i--){
			timeStamp.add(endTimeStamp);
			endTimeStamp = endTimeStamp - oneWeekInSecond;

		}
		Collections.reverse(timeStamp);
		return timeStamp;
	}

	/**
	 * 获取过去10月每个月第一天的时间戳
	 * @return
	 */
	public static List<Long> getLastTenMonthInTimeStamp(){
		List<Long> timeStamp = Lists.newArrayList();
		Calendar calendar = DateUtils.toCalendar(new Date());
		calendar.add(Calendar.MONTH,-1);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		long endTimeStamp = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMM(calendar.getTime()),"yyyyMM");
		timeStamp.add(endTimeStamp);
		for (int i=0;i<9;i++){
			calendar.add(Calendar.MONTH,-1);
			endTimeStamp = DateUtils.parse2TimesTamp(DateUtils.formatDateyyyyMM(calendar.getTime()),"yyyyMM");
			timeStamp.add(endTimeStamp);
		}
		Collections.reverse(timeStamp);
		return timeStamp;
	}

	/**
	 * 根据当前时间确定当前时间所属周一的时间
	 * @param date
	 * @return
	 */
	public static Date getThisWeekMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		return cal.getTime();
	}

	/**
	 * 返回格式化（YYYYMMdd）的时间集合
	 * @param start 开始时间时间戳
	 * @param end 结束时间时间戳
	 * @return
	 */
	public static List<Long> getDaysBetweenDay(Long start,Long end){
		List<Long> dateList = Lists.newArrayList();
		String endTime = DateUtils.formatDateyyyyMMdd(new Date(end));
		String startTime = DateUtils.formatDateyyyyMMdd(new Date(start));
		if (Long.valueOf(endTime)<Long.valueOf(startTime)){
			logger.info("invalid time");
			return null;
		}
		long i =1;
		while(!startTime.equals(endTime)){
			if (Long.valueOf(endTime)<Long.valueOf(startTime)){
				break;
			}
			dateList.add(Long.valueOf(startTime));
			startTime = DateUtils.formatDateyyyyMMdd(new Date(start+oneDayInSecond*i*1000));
			i++;
		}
		dateList.add(Long.valueOf(endTime));
		return dateList;
	}

	public static void main(String[] args) {

		List<Long> result = getDaysBetweenDay(1529044136000L,1529562536000L);
		System.out.println(result);
	}


}
