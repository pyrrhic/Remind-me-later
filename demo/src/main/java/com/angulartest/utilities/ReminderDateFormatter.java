package com.angulartest.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.angulartest.model.ReminderFO;
	
public class ReminderDateFormatter extends ReminderFormatter {
	public String getDateFromUserInput(String dateTime, String timezone) {
		List<String> slashMatches = new LinkedList<String>();
		List<String> weekMatches = new LinkedList<String>();
		List<String> soonMatches = new LinkedList<String>();
		
		slashMatches = getSlashFormatMatches(dateTime);
		if (slashMatches.size() == 0) {
			weekMatches = getWeekFormatMatches(dateTime);
			if (weekMatches.size() == 0) {
				soonMatches = getSoonFormatMatches(dateTime);
			}
		}
		
		int numDateMatches = slashMatches.size() + weekMatches.size() + soonMatches.size();
		if (numDateMatches == 0) return "";
		
		// if matches > 1 or < 1, gtfo
		
		DateType dateType = getDateType(slashMatches.size(), weekMatches.size());
		
		String returnDate = "";
		switch(dateType) {
			case SLASH:
				returnDate = getDateFromSlash(slashMatches.get(0));
				break;
				
			case WEEK:
				returnDate = getDateFromWeek(timezone, weekMatches.get(0));
				break;
				
			case SOON:
				returnDate = getDateFromSoon(soonMatches.get(0));
				break;
		}
		
		return returnDate;
	}
	
	private enum DateType {
		SLASH, WEEK, SOON
	}
	
	private List<String> getSlashFormatMatches(String dateTime) {
		String regexPattern = "[0-9]{1,2}/{1}[0-9]{1,2}/{1}([0-9]{4}|[0-9]{2})"; 
		return getMatches(regexPattern, dateTime);
	}
	
	private List<String> getSoonFormatMatches(String dateTime) {
		String regexPattern = "today|tomorrow";
		return getMatches(regexPattern, dateTime);
	}
	
	private List<String> getWeekFormatMatches(String dateTime) {		
		String regexPattern = "monday|tuesday|wednesday|thursday|friday|saturday|sunday";
		return getMatches(regexPattern, dateTime);
	}
	
	private DateType getDateType(int numSlashMatches, int numWeekMatches) {
		DateType dateType;
		
		if(numSlashMatches > 0) {
			dateType = DateType.SLASH;
		}
		else if(numWeekMatches > 0) {
			dateType = DateType.WEEK;
		}
		else {
			dateType = DateType.SOON;
		}
			
		return dateType;
	}
	
	private String getDateFromSlash(String date) {		
		int monthDelim = date.indexOf("/");
		String month = date.substring(0, monthDelim);
		
		int dayDelimStart = date.indexOf("/", monthDelim);
		int dayDelimEnd = date.indexOf("/", date.lastIndexOf("/"));
		String day = date.substring(dayDelimStart+1, dayDelimEnd);
		
		int yearDelimStart = date.lastIndexOf("/");
		String year = date.substring(yearDelimStart+1, date.length());
		
		return year + "-" + month + "-" + day;
	}
	
	private String getDateFromWeek(String timezone, String date) {
		Calendar cal = new GregorianCalendar();
		TimeZone tz = TimeZone.getTimeZone("America/New_York");
		cal.setTimeZone(tz);
		
		if( !timezone.equals("America/New_York") ) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setCalendar(cal);
			Date todayDate = cal.getTime();
			String today = dateFormat.format(todayDate);
			
			//this doesnt get used. this just uses current time derp.
			String convertedTime = Utils.convertToTimezone(today, timezone, "America/New_York");
			
			int space = convertedTime.indexOf(" ");
			String formattedTime = convertedTime.substring(0, space) + "T" + convertedTime.substring(space + 1);
			
			cal.setTimeZone(TimeZone.getTimeZone(timezone));
			cal.setTime(todayDate);
		}	
		
		int reminderDayOfWeek = getReminderDayOfWeekAsNumber(date);

		//if today is monday and user entered in monday for the reminder, they probably meant next monday, not today.
		addDaysToCalendarBasedOnReminderDate(cal, reminderDayOfWeek);
		
		return getCalendarDate(cal);
	}
	
	private void addDaysToCalendarBasedOnReminderDate(Calendar cal, int reminderDayOfWeek) {
		if(reminderDayOfWeek != cal.get(Calendar.DAY_OF_WEEK)) {
			while(cal.get(Calendar.DAY_OF_WEEK) != reminderDayOfWeek) {
				cal.add(Calendar.DAY_OF_WEEK, 1);
			}	
		}
		else {
			cal.add(Calendar.DAY_OF_WEEK, 7);
		}
	}
	
	private String getCalendarDate(Calendar cal) {
		String year = Integer.toString(cal.get(Calendar.YEAR));
		
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		
		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		
		return year + "-" + month + "-" + day; 
	}
	
	private int getReminderDayOfWeekAsNumber(String date) {
		int reminderDayOfWeek = 1;
		
		if (date.equals("monday")) {
			reminderDayOfWeek = 2;
		}
		else if (date.equals("tuesday")) {
			reminderDayOfWeek = 3;
		}
		else if (date.equals("wednesday")) {
			reminderDayOfWeek = 4;
		}
		else if (date.equals("thursday")) {
			reminderDayOfWeek = 5;
		}
		else if (date.equals("friday")) {
			reminderDayOfWeek  = 6;
		}
		else if (date.equals("saturday")) {
			reminderDayOfWeek = 7;
		}
		else if (date.equals("sunday")) {
			reminderDayOfWeek = 1;
		}
		
		return reminderDayOfWeek;
	}
	
	private String getDateFromSoon(String date) {		
		//now|today|tomorrow
		Calendar calendar = new GregorianCalendar();
		TimeZone timezone = TimeZone.getTimeZone("America/New_York");
		calendar.setTimeZone(timezone);
		
		if (date.equals("tomorrow")) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		String month = Integer.toString(calendar.get(Calendar.MONTH)+1);
		String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		
		return year + "-" + month + "-" + day;
	}
}
