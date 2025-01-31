package bk.sandbox;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper class that does date arithmetic and date format conversions.
 * 
 * @author bk
 *
 */
public abstract class DateHelper {

	private static String[] holidays = {
			"2019-01-01", 	// New Years day
			"2019-01-21", 	// MLK Day
			"2019-02-18", 	// President's Day
			"2019-04-19", 	// Good Friday
			"2019-05-27",	// Memorial Day
			"2019-07-04", 	// July 4th
			"2019-09-02", 	// Labor Day
			"2019-11-28", 	// Thanksgiving
			"2019-12-25",	// Christmas
		};

	/**
	 * Returns true if arg string is in YYYY/MM/DD format. Only the syntax is checked. The validity
	 * of the date is not checked.
	 *
	 * @param s
	 * @return
	 */
	public static boolean isDateFormat(final String s) {
		return s.matches("[0-9]{4}/[0-9]{2}/[0-9]{2}");
	}

	/**
	 * Sets the date in the provided Calendar object to the value
	 * of the provided date string in yyyy-MM-dd format.
	 *
	 * @param dateStr
	 * @param c
	 */
	public static void getCalendarFromDate(String dateStr, Calendar c) {
		dateStr = dateStr.replaceAll("/", "-");
		final String[] arr = dateStr.split("-");
		c.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));
	}

	/**
	 * Converts date in yyyy-MM-dd format and time in HH:mm:ss format into a Calendar object.
	 * 
	 * @param dateStr
	 * @param time
	 * @param c
	 */
	public static void getCalendarFromDateTime(String dateStr, String time, Calendar c) {
		dateStr = dateStr.replaceAll("/", "-");
		final String[] dateArr = dateStr.split("-");
		final String[] timeArr = time.split(":");
		c.clear();
		c.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2]), 
				Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
	}
	
	/**
	 * Returns date in yyyy-MM-dd format
	 *
	 * @param calendar
	 * @return
	 */
	public static String getDateFromCalendar(final Calendar calendar) {
		final int month = calendar.get(Calendar.MONTH) + 1;
		final String monthStr = month < 10 ? "0" + month : Integer.toString(month);

		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final String dayStr = day < 10 ? "0" + day : Integer.toString(day);

		return calendar.get(Calendar.YEAR) + "-" + monthStr + "-" + dayStr;
	}

	/**
	 * Returns a list of dates in the range.
	 *
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static List<Calendar> getDatesInRange(Calendar fromDate, final Calendar toDate) {
		final List<Calendar> alDates = new ArrayList<>();
		
		// if fromDate is after toDate then return an empty list
		if (fromDate.after(toDate))
			return alDates;
		
		int i = 0;
		
		// add dates to the list including from and to dates
		while (true) {
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate.getTime());
			c.add(Calendar.DAY_OF_MONTH, i);
			
			if (c.after(toDate))
				break;
			
			alDates.add(c);
			i += 1;
		}
		
		return alDates;
	}

	/**
	 * Returns dates in range between given from and to dates.  Both from 
	 * and to dates are included in the list.  Returned dates are in yyyy-MM-dd format.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List<String>
	 */
	public static List<String> getDatesInRange(String fromDate, String toDate, boolean excludeHolidays) {
		fromDate = fromDate.replace("/", "-");
		toDate = toDate.replace("/", "-");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		DateHelper.getCalendarFromDate(fromDate, c1);
		DateHelper.getCalendarFromDate(toDate, c2);
		
		List<Calendar> datesInRange = DateHelper.getDatesInRange(c1, c2);
		List<String> al = new ArrayList<>();
		
		for (Calendar c : datesInRange) {
			if (! excludeHolidays)
				al.add(getDateFromCalendar(c));
			else if (! isHoliday(c))
				al.add(getDateFromCalendar(c));
		}
		
		return al;
	}
	
	/**
	 * Given the date in yyyy-MM-dd format, return the week of the year.
	 * For example, given 2019-01-03, returns 2019-01 and given 2016-01-03,
	 * return 2016-02.
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekOfYear(String date) {
		Calendar c = Calendar.getInstance();
		DateHelper.getCalendarFromDate(date, c);
		SimpleDateFormat sdf = new SimpleDateFormat("Y-ww");
		String weekOfYear = sdf.format(c.getTime());

		return weekOfYear;
	}
	
	public static String getMonthOfYear(String date) {
		Calendar c = Calendar.getInstance();
		DateHelper.getCalendarFromDate(date, c);
		SimpleDateFormat sdf = new SimpleDateFormat("Y-MM");
		String monthOfYear = sdf.format(c.getTime());

		return monthOfYear;
	}
	
	/**
	 * Takes as arguments the date format and a date in that format and converts it
	 * to yyyy-MM-dd format.
	 * 
	 * @param origDateFormat (e.g) MMM dd yyyy
	 * @param dateStr (e.g.) Jan 01 2019
	 * @return - String in yyyy-MM-dd format
	 * @throws ParseException 
	 */
	public static String convertDateFormat(String origDateFormat, String dateStr) throws ParseException {
		SimpleDateFormat fromDateFormat = new SimpleDateFormat(origDateFormat);
		SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		String retVal = "";
		d = fromDateFormat.parse(dateStr);
		retVal = toDateFormat.format(d);
		return retVal;
	}
	
	/**
	 * Returns date in yyyy-MM-dd format for the provided long
	 * 
	 * @param msec
	 * @return
	 */
	public static String long2DateStr(long msec) {
		//creating Date from millisecond
		Date date = new Date(msec);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		//formatted value of current Date
		return df.format(date);
	}

	
	/**
	 * Returns date in yyyy-MM-dd HH:mm:ss.S format for the provided long
	 * 
	 * @param msec
	 * @return
	 */
	public static String long2Timestamp(long msec) {
		//creating Date from millisecond
		Date date = new Date(msec);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

		//formatted value of current Date
		return df.format(date);
	}
	
	/**
	 * Takes a timestamp in long and returns an int in the form HHmmss
	 * 
	 * @param msec
	 * @return
	 */
	public static int long2HHmmss(long msec) {
		String timestampStr = DateHelper.long2Timestamp(msec);
		return Integer.parseInt(timestampStr.replaceAll(".* ", "").replaceAll("\\..*", "").replaceAll(":", ""));
	}
	
	/**
	 * Returns date in HH:mm format for the provided long
	 * 
	 * @param msec
	 * @return
	 */
	public static String long2Time(long msec) {
		//creating Date from millisecond
		Date date = new Date(msec);

		DateFormat df = new SimpleDateFormat("HH:mm");

		//formatted value of current Date
		return df.format(date);
	}
	
	/**
	 * Converts date in yyyy-MM-dd format to milliseconds.
	 * 
	 * @param dateStr
	 * @return
	 */
	public static long dateStr2Long(String dateStr) {
		dateStr = dateStr.replaceAll("/", "-");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		
		try {
			d = sdf.parse(dateStr);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return d.getTime();
	}
	
	/**
	 * Converts timestamp in "yyyy-MM-dd HH:mm:ss.S" format to milliseconds.
	 * 
	 * @param timestamp
	 * @return
	 */
	public static long timestamp2Long(String timestamp) {
		timestamp = timestamp.replaceAll("/", "-");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date d = null;
		
		try {
			d = sdf.parse(timestamp);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return d.getTime();
	}
	
	/**
	 * Returns true if the given date is a weekend or a holiday
	 * 
	 * @param calendar
	 * @return
	 */
	public static boolean isHoliday(Calendar calendar) {
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(calendar.getTime());
		
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || Arrays.asList(holidays).contains(date)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the given date in yyyy-MM-dd format is a holiday (including weekends).
	 * 
	 * @param dateStr
	 * @return
	 */
	public static boolean isHoliday(String dateStr) {
		Calendar c = Calendar.getInstance();
		getCalendarFromDate(dateStr, c);
		return isHoliday(c);
	}
	
	/**
	 * Returns true if the given date is today's date
	 * 
	 * @param calendar
	 * @return
	 */
	public static boolean isToday(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		
		if (sdf.format(today.getTime()).equals(sdf.format(calendar.getTime())))
			return true;
		
		return false;
	}
	
	/**
	 * Subtracts a given number of working days from the start date (i.e.) holidays are
	 * excluded. If startDate is a holiday then the previous working day is used as the 
	 * start date.
	 *   
	 * @param startDate
	 * @param numDays
	 * @return
	 */
	public static long dateSub(long startDate, int numDays) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startDate);
		
		while (isHoliday(c)) {
			c.add(Calendar.DATE, -1);
		}
			
		for (int i=0; i<numDays; ) {
			c.add(Calendar.DATE, -1);
			
			if (! isHoliday(c))
				++i;
		}
		
		return c.getTimeInMillis();
	}
		
	/**
	 * Subtracts a given number of working days from the start date (i.e.) holidays are
	 * excluded. If startDate is a holiday then the previous working day is used as the 
	 * start date.
	 * 
	 * @param startDate
	 * @param numDays
	 * @return Date in yyyy-MM-dd format
	 */
	public static String dateSub(String startDate, int numDays) {
		long startDateLong = dateSub(dateStr2Long(startDate), numDays);
		return long2DateStr(startDateLong);
	}
	
	/**
	 * Adds a given number of working days from the start date (i.e.) holidays are
	 * excluded. If startDate is a holiday then the previous working day is used as the 
	 * start date.
	 *   
	 * @param startDate
	 * @param numDays
	 * @return
	 */
	public static long dateAdd(long startDate, int numDays) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startDate);
		
		while (isHoliday(c)) {
			c.add(Calendar.DATE, -1);
		}
			
		for (int i=0; i<numDays; ) {
			c.add(Calendar.DATE, 1);
			
			if (! isHoliday(c))
				++i;
		}
		
		return c.getTimeInMillis();
	}

	/**
	 * Adds a given number of working days from the start date (i.e.) holidays are
	 * excluded. If startDate is a holiday then the previous working day is used as the 
	 * start date.
	 * 
	 * @param startDateStr
	 * @param numDays
	 * @return
	 */
	public static String dateAdd(String startDateStr, int numDays) {
		long startDate = dateStr2Long(startDateStr);
		long result = dateAdd(startDate, numDays);
		
		return long2DateStr(result);
	}
	
	/**
	 * Retruns current timestamp in yyyy-MM-dd HH:mm:ss.S format.
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String timeStamp = sdf.format(new Date());
		return timeStamp;
	}
	
	/**
	 * Retruns current timestamp in yyyy-MM-dd HH:mm:ss format.
	 * 
	 * @return
	 */
	public static String getTimestampNoMillis() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStamp = sdf.format(new Date());
		return timeStamp;
	}

	/**
	 * Returns today's date in yyyy-MM-dd format.
	 * @return
	 */
	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String timeStamp = sdf.format(new Date());
		return timeStamp;
	}

	/**
	 * Returns true if given time is more than 20 mins in the past.
	 * 
	 * @param time
	 * @return
	 */
	public static boolean timeIsMoreThan20MinsInThePast(long time) {
		long currentTime = new Date().getTime();
		long timeDiff = currentTime - time;
		return timeDiff > 1200000;  // 1200000 = 20 * 60 * 1000
	}
	
	/**
	 * Given a date in yyyy-MM-dd format, this method returns the day of week as Mon, Tue etc.
	 * 
	 * @param date
	 * @return
	 */
	public static String getDayOfWeek(String date) {
		String[] arr = date.split("-");
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));

		LocalDate localDate = LocalDate.parse(date);
		DayOfWeek dow = localDate.getDayOfWeek();
		return dow.getDisplayName(TextStyle.SHORT, Locale.US);
	}
	
	/**
	 * Given a date in yyyy-MM-dd format, returns the quarter.  For
	 * example, given 2019-09-04, the return value is 2019Q3
	 * 
	 * @param date
	 * @return
	 */
	public static String date2Qtr(String date) {
		String year = date.replaceAll("-.*", "");
		String monthDay = date.substring(5);
		int md = Integer.parseInt(monthDay.replaceAll("-", ""));
		String qtr = "";
		
		if (md <= 331) {
			qtr = "Q1";
		}
		else if (md <= 630) {
			qtr = "Q2";
		}
		else if (md <= 930) {
			qtr = "Q3";
		}
		else {
			qtr = "Q4";
		}
		return year + qtr;
	}
	
	/**
	 * Given a quarter in yyyyQn format, returns the previous quarter.
	 * For example, given 2020Q1, return 2019Q4.
	 * 
	 * @param qtr
	 * @return
	 */
	public static String getPrevQtr(String qtr) {
		int year = Integer.parseInt(qtr.replaceAll("Q.*", ""));
		int q = Integer.parseInt(qtr.replaceAll(".*Q", ""));
		
		if (q == 1) {
			int prevYear = year - 1;
			return prevYear + "Q4";
		}
		else {
			int prevQtr = q - 1;
			return year + "Q" + prevQtr;
		}
	}
	
	/**
	 * Returns the number of working days (excluding holidays) between the given dates date1 and date2.
	 * date1 and date2 must be in yyyy-MM-dd format.
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int dateDiff(String date1, String date2) {
		int d1 = Integer.parseInt(date1.replaceAll("-", ""));
		int d2 = Integer.parseInt(date2.replaceAll("-", ""));
		
		List<String> datesBetween = null;
		
		if (d1 >= d2) {
			datesBetween = getDatesInRange(date2, date1, true);
		}
		else {
			datesBetween = getDatesInRange(date1, date2, true);
		}
		
		int numDays = datesBetween.size() - 1;
		
		return d1 > d2 ? numDays * -1 : numDays;
	}
	
	/**
	 * Returns the number of days between the given dates date1 and date2.
	 * date1 and date2 must be in YYYY-MM-DD format.
	 * 
	 * @param date1
	 * @param date2
	 * @param excludeHolidays
	 * @return
	 */
	public static int dateDiff(String date1, String date2, boolean excludeHolidays) {
		int d1 = Integer.parseInt(date1.replaceAll("-", ""));
		int d2 = Integer.parseInt(date2.replaceAll("-", ""));
		
		List<String> datesBetween = null;
		
		if (d1 >= d2) {
			datesBetween = getDatesInRange(date2, date1, excludeHolidays);
		}
		else {
			datesBetween = getDatesInRange(date1, date2, excludeHolidays);
		}
		
		int numDays = datesBetween.size() - 1;
		
		return d1 > d2 ? numDays * -1 : numDays;
	}
}
