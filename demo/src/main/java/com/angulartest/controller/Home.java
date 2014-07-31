package com.angulartest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.angulartest.dao.Providers;
import com.angulartest.dao.ReminderDAO;
import com.angulartest.model.Reminder;
import com.angulartest.model.ReminderFO;
import com.angulartest.validator.ReminderFOValidator;

@RestController
public class Home {
	private ReminderDAO reminderDAO;
	
	@Autowired
	public Home(ReminderDAO reminderDAO) {
		this.reminderDAO = reminderDAO;
	}
	
	@RequestMapping(value="/addReminder", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public void addReminder(@RequestBody ReminderFO reminderFO, BindingResult result) {
		ReminderFOValidator reminderFOValidator = new ReminderFOValidator();
		reminderFOValidator.validate(reminderFO, result);
		
		
		Reminder reminder = reminderFO.convertReminderFOToReminder();
		reminderDAO.addReminder(reminder, "anonymous@anonymous.com");
	}
	
	@RequestMapping(value="/getProviders", method=RequestMethod.GET)
	public String[] addReminder() {
		return Providers.getInstance().getProviders();
	}
}
