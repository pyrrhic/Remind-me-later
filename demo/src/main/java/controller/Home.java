package controller;

import model.Reminder;
import model.ReminderFO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dao.ReminderDAO;

@RestController
public class Home {

	@Autowired
	ReminderDAO reminderDAO;
	
	@RequestMapping(value="/addReminder", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public void addReminder(@RequestBody ReminderFO reminderFO) {
		Reminder reminder = reminderFO.convertReminderFOToReminder();
		reminderDAO.addReminder(reminder, "anonymous@anonymous.com");
	}
}
