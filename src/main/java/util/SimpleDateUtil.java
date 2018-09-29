package util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateUtil {
	private static String defaultDate = "yyyy-MM-dd ";
	private static String defaultDateTime = "yyyy-MM-dd hh:mm ";
	private static String defaultDateTimes = "yyyy-MM-dd HH:mm:ss";

	public static String getTimeDifferent(Date dt1,Date dt2){
		//一天的毫秒数
		long d = 1000 * 24 * 60 * 60;
		//一小时的毫秒数
		long h = 1000 * 60 * 60;
		//一分钟的毫秒数
		long m = 1000 * 60;
		// 获得两个时间的毫秒时间差异
		long timeDiff = dt2.getTime() - dt1.getTime();
		// 计算差多少天
		long day = timeDiff / d;
		// 计算差多少小时
		long hour = timeDiff % d / h;
		// 计算差多少分钟
		long min = timeDiff % d % h / m;
		//计算差多少秒
//		long ss=
		return day + "天" + hour + "小时" + min + "分钟";
	}
	/**
	 * 根据String型时间，获取long型时间，单位毫秒
	 * @param inVal 时间字符串
	 * @return long型时间
	 */
	public static long fromDateStringToLong(String inVal) {
		Date date = null;
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if (null==inVal){
			inVal=inputFormat.format(new Date());
		}
		try {
			date = inputFormat.parse(inVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
	/**
	 * 
	 * 摘要：字符串转化为日期
	 * 
	 * @说明：
	 * @创建：作者:
	 * @param format
	 *            转化格式(如:yyyy-MM-dd HH:mm:ss)
	 * @param source
	 *            字符串(2011-01-18)
	 * @return
	 * @修改历史：
	 */
	public static Date parse(String format, String source) {
		Date target = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		try {
			target = simpleDateFormat.parse(source);
		} catch (Exception px) {
			target = null;
		}
		return target;
	}
	


	/**
	 * 
	 * 摘要：日期转化为字符串
	 * 
	 * @说明：
	 * @创建：
	 * @param
	 * @param source 日期
	 * @return
	 * @修改历史：
	 */
	public static String format(String strFormat, Date source) {
		String target = null;
		SimpleDateFormat f = new SimpleDateFormat(strFormat);
		target = f.format(source);
		return target;
	}
	

	
	/**
	 * 比较2个日期的大小
	 * @param dateStr1 最近的日期
	 * @param dateStr2 以前的日期
	 * @return 返回天数
	 */
	public static Integer compareDate(String dateStr1, String dateStr2){
		Integer day= -1;
		Date date1= parse("yyyy-MM-dd", dateStr1);
		Date date2= parse("yyyy-MM-dd", dateStr2);
		
		Long l= date1.getTime()-date2.getTime(); 
		if(l.intValue()!=0){
			Long day_l= l/(24*60*60*1000);
			day = day_l.intValue();
		}
		//	long l=now.getTime()-date.getTime(); 
		//	long day=l/(24*60*60*1000);  
		//	long hour=(l/(60*60*1000)-day*24);  
		//	long min=((l/(60*1000))-day*24*60-hour*60); 
		//	long s=(l/1000-day*24*60*60-hour*60*60-min*60);
		return day;
	}
	/**
	 * 获取现在时间
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getNowDateTime(String strFormat){
		Date date=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat(strFormat);
		String dateString=dateFormat.format(date);
		ParsePosition pos=new ParsePosition(8);
		Date dateTime=dateFormat.parse(dateString,pos);
		return dateTime;
	}

	/**
	 * 获取当前时间
	 * @param strFormat
	 * @return
	 */
	public static String getCurrentDate(String strFormat) {
		Date source = new Date();
		String target = null;
		SimpleDateFormat f = new SimpleDateFormat(strFormat);
		target = f.format(source);
		return target;
	}

	/**
	 * 获取日期,格式yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static String getLongDate() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}


	

	
}
