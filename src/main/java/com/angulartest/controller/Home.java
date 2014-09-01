package com.angulartest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.angulartest.model.RegistrationFO;
import com.angulartest.model.Reminder;
import com.angulartest.model.ReminderFO;
import com.angulartest.service.ProvidersService;
import com.angulartest.service.RegistrationService;
import com.angulartest.service.ReminderService;
import com.angulartest.validator.RegistrationFOValidator;
import com.angulartest.validator.ReminderFOValidator;

@RestController
public class Home {
	private ReminderService addReminderService;
	private ReminderFOValidator reminderFOValidator;
	private RegistrationFOValidator registrationFOValidator;
	private RegistrationService registrationService;
	
	@Autowired
	public Home(ReminderService addReminderService, ReminderFOValidator reminderFOValidator, 
			    RegistrationService registrationService, RegistrationFOValidator registrationFOValidator) {
		this.addReminderService = addReminderService;
		this.reminderFOValidator = reminderFOValidator;
		this.registrationFOValidator = registrationFOValidator;
		this.registrationService = registrationService;
	}
	
	@RequestMapping(value="/addReminder", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public List<FieldError> addReminder(@RequestBody ReminderFO reminderFO, BindingResult result) {
		reminderFOValidator.validate(reminderFO, result);
		
		if (!result.hasErrors()) {
			Reminder reminder = reminderFO.convertReminderFOToReminder();
			addReminderService.addReminder(reminder);
		}
		
		return result.getFieldErrors();
	}
	
	@RequestMapping(value="/getProviders", method=RequestMethod.GET)
	public String[] addReminder() {
		return ProvidersService.getInstance().getProviders();
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public List<FieldError> register(@RequestBody RegistrationFO registrationFO, BindingResult result) {
		registrationFOValidator.validate(registrationFO, result);
		
		String email = registrationFO.getEmail();
		if (!result.hasErrors() && !registrationService.isEmailRegistered(email)) {
			registrationService.register(registrationFO);
		}
		
		return result.getFieldErrors();
	}
}
