package com.angulartest.utility;

import java.util.ArrayList;
import java.util.List;

public class ReminderTimeFormatter extends ReminderFormatter {
	public String getTimeFromUserDateTime(String dateTime) {
		//assumes format MM/DD/YYYY 00:00:00AM
		String time = "";
		if (dateTime.contains(" ")) { 
			String tokens[] = dateTime.split(" ");
			if (tokens.length <= 1) return "";
			
			time = dateTime.split(" ")[1];		
		}
		
		if (time == "") return "";
		
		List<String> shortMatches = new ArrayList<String>();
		List<String> longMatches = getLongTimeMatches(dateTime);
		
		if (longMatches.size() == 0) {
			shortMatches = getShortTimeMatches(dateTime);
			if (shortMatches.size() == 0) {
				return "";
			}
		}
		
		TimeFormatType timeFormat = getTimeFormatType(shortMatches, longMatches, dateTime);

		String unformattedTime = (timeFormat == TimeFormatType.SHORT) ? getTimeMatch(shortMatches, dateTime) : getTimeMatch(longMatches, dateTime);
		String hours = "00";
		String minutes = "00";
		String seconds = "00";
		String period = unformattedTime.substring(unformattedTime.length() - 2).toUpperCase();
		if (timeFormat == TimeFormatType.SHORT) {
			// 10am
			// 5pm
			int endHourLocation = unformattedTime.length() - 2;
			hours = unformattedTime.substring(0, endHourLocation);
			minutes = "00";
		} else {
			// 12:09pm
			// 5:44am
			int endHourLocation = unformattedTime.indexOf(":");
			hours = unformattedTime.substring(0, endHourLocation);
			minutes = unformattedTime.substring(endHourLocation + 1, unformattedTime.length() - 2);
		}

		return hours + ":" + minutes + ":" + seconds + period;
	}

	private TimeFormatType getTimeFormatType(List<String> shortMatches, List<String> longMatches, String dateTime) {
		TimeFormatType timeFormatType = null;
		if (shortMatches.size() > 0 && shortMatches.get(0).length() <= dateTime.length()) {
			timeFormatType = TimeFormatType.SHORT;
		} else if (longMatches.size() > 0 && longMatches.get(0).length() <= dateTime.length()) {
			timeFormatType = TimeFormatType.LONG;
		}

		return timeFormatType;
	}

	private enum TimeFormatType {
		SHORT, LONG
	}

	private String getTimeMatch(List<String> matches, String dateTime) {
		String match = "";
		if (matches.size() > 0 && matches.get(0).length() <= dateTime.length()) {
			match = matches.get(0);
		}

		return match;
	}

	private List<String> getShortTimeMatches(String dateTime) {
		String regexPattern = "([1]{1}[0-2]{1}|[1-9]{1})(AM|PM|am|pm){1}";

		return getMatches(regexPattern, dateTime);
	}

	private List<String> getLongTimeMatches(String dateTime) {
		String regexPattern = "([1]{1}[0-2]{1}|[1-9]{1})(:){1}[0-5][0-9](AM|PM|am|pm){1}";

		return getMatches(regexPattern, dateTime);
	}
}
