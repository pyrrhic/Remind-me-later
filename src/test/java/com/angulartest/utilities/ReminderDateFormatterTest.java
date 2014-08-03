package com.angulartest.utilities;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class ReminderDateFormatterTest {
	ReminderDateFormatter dateFormatter = new ReminderDateFormatter();
	
	@Test
	public void getLongDateFromUserInputSuccess() {
		final String DATETIME_INPPUT = "7/30/2014 10:00pm";
		final String TIMEZONE_INPUT = MyConstants.SERVER_TIMEZONE;
		
		String formattedDate = dateFormatter.getDateFromUserInput(DATETIME_INPPUT, TIMEZONE_INPUT);
		
		final String EXPECTED_DATE = "2014-7-30";
		assertTrue("Expected: " + EXPECTED_DATE + " but got " + formattedDate, formattedDate.equals(EXPECTED_DATE));
	}
	
	@Test
	public void getShortDateFromUserInputSuccess() {
		final String dateTimeInput = "today 10:00pm";
		final String timezoneInput = MyConstants.SERVER_TIMEZONE;
		
		String formattedDate = dateFormatter.getDateFromUserInput(dateTimeInput, timezoneInput);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
		Date date = new Date();
		String today = dateFormat.format(date);
		
		assertTrue("Expected: " + today + " but got " + formattedDate, formattedDate.equals(today));
	}
}
