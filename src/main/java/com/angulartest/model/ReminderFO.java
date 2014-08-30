package com.angulartest.model;

import com.angulartest.utility.ReminderDateFormatter;
import com.angulartest.utility.ReminderTimeFormatter;

public class ReminderFO {
	private String mobileNumber;
	private String provider;
	private String dateTime;
	private String timezone;
	private String message;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public Reminder convertReminderFOToReminder() {
		ReminderDateFormatter reminderDateFormatter = new ReminderDateFormatter();
		String formattedDate = reminderDateFormatter.getDateFromUserInput(dateTime, timezone);
		
		Reminder reminder = new Reminder();
		reminder.setDate(formattedDate);
		
		ReminderTimeFormatter reminderTimeFormatter = new ReminderTimeFormatter();
		String formattedTime = reminderTimeFormatter.getTimeFromUserDateTime(dateTime);
		
		reminder.setTime(formattedTime);
		reminder.setTimezone(getTimezone());
		reminder.setMessage(getMessage());
		reminder.setProvider(getProvider());
		reminder.setCellNumber(getMobileNumber()); 
		
		return reminder;
	}
}
