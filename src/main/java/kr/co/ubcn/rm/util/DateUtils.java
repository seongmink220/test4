/**
 * Copyright (c) 2008-2020 UBCn, Inc. and/or its affiliates. All rights reserved.
 *
 * This file is part of TMS(Terminal Management System).
 *
 * TMS can not be copied and/or distributed without the express permission of UBCn, Inc.
 */
package kr.co.ubcn.rm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 *
 * 날짜{@link Date}를 다루기 위한 static 메소드 모음
 *
 * 날짜를 간편하게 다루기 위한 메소드들이 정의되어 있다.
 *
 * @author Young.K Park
 *
 */
public final class DateUtils {

	/** 날짜시간 서식 */
	public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";
	
	public static final String DATETIME_FORMAT_HYPEN = "yyyy-MM-dd HH:mm:ss";
	
	/** 날짜 서식 */
	public static final String DATE_FORMAT = "yyyyMMdd";
	/** 시간 서식 */
	public static final String TIME_FORMAT = "HHmmss";
	
	public static final String DATE_FORMAT_HYPEN = "yyyy-MM-dd";

	/** 날짜서식화 인스턴스 캐시 */
	private static final Hashtable<String, ThreadLocal<DateFormat>> dateFormatCache
		= new Hashtable<>();

	/**
	 * 기본 생성자로 객체로 생성될 수 없도록 한다.
	 *
	 * 유틸리티 클래스는 모든 정적 함수로 생성될 필요가 없다.
	 */
	private DateUtils() {}

	/**
	 * 주어진 서식의 현재 쓰레드 전용 DateFormat 인스턴스를 얻는다.
	 *
	 * @param format 서식
	 *
	 * @return DateFormat 인스턴스
	 */
	private static DateFormat getDateFormat(final String format) {
		ThreadLocal<DateFormat> tldf = dateFormatCache.get(format);

		if (tldf == null) {
			tldf = new ThreadLocal<DateFormat>() {
						@Override
						protected DateFormat initialValue() {
							DateFormat dateFormat = new SimpleDateFormat(format);

							dateFormat.setLenient(false);

							return dateFormat;
						}
					}
				;

			dateFormatCache.put(format, tldf);
		}

		return tldf.get();
	}

	/**
	 * 서식에 맞추어 문자열을 해석하여 날짜를 생성한다.
	 *
	 * @param format 서식
	 * @param text 문자열
	 *
	 * @return 날짜
	 */
	public static Date parse(String format, String text) throws ParseException {
		return getDateFormat(format).parse(text);
	}

	/**
	 * 단순 년월일 서식의 문자열을 해석하여 날짜를 생성한다.
	 *
	 * @param text 문자열
	 *
	 * @return 날짜
	 *
	 * @throws ParseException
	 */
	public static Date parseSimpleDate(String text) throws ParseException {
		return parse(DATE_FORMAT, text);
	}

	/**
	 * 단순 시분초 서식의 문자열을 해석하여 날짜를 생성한다.
	 *
	 * @param text 문자열
	 *
	 * @return 날짜
	 *
	 * @throws ParseException
	 */
	public static Date parseSimpleTime(String text) throws ParseException {
		return parse(TIME_FORMAT, text);
	}

	/** 단순 년월일시분초 서식의 문자열을 해석하여 날짜를 생성한다.
	 *
	 * @param text 문자열
	 *
	 * @return 날짜
	 *
	 * @throws ParseException
	 */
	public static Date parseSimpleDatetime(String text) throws ParseException {
		return parse(DATETIME_FORMAT, text);
	}

	/**
	 * 서식에 맞춰 날짜를 문자열로 구성한다.
	 *
	 * @param format 형식
	 * @param date 날짜
	 *
	 * @return 서식에 맞춰 생성된 문자열
	 */
	public static String format(String format, Date date) {
		return getDateFormat(format).format(date);
	}

	/**
	 * 날짜를 년월일(yyyyMMdd) 서식의 문자열로 구성한다.
	 *
	 * @param date 날짜
	 *
	 * @return 년월일 문자열
	 */
	public static String formatSimpleDate(Date date) {
		return format(DATETIME_FORMAT, date);
	}

