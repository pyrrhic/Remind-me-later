package com.angulartest.validator;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.angulartest.model.ReminderFO;

public class ReminderFOValidatorTest {
	private ReminderFOValidator reminderFOValidator = new ReminderFOValidator();
	private ReminderFO reminderFO;
	private BindingResult errors;
	
	@Before
	public void setup() {
		reminderFO = new ReminderFO();
		errors = new BeanPropertyBindingResult(reminderFO, ReminderFO.class.getName());
	}
	
	@Test
	public void validateAllEmptyFieldsFail() {
		ReminderFO reminderFO = new ReminderFO();
			
		reminderFOValidator.validate(reminderFO, errors);
		
		final int expectedNumberErrors = 5;
		assertTrue("Expected: " + expectedNumberErrors + " errors. Actual: " + errors.getFieldErrorCount(), errors.getFieldErrorCount() == expectedNumberErrors);
	}
}
