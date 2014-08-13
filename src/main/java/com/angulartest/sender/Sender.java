package com.angulartest.sender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.angulartest.dao.MonthTable;
import com.angulartest.dao.NoSendListDAO;
import com.angulartest.dao.Providers;
import com.angulartest.dao.ReminderDAO;
import com.angulartest.model.Reminder;
import com.angulartest.utilities.MyConstants;

public class Sender implements Runnable {	
	private final String appendedMessage = "\ntxt 'STOP' to end";
	
	private Providers providers;
	private NoSendListDAO noSendListDAO;
	
	private boolean isRunning;
	private ServletContext context;
	public Sender(ServletContext context, Providers providers) {
		this.context = context;
		this.providers = providers;
	}   
	
	public void run()
	{
		//some process to fire off emails that were somehow missed?
		
		// only pull 1000 emails at a time? so i dont run out of memory.
		isRunning = true;
		while(isRunning) {			
			Mail mail = new Mail();	
			updateNoSendList(mail);
			
			Calendar cal = new GregorianCalendar();
			TimeZone tz = TimeZone.getTimeZone(MyConstants.SERVER_TIMEZONE);
			cal.setTimeZone(tz);
			
			String date = getDate(cal);			
			String monthTable = getMonthTable(date);
			String dateTime = date + " " + getTime(cal).substring(0, 6) + "00";
			
			//System.out.println("getting messages for sending - " + dateTime);
			send(monthTable, dateTime, mail);

			sleepUntilNextTimeIncrement(cal);
		}
		
		System.out.println(Thread.currentThread().getName() + " is shutting down.");
	}

	private void updateNoSendList(Mail mail) {		
		List<String> contactsToAddToNoSendList = mail.updateNoSendList();
		
		WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(context);
		noSendListDAO = (NoSendListDAO) servletContext.getBean("noSendListDAO");
		
		for (String contact : contactsToAddToNoSendList) {
			if (!noSendListDAO.isContactOnNoSendList(contact)) {
				noSendListDAO.addToNoSendList(contact);
			}
		}
	}

	private String getMonthTable(String date) {
		int month = Integer.parseInt(date.substring(5, 7));
		return MonthTable.getMonthTableName(month);
	}
	
	private void send(String monthTable, String dateTime, Mail mail) {
		WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(context);	
		ReminderDAO reminderDao = (ReminderDAO) servletContext.getBean("reminderDAO");
		
		List<Reminder> messages = reminderDao.getRemindersToSend(monthTable, dateTime);
		//System.out.println("num messages:" + messages.size());

		for(Reminder msgObj: messages) {					
			String provider = msgObj.getProvider();
			String email = providers.getEmailForProvider(provider);
			String sendAddr = msgObj.getCellNumber() + email;
			String msg = msgObj.getMessage();
			
			msg = msg + appendedMessage;
			
			if (noSendListDAO.isContactOnNoSendList(msgObj.getCellNumber())) {
				System.out.println("Attempted to send message to a number on the 'no send list': " + msgObj.getCellNumber() + " msg:" + msg);
			}
			else {
				System.out.println("Sending- " + " sendAddr: " + sendAddr + " msg: " + msg);
				mail.send(msg, sendAddr);	
			}
			
			reminderDao.setRemindersAsSent(msgObj);
		}	
	}
	
	private void sleepUntilNextTimeIncrement(Calendar cal) {
		String time = getTime(cal);
		int seconds = Integer.parseInt(time.substring(6));
		
		int timeIncrementInSeconds = 60;
		long wait = 0;
		long milliTime = seconds * 1000;
		if(seconds <= timeIncrementInSeconds){
			wait = timeIncrementInSeconds * 1000 - milliTime;
		}
		
		//System.out.println("SLEEPING. currTime = " + time + " sleepTime = " + (wait/1000) + " seconds");
		
		try
			{Thread.sleep(wait);} 
		catch (InterruptedException e) {
			e.printStackTrace();
			isRunning = false;
		}
	}
	
	private String getDate(Calendar cal) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setCalendar(cal);
		return dateFormat.format(cal.getTime());
	}
	
	private String getTime(Calendar cal) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		timeFormat.setCalendar(cal);
		String time = timeFormat.format(cal.getTime());
		return time;
	}
}
