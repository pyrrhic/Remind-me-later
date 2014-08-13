package com.angulartest.model;

public class Reminder {
	private int id;
	
	private String fromUser;
	private String name;
	private String message;
	private String cellNumber;
	private String provider;
	
	private String date;
	
	private String time;
	private String timezone;
	
	/**
	 * Non-basic getters and setters
	 */
	
	//date related
	public String getYear() {
		int endOfYearDelim = date.indexOf("-");
		
		return date.substring(0, endOfYearDelim);
	}
	
	public String getMonth() {
		int endOfYearDelim = date.indexOf("-");
		int endOfMonthDelim = date.indexOf("-", endOfYearDelim+1);
		
		return date.substring(endOfYearDelim+1, endOfMonthDelim);
	}
	
	public String getDay() {
		int endOfYearDelim = date.indexOf("-");
		int endOfMonthDelim = date.indexOf("-", endOfYearDelim+1);
		
		return date.substring(endOfMonthDelim+1);
	}
	
	
	//time related
	public String getHour() {
		return time.substring(0, 2);
	}
	
	public String getMinute() {
		return time.substring(3, 5);
	}
	
	public String getPeriod() {
		return time.substring(time.length()-2);
	}
	
	// Basic getters and setters
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCellNumber() {
		return this.cellNumber;
	}

	public void setCellNumber(String sendTo) {
		this.cellNumber = sendTo;
	}

	public String getProvider() {
		return this.provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getTimezone() {
		return this.timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
}
