package com.angulartest.validator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.angulartest.model.ReminderFO;
import com.angulartest.service.ProvidersService;
import com.angulartest.utility.ReminderDateFormatter;
import com.angulartest.utility.ReminderTimeFormatter;

@Component
public class ReminderFOValidator implements Validator {	
	private final int MAX_MESSAGE_LENGTH = 140;
	private final String GENERIC_ERROR_MESSAGE = "Please refer to the example for the correct usage of this field.";

	@Override
	public boolean supports(Class<?> clazz) {
		return ReminderFO.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		ReminderFO reminderFO = (ReminderFO)obj;
		
		String cleanedMobileNumber = cleanMobileNumber(reminderFO.getMobileNumber());
		reminderFO.setMobileNumber(cleanedMobileNumber);
		
		if (!isMobileNumberValid(reminderFO.getMobileNumber())) {
			errors.rejectValue("mobileNumber", "", GENERIC_ERROR_MESSAGE);
		}
		
		if (!isValidProvider(reminderFO.getProvider())) {
			errors.rejectValue("provider", "", GENERIC_ERROR_MESSAGE);
		}
		
		if (!isDateTimeValid(reminderFO.getDateTime(), reminderFO.getTimezone())) {
			errors.rejectValue("dateTime", "", GENERIC_ERROR_MESSAGE);
		}
		
		if (!isTimezoneValid(reminderFO.getTimezone())) {
			errors.rejectValue("timezone", "", GENERIC_ERROR_MESSAGE);
		}
		
		if (!isMessageValid(reminderFO.getMessage())) {
			errors.rejectValue("message", "", GENERIC_ERROR_MESSAGE);
		}
	}
	
	private boolean isTimezoneValid(String timezone) {
		boolean isValid = !isBlankOrEmpty(timezone);
		
		if (isValid) {
			isValid = DateTimeZone.getAvailableIDs().contains(timezone);
		}
		
		return isValid;
	}

	private boolean isMessageValid(String message) {
		return !(isBlankOrEmpty(message) || message.length() > MAX_MESSAGE_LENGTH);
	}

	private String cleanMobileNumber(String mobileNumber) {
		return mobileNumber.replaceAll("[^0-9]", "");
	}
	
	private boolean isMobileNumberValid(String mobileNumber) {		
		boolean isValid = !isBlankOrEmpty(mobileNumber);
		
		if (isValid) {
			isValid = (mobileNumber.length() == 10);
		}
		
		
		return isValid;
	}
	
	private boolean isValidProvider(String provider) {
		boolean isValid = !isBlankOrEmpty(provider);
		
		if (isValid) {
			isValid = (!ProvidersService.getInstance().getEmailForProvider(provider).equals("")) ? true : false;
		}
		
		return isValid;
	}
	
	private boolean isDateTimeValid(String dateTime, String timezone) {
		boolean isDateTimeValid = !isBlankOrEmpty(dateTime);
		
		if (isDateTimeValid) {
			ReminderDateFormatter reminderDateFormatter = new ReminderDateFormatter();
			String formattedDate = reminderDateFormatter.getDateFromUserInput(dateTime, timezone);
			
			isDateTimeValid = !(StringUtils.isEmpty(formattedDate));
		}
		
		if (isDateTimeValid) {
			ReminderTimeFormatter reminderTimeFormatter = new ReminderTimeFormatter();
			String formattedTime = reminderTimeFormatter.getTimeFromUserDateTime(dateTime);
			
			isDateTimeValid = !(StringUtils.isEmpty(formattedTime));
		}
		
		return isDateTimeValid;
	}
	
	private boolean isBlankOrEmpty(String str) {
		return (StringUtils.isEmpty(str) || StringUtils.isBlank(str));
	}
}
