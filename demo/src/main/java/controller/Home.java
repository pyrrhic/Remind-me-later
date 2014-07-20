package controller;

import model.Reminder;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

	@RequestMapping(value="/addReminder", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public void addReminder(@RequestBody Reminder reminder) {
		System.out.println(reminder.getTimezone());
		System.out.println(reminder.getMessage());
		System.out.println(reminder.getProvider());
	}
}
