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

import com.angulartest.dao.ReminderDAO;
import com.angulartest.model.Reminder;
import com.angulartest.model.ReminderFO;
import com.angulartest.service.ProvidersService;
import com.angulartest.service.ReminderService;
import com.angulartest.validator.ReminderFOValidator;

@RestController
public class Home {
	private ReminderService addReminderService;
	
	@Autowired
	public Home(ReminderService addReminderService) {
		this.addReminderService = addReminderService;
	}
	
	@RequestMapping(value="/addReminder", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public List<FieldError> addReminder(@RequestBody ReminderFO reminderFO, BindingResult result) {
		addReminderService.addReminder(reminderFO, result);
		
		return result.getFieldErrors();
	}
	
	@RequestMapping(value="/getProviders", method=RequestMethod.GET)
	public String[] addReminder() {
		return ProvidersService.getInstance().getProviders();
	}
}
