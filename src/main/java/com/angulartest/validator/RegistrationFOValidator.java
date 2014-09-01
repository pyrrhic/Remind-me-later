package com.angulartest.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.angulartest.model.RegistrationFO;

@Component
public class RegistrationFOValidator implements Validator {

	private final int MIN_EMAIL_LENGTH = 5;
	private final int MAX_EMAIL_LENGTH = 45;
	
	private final String EMAIL_INVALID = "Please enter a valid email.";
	private final String EMAIL_TOO_SHORT = "Minimum password length is " + MIN_EMAIL_LENGTH + ".";
	private final String EMAIL_TOO_LONG = "Maximum password length is " + MAX_EMAIL_LENGTH + ".";
	
	private final int MIN_PASSWORD_LENGTH = 1;
	private final int MAX_PASSWORD_LENGTH = 40;
	
	private final String PASSWORD_TOO_SHORT = "Minimum password length is " + MIN_PASSWORD_LENGTH + ".";
	private final String PASSWORD_TOO_LONG = "Maximum password length is " + MAX_PASSWORD_LENGTH + ".";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return RegistrationFOValidator.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		RegistrationFO registrationFO = (RegistrationFO) obj;
		
		String email = registrationFO.getEmail();
		if (!EmailValidator.getInstance().isValid(email)) {
			errors.rejectValue("email", "", EMAIL_INVALID);
		}
		else if (email.length() < MIN_EMAIL_LENGTH) {
			errors.rejectValue("email", "", EMAIL_TOO_SHORT);
		}
		else if(email.length() > MAX_EMAIL_LENGTH) {
			errors.rejectValue("email", "", EMAIL_TOO_LONG);
		}
		
		String password = registrationFO.getPassword();
		if (password.length() < 1) {
			errors.rejectValue("password",  "", PASSWORD_TOO_SHORT);
		}
		else if (password.length() > 40) {
			errors.rejectValue("password", "", PASSWORD_TOO_LONG);
		}
	}
}