	/**
	 * 날짜를 시분초(HHmmss) 서식의 문자열로 구성한다.
	 *
	 * @param date 날짜
	 *
	 * @return 시분초 문자열
	 */
	public static String formatSimpleTime(Date date) {
		return format(TIME_FORMAT, date);
	}

	/**
	 * 날짜를 년월일시분초(yyyyMMddHHmmss) 서식의 문자열로 구성한다.
	 *
	 * @param date 날짜
	 *
	 * @return 년월일시분초 문자열
	 */
	public static String formatSimpleDatetime(Date date) {
		return format(DATETIME_FORMAT, date);
	}
	
	public static String formatSimpleDatetimeHypen(Date date) {
		return format(DATETIME_FORMAT_HYPEN, date);
	}
	
	 public static String formatSimpleDateHypen(Date date) {
		    return format(DATE_FORMAT_HYPEN, date);
	  }
	 
	 
	 /**
	   * 
	   * @param code  "h" - hypen
	   * @param type  "t" - time format
	   * @return
	   */
	  public static String nowDate(String code,String type) {
	    Date date = new Date();
	    String nowDate = "";
	    if("h".equals(code)) {
	       if("t".equals(type)) {	
	    	   nowDate = formatSimpleDatetimeHypen(date);
	       }else {
	    	   nowDate = formatSimpleDatetime(date);
	       }//
	    }else {
	       if("t".equals(type)) {
	    	   nowDate = formatSimpleDatetime(date);
	       }else {
	    	   nowDate = formatSimpleDate(date);   
	       }//
	    }		
		return nowDate;
	  }
	 
	 
	
	/**
	   * DataAdd 
	   * @param code
	   * @param iday
	   * @return
	   */
	  public static String dateAdd(String code, int iday) {
		  Date date = new Date();
		  String addDay = null;
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  cal.add(Calendar.DATE,iday);
		  
		  if(code.equals("h")) {			  
			  addDay = formatSimpleDateHypen(cal.getTime());			  
		  }else {
			  addDay = formatSimpleDate(cal.getTime());
		  }
		  
		  return addDay;
	  }
	  
	  /**
	   * timeAdd
	   * @param code
	   * @param iTime
	   * @return
	   */
	  public static String timeAdd(String code, int iTime) {
		  Date date = new Date();
		  String addDay = null;
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  cal.add(Calendar.HOUR,iTime);
		  
		  if(code.equals("h")) {			  
			  addDay = formatSimpleDateHypen(cal.getTime());			  
		  }else {
			  addDay = formatSimpleDate(cal.getTime());
		  }
		  
		  return addDay;
	  }
	  
	  
	  public static String timeAdd2(String dt,int iTime, int iMin) {
		  String addDay="";
		  try {
			  SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  Date dte = sdt.parse(dt);
			  
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(dte);
			  cal.add(Calendar.HOUR,iTime);
			  cal.add(Calendar.MINUTE,iMin);
			  
			  addDay = formatSimpleDate(cal.getTime());
			  
		  }catch(Exception ex) {
			  ex.printStackTrace();
		  }
		  return addDay;
	  }
	  
	  /**
	   * 날짜 String 를 날짜형식으로 변환
	   * @param dt
	   * @return
	   */
	  public static String rtnDateString(String dt) {
		  String rtnDT=dt;
		  StringBuilder sb = new StringBuilder();
		  if(dt.length()==8) {
			  sb.append(dt.substring(0,4)+"-");
			  sb.append(dt.substring(4,6)+"-");
			  sb.append(dt.substring(6,8));
			  
			  rtnDT = sb.toString();			  
		  }else if(dt.length()==14) {
			  sb.append(dt.substring(0,4)+"-");
			  sb.append(dt.substring(4,6)+"-");
			  sb.append(dt.substring(6,8)+" ");
			  sb.append(dt.substring(8,10)+":");
			  sb.append(dt.substring(10,12)+":");
			  sb.append(dt.substring(12,14));
			  
			  rtnDT = sb.toString();
		  }
		  
		  return rtnDT;
	  }
	  
	  
	  
	  
	  /**
	   * Milisec 단위 리턴
	   * @return
	   */
	  public static String rtnDateMille() {
			String pattern = "yyyyMMddHHmmssSSS";
			String nowTime="";
			//long now = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			Date now = new Date();
			nowTime = formatter.format(now);
			
			return nowTime;
	  }
	  
	  

}