package com.angulartest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.angulartest.dao.ReminderDAO;
import com.angulartest.model.Reminder;
import com.angulartest.model.ReminderFO;
import com.angulartest.validator.ReminderFOValidator;

@Service	
public class ReminderService {
	 
	private ReminderDAO reminderDAO;
	
	@Autowired
	public ReminderService(ReminderDAO reminderDAO) {
		this.reminderDAO = reminderDAO;
	}
	
	public void addReminder(ReminderFO reminderFO, BindingResult result) {
		ReminderFOValidator reminderFOValidator = new ReminderFOValidator();
		reminderFOValidator.validate(reminderFO, result);
		
		if (!result.hasErrors()) {
			Reminder reminder = reminderFO.convertReminderFOToReminder();
			
			if (!reminderDAO.doesReminderExist(reminder)) {
				reminderDAO.addReminder(reminder, "anonymous@anonymous.com");	
			}
			else {
				System.out.println(this.getClass().getSimpleName() + ": A user tried to add the same reminder multiple times. Contact: " + reminder.getCellNumber() + " Msg:" + reminder.getMessage() + " DateTime:" + reminder.getDate() + " " + reminder.getTime());
			}
		}
	}
}
